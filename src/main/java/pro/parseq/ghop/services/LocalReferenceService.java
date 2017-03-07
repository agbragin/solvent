package pro.parseq.ghop.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import pro.parseq.GenomeExplorer.ReferenceExplorer;
import pro.parseq.GenomeExplorer.exceptions.ContigNotFoundException;
import pro.parseq.GenomeExplorer.exceptions.ReferenceNotFoundException;
import pro.parseq.GenomeExplorer.utils.ReferenceSequenceCase;
import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.ReferenceGenomeNotFoundException;
import pro.parseq.ghop.exceptions.UnknownContigException;
import pro.parseq.ghop.utils.GenomicCoordinate;

public class LocalReferenceService extends AbstractReferenceService {

	private ReferenceExplorer referenceExplorer;

	public LocalReferenceService(ReferenceExplorer referenceExplorer) {
		this.referenceExplorer = referenceExplorer;
	}

	@Override
	protected String getSequence(GenomicCoordinate coord, int count) {

		return referenceExplorer.getReferenceSequence(
				coord.getContig().getReferenceGenome().getId(),
				coord.getContig().getId(),
				coord.getCoord() + 1, Math.min(coord.getCoord() + count, coord.getContig().getLength()),
				ReferenceSequenceCase.UPPER);
	}

	@Override
	public Set<ReferenceGenome> getReferenceGenomes() {

		return referenceExplorer.getReferenceGenomesList().stream()
				.map(id -> new ReferenceGenome(id))
				.collect(Collectors.toSet());
	}

	@Override
	public List<Contig> getContigs(String referenceGenomeName) {

		try {

			List<Contig> contigs = getContigNames(referenceGenomeName).stream()
					.map(id -> new Contig(referenceGenomeName, id))
					.collect(Collectors.toList());
			contigs.stream().forEach(contig -> contig.setLength(referenceExplorer
					.getContigLength(referenceGenomeName, contig.getId())));

			return contigs;
		} catch (ReferenceNotFoundException e) {
			throw new ReferenceGenomeNotFoundException(referenceGenomeName);
		}
	}

	@Override
	public long getContigLength(String referenceGenomeName, String contigId) {

		try {
			return referenceExplorer.getContigLength(referenceGenomeName, contigId);
		} catch (ReferenceNotFoundException e) {
			throw new ReferenceGenomeNotFoundException(referenceGenomeName);
		} catch (ContigNotFoundException e) {
			throw new UnknownContigException(referenceGenomeName, contigId, getContigNames(referenceGenomeName));
		}
	}

	private List<String> getContigNames(String referenceGenomeName) {
		return referenceExplorer.getReferenceContigsList(referenceGenomeName);
	}
}
