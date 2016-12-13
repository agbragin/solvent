package pro.parseq.ghop.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import pro.parseq.ghop.data.Track;
import pro.parseq.ghop.data.source.BedFileDataSourceFactory;
import pro.parseq.ghop.data.source.MasterDataSource;

@RestController
@RequestMapping("/tracks")
public class TrackController {

	@Autowired
	private MasterDataSource masterDataSource;

	@Autowired
	private BedFileDataSourceFactory bedFileDataSourceFactory;

	@GetMapping
	public Resources<Resource<Track>> getTracks() {
		return trackResources(masterDataSource.getTracks());
	}

	/**
	 * Using @RequestMapping instead of @GetMapping as while
	 * there is no support for the latter in ControllerLinkBuilder,
	 * so resources end up with inappropriate self links
	 * (/tracks, but not /tracks/{track_name})
	 * 
	 * https://github.com/spring-projects/spring-hateoas/issues/471
	 */
	@RequestMapping("/{track}")
	public Resource<Track> getTrack(@PathVariable Track track) {

		if (!masterDataSource.getTracks().contains(track)) {
			throw new TrackNotFoundException(track.getName());
		}

		return trackResource(track);
	}

	/**
	 * see previous method comment
	 */
	@RequestMapping(method = RequestMethod.POST, value = "/{bed}")
	public Resource<Track> createTrack(@RequestParam Track track,
			@RequestParam MultipartFile bed, @RequestParam String genome) {

		try {

			masterDataSource.addDataSource(
					bedFileDataSourceFactory.newInstance(track, bed.getInputStream(), genome));

			return getTrack(track);
		} catch (IOException e) {
			throw new RuntimeException(String.format(
					"I/O exception while BED file manipultaions: %s", e.getMessage()));
		}
	}

	@DeleteMapping("/{track}")
	public Resource<Track> removeTrack(@PathVariable Track track) {

		if (!masterDataSource.getTracks().contains(track)) {
			throw new TrackNotFoundException(track.getName());
		}

		return trackResource(masterDataSource.removeDataSource(track));
	}

	@DeleteMapping
	public Resources<Resource<Track>> removeAll() {
		return trackResources(masterDataSource.removeAll());
	}

	private static final Resources<Resource<Track>> trackResources(Set<Track> tracks) {

		Link selfLink = linkTo(methodOn(TrackController.class)
				.getTracks()).withSelfRel();
		Set<Resource<Track>> trackResources = tracks.stream()
				.map(track -> trackResource(track)).collect(Collectors.toSet());

		return new Resources<>(trackResources, selfLink);
	}

	private static final Resource<Track> trackResource(Track track) {

		Link selfLink = linkTo(methodOn(TrackController.class)
				.getTrack(track)).withSelfRel();

		return new Resource<>(track, selfLink);
	}
}
