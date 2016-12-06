package pro.parseq.ghop.rest;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.ghop.data.Band;
import pro.parseq.ghop.data.GenomicCoordinate;
import pro.parseq.ghop.data.Query;
import pro.parseq.ghop.data.source.MasterDataSource;

@RestController
@RequestMapping("/bands")
public class BandController {

	@Autowired
	private MasterDataSource masterDataSource;

	@GetMapping
	public Set<Band> getBands(@RequestParam("genome") String referenceGenome,
			@RequestParam("contig") String contig,
			@RequestParam("coord") long coord,
			@RequestParam("left") int left, @RequestParam("right") int right,
			@RequestParam("layers") Set<String> layers) throws ServletRequestBindingException {

		for (String layer: layers) {
			if (!masterDataSource.getLayers().contains(layer)) {
				throw new ServletRequestBindingException(String
						.format("No data source associated with layer '%s' yet", layer));
			}
		}

		GenomicCoordinate genomicCoord = new GenomicCoordinate(referenceGenome, contig, coord);
		Query query = new Query(genomicCoord, left, right, layers);

		return masterDataSource.getBands(query);
	}
}
