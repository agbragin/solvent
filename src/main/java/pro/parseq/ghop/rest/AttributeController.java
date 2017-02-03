package pro.parseq.ghop.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pro.parseq.ghop.datasources.MasterDataSource;
import pro.parseq.ghop.datasources.attributes.Attribute;
import pro.parseq.ghop.exceptions.AttributeNotFoundException;
import pro.parseq.ghop.utils.HateoasUtils;

@RestController
@RequestMapping("/attributes")
public class AttributeController {

	@Autowired
	private MasterDataSource masterDataSource;

	@GetMapping
	public Resources<Resource<Attribute<?>>> getAttributes() {
		return HateoasUtils.attributeResources(masterDataSource.getAttributes());
	}

	@RequestMapping("/{id}")
	public Resource<Attribute<?>> getAttribute(@PathVariable Long id) {

		Attribute<?> attribute = masterDataSource.getAttribute(id);
		if (attribute == null) {
			throw new AttributeNotFoundException(id);
		}

		return HateoasUtils.attributeResource(attribute);
	}
}
