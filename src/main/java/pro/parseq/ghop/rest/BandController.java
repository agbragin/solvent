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

import pro.parseq.ghop.datasources.MasterDataSource;
import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.entities.Track;
import pro.parseq.ghop.exceptions.TrackNotFoundException;
import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.Query;

@RestController
@RequestMapping("/bands")
public class BandController {

	@Autowired
	private MasterDataSource masterDataSource;

	// TODO: add tracks filtration and correlation
	@GetMapping
	public Resources<Band> getBands(
			@RequestParam("genome") ReferenceGenome referenceGenome,
			@RequestParam("contig") String contigId, @RequestParam long coord,
			@RequestParam int left, @RequestParam int right,
			@RequestParam Set<Track> tracks) {

		for (Track track: tracks) {
			if (!masterDataSource.getTracks().contains(track)) {
				throw new TrackNotFoundException(track);
			}
		}

		Contig contig = new Contig(referenceGenome, contigId);
		GenomicCoordinate genomicCoord = new GenomicCoordinate(contig, coord);
		Query query = new Query(genomicCoord, left, right, tracks);

		return bandResources(masterDataSource.getBands(query), query);
	}

	private static final Resources<Band> bandResources(Set<Band> bands, Query query) {

		Link selfLink = linkTo(methodOn(BandController.class)
				.getBands(query.getCoord().getContig().getReferenceGenome(),
						query.getCoord().getContig().getId(),
						query.getCoord().getCoord(),
						query.getLeft(), query.getRight(),
						query.getTrackSettings().getTracks()))
				.withSelfRel();

		return new Resources<>(bands, selfLink);
	}
}
