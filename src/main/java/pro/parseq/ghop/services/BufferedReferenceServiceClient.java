package pro.parseq.ghop.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.CoordinateOutOfBoundsException;
import pro.parseq.ghop.exceptions.UnknownContigException;
import pro.parseq.ghop.utils.GenomicCoordinate;

public class BufferedReferenceServiceClient extends AbstractReferenceService {

	private static final Logger logger = LoggerFactory.getLogger(BufferedReferenceServiceClient.class);

	// TODO: move to application configuration
	private static final int BUF_DECORATOR_HALF_SIZE = 100;

	private Set<ReferenceGenome> references;
	private Map<String, List<Contig>> contigMapping = new HashMap<>();

	protected DispersedSequence bufferedSequence;

	private ReferenceService referenceService;

	public BufferedReferenceServiceClient(ReferenceService referenceService) {
		this.referenceService = referenceService;
	}

	@Override
	public Set<ReferenceGenome> getReferenceGenomes() {

		if (references == null) {
			references = referenceService.getReferenceGenomes();
		}

		return references;
	}

	@Override
	public List<Contig> getContigs(String referenceGenomeName) {

		if (!contigMapping.containsKey(referenceGenomeName)) {
			contigMapping.put(referenceGenomeName, referenceService.getContigs(referenceGenomeName));
		}

		return contigMapping.get(referenceGenomeName);
	}

	@Override
	public long getContigLength(String referenceGenomeName, String contigId) {

		if (!contigMapping.containsKey(referenceGenomeName)) {
			contigMapping.put(referenceGenomeName, referenceService.getContigs(referenceGenomeName));
		}

		return contigMapping.get(referenceGenomeName).stream()
				.filter(contig -> contig.getId().equals(contigId))
				.findFirst()
				.orElseThrow(() -> new UnknownContigException(
						referenceGenomeName, contigId,
						contigMapping.get(referenceGenomeName).stream()
								.map(Contig::getId)
								.collect(Collectors.toList())))
				.getLength();
	}

	@Override
	protected String getSequence(GenomicCoordinate coord, int count) {

		logger.debug("Requesting reference genome sequence of length {} from {}", count, coord);

		if (bufferedSequence == null) {
			logger.debug("Initializing buffer");
			bufferedSequence = referenceService.getSequence(coord, BUF_DECORATOR_HALF_SIZE, BUF_DECORATOR_HALF_SIZE);
		}
		logger.debug("Buffer holds the sequence from {} to {}",
				bufferedSequence.startCoord(), bufferedSequence.endCoord());

		GenomicCoordinate retrievalEnd = shiftCoordinate(coord, count);
		logger.debug("Requesting sequence end is determined and equals: {}", retrievalEnd);

		try {
			return bufferedSequence.dispersedSubstring(coord, retrievalEnd).sequence();
		} catch (CoordinateOutOfBoundsException e) {

			logger.debug("Requesting sequence has not been buffered, fetching it");
			bufferedSequence = referenceService.getSequence(coord, BUF_DECORATOR_HALF_SIZE, BUF_DECORATOR_HALF_SIZE);

			return bufferedSequence.dispersedSubstring(coord, retrievalEnd).sequence();
		}
	}
}
