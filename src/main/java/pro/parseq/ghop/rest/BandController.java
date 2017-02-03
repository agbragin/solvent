package pro.parseq.ghop.rest;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.ghop.datasources.MasterDataSource;
import pro.parseq.ghop.datasources.QueryForBands;
import pro.parseq.ghop.entities.Band;
import pro.parseq.ghop.utils.DataSourceUtils;
import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.HateoasUtils;

@RestController
@RequestMapping("/bands")
public class BandController {

	@Autowired
	private MasterDataSource masterDataSource;

	@Autowired
	private DataSourceUtils dataSourceUtils;

	@GetMapping
	public Resources<? extends Band> getBands(@RequestParam("genome") String referenceGenomeName,
			@RequestParam("contig") String contigName, @RequestParam long coord,
			@RequestParam int left, @RequestParam int right,
			@RequestParam("dataSources") Set<String> dataSourceUris) {

		QueryForBands query = new QueryForBands(
				new GenomicCoordinate(referenceGenomeName, contigName, coord),
				left, right, dataSourceUris.stream()
						.map(dataSourceUtils::retrieveDataSourceByUri)
						.collect(Collectors.toSet()));

		return HateoasUtils.bandResources(masterDataSource.getBands(query), query);
	}
}
