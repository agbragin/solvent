package pro.parseq.ghop.rest;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.entities.ReferenceGenomeContigs;
import pro.parseq.ghop.services.ReferenceService;
import pro.parseq.ghop.utils.HateoasUtils;

@RestController
@RequestMapping("/references")
public class ReferenceController {

	@Autowired
	private ReferenceService referenceService;

	@GetMapping
	public Resources<Resource<ReferenceGenome>> getReferenceGenomes() {
		return HateoasUtils.referenceGenomeResources(referenceService.getReferenceGenomes());
	}

	@RequestMapping("/{referenceGenome:.+}")
	public Resource<ReferenceGenomeContigs> getReferenceGenome(
			@PathVariable ReferenceGenome referenceGenome) {

		List<String> contigIds = referenceService.getContigs(referenceGenome.getId())
				.stream().map(Contig::getId).collect(Collectors.toList());

		return HateoasUtils.referenceGenomeContigsResource(referenceGenome, contigIds);
	}
}
