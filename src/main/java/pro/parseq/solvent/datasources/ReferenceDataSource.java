package pro.parseq.solvent.datasources;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pro.parseq.solvent.datasources.attributes.Attribute;
import pro.parseq.solvent.datasources.filters.FilterQuery;
import pro.parseq.solvent.entities.Contig;
import pro.parseq.solvent.entities.NucleotideBand;
import pro.parseq.solvent.entities.Track;
import pro.parseq.solvent.services.ContigSequence;
import pro.parseq.solvent.services.ReferenceService;
import pro.parseq.solvent.utils.GenomicCoordinate;
import pro.parseq.solvent.utils.GenomicCoordinateUtils;
import pro.parseq.solvent.utils.IdGenerationUtils;
import pro.parseq.solvent.utils.Nucleotide;

public class ReferenceDataSource implements DataSource<NucleotideBand> {

	private static final Set<Attribute<?>> attributes = new HashSet<>();

	private final long id;
	private ReferenceService referenceService;

	private final Track track;

	public ReferenceDataSource(Track track, ReferenceService referenceService) {

		this.id = IdGenerationUtils.generateDataSourceId();
		this.track = track;
		this.referenceService = referenceService;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public DataSourceType getType() {
		return DataSourceType.REFERENCE;
	}

	@Override
	public DataSource<NucleotideBand> filter(FilterQuery query) {
		throw new UnsupportedOperationException("Reference data source can't be filtered");
	}

	@Override
	public Set<Attribute<?>> attributes() {
		return attributes;
	}

	@Override
	public Set<NucleotideBand> getBands(GenomicCoordinate coord, int left, int right) {

		return Stream
				.concat(
						this.coverage(coord).stream(),
						Stream.concat(
								this.leftBordersGenerants(left, coord).stream(),
								this.rightBordersGenerants(right, coord).stream()))
				.collect(Collectors.toSet());
	}

	protected List<GenomicCoordinate> leftBorders(int count, GenomicCoordinate coord) {

		if (count == 0) {
			return Arrays.asList(coord);
		}

		return generateBorders(referenceService.shiftCoordinate(coord, -count), coord);
	}

	protected List<GenomicCoordinate> rightBorders(int count, GenomicCoordinate coord) {

		if (count == 0) {
			return Arrays.asList(coord);
		}

		return generateBorders(coord, referenceService.shiftCoordinate(coord, count));
	}

	protected Set<NucleotideBand> borderGenerants(GenomicCoordinate coord) {

		completeWithContigLength(coord);
		List<ContigSequence> fragments;
		if (!GenomicCoordinateUtils.isContigExternal(coord)) {
			fragments = referenceService.getSequence(coord, 1, 0).getFragments();
		} else {
			if (GenomicCoordinateUtils.isContigLeftmost(coord)) {
				fragments = referenceService.getSequence(coord, 0, 0).getFragments();
			} else {
				fragments = referenceService.getSequence(GenomicCoordinateUtils.decrementCoordinate(coord), 0, 0).getFragments();
			}
		}

		ContigSequence sequence = fragments.get(fragments.size() - 1);

		return retrieveGenerants(sequence);
	}

	protected Set<NucleotideBand> coverage(GenomicCoordinate coord) {
		// Same as border generatnts
		return borderGenerants(coord);
	}

	protected Set<NucleotideBand> leftBordersGenerants(int borderCount, GenomicCoordinate coord) {

		return leftBorders(borderCount, coord).stream()
				.map(this::borderGenerants)
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

	protected Set<NucleotideBand> rightBordersGenerants(int borderCount, GenomicCoordinate coord) {

		return rightBorders(borderCount, coord).stream()
				.map(this::borderGenerants)
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

	private Set<NucleotideBand> retrieveGenerants(ContigSequence sequence) {

		Contig contig = sequence.getContig();
		long startCoord = sequence.startCoord().getCoord();

		return Stream.iterate(0, i -> i + 1)
				.limit(sequence.length())
				.map(i -> new NucleotideBand(track,
						new GenomicCoordinate(contig, startCoord + i),
						new GenomicCoordinate(contig, startCoord + i + 1),
						Nucleotide.valueOf(sequence.sequence().substring(i, i + 1))))
				.collect(Collectors.toSet());
	}

	private void completeWithContigLength(GenomicCoordinate coord) {

		Contig contig = coord.getContig();
		contig.setLength(referenceService
				.getContigLength(contig.getReferenceGenome().getId(), contig.getId()));
	}

	private List<GenomicCoordinate> generateBorders(GenomicCoordinate from, GenomicCoordinate to) {

		if (from.getContig().equals(to.getContig())) {

			return Stream.iterate(from.getCoord(), coord -> coord + 1)
					.limit(to.getCoord() - from.getCoord() + 1)
					.map(coord -> new GenomicCoordinate(from.getContig(), coord))
					.collect(Collectors.toList());
		}

		List<Contig> contigs = referenceService.getContigs(from.getContig().getReferenceGenome().getId());
		Contig next = contigs.get(contigs.indexOf(from.getContig()) + 1);
		long contigLastCoord = Objects.requireNonNull(from.getContig().getLength(),
				String.format("Contig's length has not been provided for: %s", from.getContig()));

		return Stream.concat(
						Stream.iterate(from.getCoord(), coord -> coord + 1)
								.limit(contigLastCoord - from.getCoord() + 1)
								.map(coord -> new GenomicCoordinate(from.getContig(), coord)),
						generateBorders(GenomicCoordinateUtils.firstCoordinateOf(next), to).stream())
				.collect(Collectors.toList());
	}
}
