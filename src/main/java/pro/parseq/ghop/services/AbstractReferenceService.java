package pro.parseq.ghop.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.GenomicCoordinateUtils;

public abstract class AbstractReferenceService implements ReferenceService {

	protected abstract String getSequence(GenomicCoordinate coord, int count);

	@Override
	public DispersedSequence getSequence(GenomicCoordinate coord, int prefixSize, int suffixSize) {
		return disperse(coord, prefixSize, suffixSize);
	}

	@Override
	public GenomicCoordinate shiftCoordinate(GenomicCoordinate coord, int offset) {

		Contig contig = coord.getContig();
		long contigLength = Objects.requireNonNull(contig.getLength(),
				String.format("Contig's length has not been provided for: %s", contig));

		if (offset == 0) {
			return coord;
		}

		List<Contig> contigs = getContigs(coord.getContig().getReferenceGenome().getId());
		int contigIdx = contigs.indexOf(contig);
		if ((coord.getCoord() + offset) < 0) {

			if (contigIdx == 0) {
				return GenomicCoordinateUtils.firstCoordinateOf(contig);
			}

			// Hop to the 'previous' contig
			Contig prev = contigs.get(contigIdx - 1);
			Objects.requireNonNull(prev.getLength(),
					String.format("Contig's length has not been provided for: %s", prev));

			return shiftCoordinate(GenomicCoordinateUtils.lastCoordinateOf(prev), (int) (offset + coord.getCoord() + 1));
		}
		if ((coord.getCoord() + offset) > contigLength) {

			if (contigIdx == (contigs.size() - 1)) {
				return GenomicCoordinateUtils.lastCoordinateOf(contig);
			}

			// Hop to the 'next' contig
			Contig next = contigs.get(contigIdx + 1);
			Objects.requireNonNull(next.getLength(),
					String.format("Contig's length has not been provided for: %s", next));

			return shiftCoordinate(GenomicCoordinateUtils.firstCoordinateOf(next), (int) (coord.getCoord() + offset - contigLength - 1));
		}

		return new GenomicCoordinate(contig, coord.getCoord() + offset);
	}

	/**
	 * Construct a {@link DispersedSequence}, that covers specified region
	 * 
	 * @param coord Bearing {@link GenomicCoordinate}
	 * @param prefixSize Positions to store before the bearing coordinate
	 * @param suffixSize Positions to store after the bearing coordinate
	 * @return {@link DispersedSequence}
	 */
	private DispersedSequence disperse(GenomicCoordinate coord, int prefixSize, int suffixSize) {

		List<Contig> contigs = getContigs(coord.getContig().getReferenceGenome().getId());
		GenomicCoordinate fragmentsStart = leftGapAwareShift(coord, prefixSize);
		GenomicCoordinate fragmentsEnd = rightGapAwareShift(coord, suffixSize + (GenomicCoordinateUtils.isContigRightMost(coord) ? 0 : 1));
		List<ContigSequence> fragments = new ArrayList<>();
		List<Contig> fragmentsContigs = contigs.subList(
				contigs.indexOf(fragmentsStart.getContig()),
				contigs.indexOf(fragmentsEnd.getContig()) + 1);

		Iterator<Contig> it = fragmentsContigs.iterator();
		Contig current = it.next();
		while (!fragmentsStart.getContig().equals(fragmentsEnd.getContig())) {

			GenomicCoordinate contigEnd = GenomicCoordinateUtils.lastCoordinateOf(current);
			fragments.add(new ContigSequence(fragmentsStart, contigEnd,
					getSequence(fragmentsStart, (int) (contigEnd.getCoord() - fragmentsStart.getCoord()))));

			current = it.next();
			fragmentsStart = GenomicCoordinateUtils.firstCoordinateOf(current);
		}

		fragments.add(new ContigSequence(fragmentsStart, fragmentsEnd,
				getSequence(fragmentsStart, (int) (fragmentsEnd.getCoord() - fragmentsStart.getCoord()))));

		return new DispersedSequence(fragments);
	}

	protected GenomicCoordinate leftGapAwareShift(GenomicCoordinate coord, int leftShift) {

		Contig contig = coord.getContig();
		Objects.requireNonNull(contig.getLength(),
				String.format("Contig's length has not been provided for: %s", contig));

		if (leftShift == 0) {
			return coord;
		}

		if ((coord.getCoord() - leftShift) >= 0) {
			return new GenomicCoordinate(contig, coord.getCoord() - leftShift);
		}

		List<Contig> contigs = getContigs(coord.getContig().getReferenceGenome().getId());
		int contigIdx = contigs.indexOf(contig);
		if (contigIdx == 0) {
			return GenomicCoordinateUtils.firstCoordinateOf(contig);
		}

		Contig nextLeftContig = contigs.get(contigIdx - 1);
		Objects.requireNonNull(nextLeftContig.getLength(),
				String.format("Contig's length has not been provided for: %s", nextLeftContig));

		return leftGapAwareShift(GenomicCoordinateUtils.decrementCoordinate(GenomicCoordinateUtils.lastCoordinateOf(nextLeftContig)),
				(int) (leftShift - coord.getCoord() - 1));
	}

	protected GenomicCoordinate rightGapAwareShift(GenomicCoordinate coord, int rightShift) {

		Contig contig = coord.getContig();
		long contigLength = Objects.requireNonNull(contig.getLength(),
				String.format("Contig's length has not been provided for: %s", contig));

		if (rightShift == 0) {
			return coord;
		}

		if ((coord.getCoord() + rightShift) <= contigLength) {
			return new GenomicCoordinate(contig, coord.getCoord() + rightShift);
		}

		List<Contig> contigs = getContigs(coord.getContig().getReferenceGenome().getId());
		int contigIdx = contigs.indexOf(contig);
		if (contigIdx == (contigs.size() - 1)) {
			return GenomicCoordinateUtils.lastCoordinateOf(contig);
		}

		Contig nextRightContig = contigs.get(contigIdx + 1);
		Objects.requireNonNull(nextRightContig.getLength(),
				String.format("Contig's length has not been provided for: %s", nextRightContig));

		return rightGapAwareShift(GenomicCoordinateUtils.firstCoordinateOf(nextRightContig),
				(int) (coord.getCoord() + rightShift - contigLength));
	}
}
