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

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.ReferenceGenomeNotFoundException;
import pro.parseq.ghop.services.configs.RefserviceConfig;

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
	public List<Contig> getContigs(String referenceGenomeName) {

		try {

			ParameterizedTypeReference<List<Contig>> responseType =
					new ParameterizedTypeReference<List<Contig>>() {};
			ResponseEntity<List<Contig>> response = restTemplate
					.exchange(referenceUri(referenceGenomeName), HttpMethod.GET, null, responseType);

			return response.getBody();
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				throw new ReferenceGenomeNotFoundException(referenceGenomeName);
			} else {
				throw e;
			}
		}
	}

	private URI referenceUri(String referenceGenomeName) {

		return refserviceRoot()
				.pathSegment(config.getReferencesEndpoint())
				.pathSegment(referenceGenomeName)
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
