package pro.parseq.ghop.data.service;

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

import pro.parseq.ghop.data.UnknownReferenceGenomeException;
import pro.parseq.ghop.data.utils.RefserviceProperties;

@Component
public class ReferenceWebService implements ReferenceService {

	private RestTemplate restTemplate = new RestTemplate();

	@Autowired
	private RefserviceProperties refserviceProperties;

	@Override
	@Cacheable("references")
	public Set<ReferenceGenome> getReferenceGenomes() {

		ParameterizedTypeReference<List<ReferenceGenome>> referenceGenomeSetType =
				new ParameterizedTypeReference<List<ReferenceGenome>>() {};
		ResponseEntity<List<ReferenceGenome>> response = restTemplate
				.exchange(referencesUri(), HttpMethod.GET, null, referenceGenomeSetType);

		return new HashSet<>(response.getBody());
	}

	@Override
	@Cacheable("contigs")
	public List<Contig> getContigs(ReferenceGenome referenceGenome) {

		try {

			ParameterizedTypeReference<List<Contig>> contigListType =
					new ParameterizedTypeReference<List<Contig>>() {};
			ResponseEntity<List<Contig>> response = restTemplate
					.exchange(referenceUri(referenceGenome), HttpMethod.GET, null, contigListType);

			return response.getBody();
		} catch (HttpStatusCodeException e) {
			if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
				throw new UnknownReferenceGenomeException(referenceGenome.getId());
			} else {
				throw e;
			}
		}
	}

	private URI referenceUri(ReferenceGenome referenceGenome) {

		return refserviceRoot()
				.pathSegment("references")
				.pathSegment(referenceGenome.getId())
				.build().encode().toUri();
	}

	private URI referencesUri() {

		return refserviceRoot()
				.pathSegment("references")
				.build().encode().toUri();
	}

	private UriComponentsBuilder refserviceRoot() {

		return UriComponentsBuilder.newInstance()
				.scheme(refserviceProperties.getScheme())
				.host(refserviceProperties.getHost())
				.port(refserviceProperties.getPort())
				.path(refserviceProperties.getRoot())
				.pathSegment(refserviceProperties.getVersion());
	}
}
