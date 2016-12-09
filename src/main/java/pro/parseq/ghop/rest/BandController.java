package pro.parseq.ghop.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.ghop.data.Band;
import pro.parseq.ghop.data.GenomicCoordinate;
import pro.parseq.ghop.data.Query;
import pro.parseq.ghop.data.Track;
import pro.parseq.ghop.data.source.MasterDataSource;

@RestController
@RequestMapping("/bands")
public class BandController {

	@Autowired
	private MasterDataSource masterDataSource;

	// TODO: add tracks filtration and correlation
	@GetMapping
	public Resources<Band> getBands(@RequestParam String genome,
			@RequestParam String contig, @RequestParam long coord,
			@RequestParam int left, @RequestParam int right,
			@RequestParam Set<Track> tracks) {

		for (Track track: tracks) {
			if (!masterDataSource.getTracks().contains(track)) {
				throw new TrackNotFoundException(track.getName());
			}
		}

		GenomicCoordinate genomicCoord = new GenomicCoordinate(genome, contig, coord);
		Query query = new Query(genomicCoord, left, right, tracks);

		return bandResources(masterDataSource.getBands(query), query);
	}

	private static final Resources<Band> bandResources(Set<Band> bands, Query query) {

		Link selfLink = linkTo(methodOn(BandController.class)
				.getBands(query.getCoord().getReferenceGenome(),
						query.getCoord().getContig(),
						query.getCoord().getCoord(),
						query.getLeft(), query.getRight(),
						query.getTrackSettings().getTracks()))
				.withSelfRel();

		return new Resources<>(bands, selfLink);
	}
}
