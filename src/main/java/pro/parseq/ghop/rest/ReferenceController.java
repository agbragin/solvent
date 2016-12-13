package pro.parseq.ghop.rest;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.ghop.data.service.Contig;
import pro.parseq.ghop.data.service.ReferenceGenome;
import pro.parseq.ghop.data.service.ReferenceService;
import pro.parseq.ghop.data.utils.ReferenceGenomeContigs;

@RestController
@RequestMapping("/references")
public class ReferenceController {

	@Autowired
	private ReferenceService referenceService;

	@GetMapping
	public Resources<Resource<ReferenceGenome>> getReferenceGenomes() {
		return referenceGenomeResources(referenceService.getReferenceGenomes());
	}

	@RequestMapping("/{referenceGenome:.+}")
	public Resource<ReferenceGenomeContigs> getReferenceGenome(
			@PathVariable ReferenceGenome referenceGenome) {

		return referenceGenomeContigsResource(referenceGenome,
				referenceService.getContigs(referenceGenome));
	}

	private static final Resource<ReferenceGenomeContigs> referenceGenomeContigsResource(
			ReferenceGenome referenceGenome, List<Contig> contigs) {

		Link selfLink = linkTo(methodOn(ReferenceController.class)
				.getReferenceGenome(referenceGenome)).withSelfRel();
		List<String> contigIds = contigs.stream().map(contig -> contig.getId())
				.collect(Collectors.toList());

		return new Resource<>(new ReferenceGenomeContigs(contigIds), selfLink);
	}

	private static final Resource<ReferenceGenome> referenceGenomeResource(
			ReferenceGenome referenceGenome) {

		Link selfLink = linkTo(methodOn(ReferenceController.class)
				.getReferenceGenome(referenceGenome)).withSelfRel();

		return new Resource<>(referenceGenome, selfLink);
	}

	private static final Resources<Resource<ReferenceGenome>> referenceGenomeResources(Set<ReferenceGenome> referenceGenomes) {

		Link selfLink = linkTo(methodOn(ReferenceController.class)
				.getReferenceGenomes()).withSelfRel();
		Set<Resource<ReferenceGenome>> referenceGenomeResources = referenceGenomes.stream()
				.map(referenceGenome -> referenceGenomeResource(referenceGenome))
				.collect(Collectors.toSet());

		return new Resources<>(referenceGenomeResources, selfLink);
	}
}
