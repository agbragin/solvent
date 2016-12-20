package pro.parseq.ghop.services;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import pro.parseq.ghop.configs.RefserviceConfig;
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.ReferenceGenomeNotFoundException;

/**
 * {@link ReferenceService} implementation based on refservice web-service
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 */
@Component
public class ReferenceWebService implements ReferenceService {

	@Autowired
	private RefserviceConfig config;

	private RestTemplate restTemplate = new RestTemplate();

	@Override
	@Cacheable("referenceGenomes")
	public Set<ReferenceGenome> getReferenceGenomes() {

		ParameterizedTypeReference<List<ReferenceGenome>> responseType =
				new ParameterizedTypeReference<List<ReferenceGenome>>() {};
		ResponseEntity<List<ReferenceGenome>> response = restTemplate
				.exchange(referencesUri(), HttpMethod.GET, null, responseType);

		return new HashSet<>(response.getBody());
	}

	@Override
	@Cacheable("referenceGenomes")
	public List<Contig> getContigs(ReferenceGenome referenceGenome) {

		try {

			ParameterizedTypeReference<List<Contig>> responseType =
					new ParameterizedTypeReference<List<Contig>>() {};
			ResponseEntity<List<Contig>> response = restTemplate
					.exchange(referenceUri(referenceGenome), HttpMethod.GET, null, responseType);

			return response.getBody();
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				throw new ReferenceGenomeNotFoundException(referenceGenome);
			} else {
				throw e;
			}
		}
	}

	private URI referenceUri(ReferenceGenome referenceGenome) {

		return refserviceRoot()
				.pathSegment(config.getReferencesEndpoint())
				.pathSegment(referenceGenome.getId())
				.build().encode()
				.toUri();
	}

	private URI referencesUri() {

		return refserviceRoot()
				.pathSegment(config.getReferencesEndpoint())
				.build().encode()
				.toUri();
	}

	private UriComponentsBuilder refserviceRoot() {

		return UriComponentsBuilder.newInstance()
				.scheme(config.getConnectionScheme())
				.host(config.getConnectionHost())
				.port(config.getConnectionPort())
				.path(config.getApiRoot())
				.pathSegment(config.getApiVersion());
	}
}
