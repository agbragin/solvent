/*******************************************************************************
 *     Copyright 2016-2017 the original author or authors.
 *
 *     This file is part of CONC.
 *
 *     CONC. is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Affero General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     CONC. is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU Affero General Public License for more details.
 *
 *     You should have received a copy of the GNU Affero General Public License
 *     along with CONC. If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package pro.parseq.ghop.services;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.ReferenceGenomeNotFoundException;
import pro.parseq.ghop.exceptions.UnknownContigException;
import pro.parseq.ghop.services.configs.RefserviceConfig;
import pro.parseq.ghop.utils.GenomicCoordinate;

/**
 * {@link ReferenceService} implementation based on refservice web-service
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 */
public class RemoteReferenceService extends AbstractReferenceService {

	private static final Logger logger = LoggerFactory.getLogger(RemoteReferenceService.class);

	private RefserviceConfig config;

	private RestTemplate restTemplate = new RestTemplate();

	public RemoteReferenceService(RefserviceConfig config) {
		this.config = config;
	}

	@Override
	protected String getSequence(GenomicCoordinate coord, int count) {

		logger.info("Perform an HTTP request for reference genome sequence of length {} starting from: {}",
				count, coord);

		ResponseEntity<JsonNode> response = restTemplate
				.exchange(sequenceUri(coord, count), HttpMethod.GET, null, JsonNode.class);
		String sequence = response.getBody().get("sequence").asText();
		logger.info("Got reference genome sequence of length {} starting from {}: {}",
				sequence.length(), coord, sequence);

		return sequence;
	}

	@Override
	public Set<ReferenceGenome> getReferenceGenomes() {

		logger.info("Perform an HTTP request for reference genome list");

		ParameterizedTypeReference<List<ReferenceGenome>> responseType =
				new ParameterizedTypeReference<List<ReferenceGenome>>() {};
		ResponseEntity<List<ReferenceGenome>> response = restTemplate
				.exchange(referencesUri(), HttpMethod.GET, null, responseType);

		return new HashSet<>(response.getBody());
	}

	@Override
	public List<Contig> getContigs(String referenceGenomeName) {

		logger.info("Perform an HTTP request for contigs list of referenc genome: {}",
				referenceGenomeName);

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

	@Override
	public long getContigLength(String referenceGenomeName, String contigId) {

		logger.info("Perform an HTTP request for reference genome '{}' contig '{}' length",
				referenceGenomeName, contigId);

		List<Contig> contigs = getContigs(referenceGenomeName);
		List<String> contigNames = contigs.stream()
				.map(Contig::getId)
				.collect(Collectors.toList());

		return contigs.stream()
				.filter(contig -> contig.getId().equals(contigId))
				.findFirst()
				.orElseThrow(() -> new UnknownContigException(
						referenceGenomeName, contigId, contigNames))
				.getLength();
	}

	private URI sequenceUri(GenomicCoordinate coord, int count) {

		return refserviceRoot()
				.pathSegment(config.getReferencesEndpoint())
				.pathSegment(coord.getContig().getReferenceGenome().getId())
				.path(coord.getContig().getId())
				.queryParam("start", coord.getCoord() + 1)
				.queryParam("stop", coord.getCoord() + count)
				.build().encode()
				.toUri();
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
