package pro.parseq.ghop.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.exceptions.CoordinateOutOfBoundsException;
import pro.parseq.ghop.utils.GenomicCoordinate;
import pro.parseq.ghop.utils.PredicateUtils;

public class DispersedSequence implements Sequence {

	private final GenomicCoordinate startCoord;
	private final GenomicCoordinate endCoord;

	private final List<ContigSequence> fragments;

	public DispersedSequence(List<ContigSequence> fragments) {

		ContigSequence firstFragment = fragments.get(0);
		ContigSequence lastFragment = fragments.get(fragments.size() - 1);

		this.startCoord = firstFragment.startCoord();
		this.endCoord = lastFragment.endCoord();
		this.fragments = fragments;
	}

	public List<ContigSequence> getFragments() {
		return fragments;
	}

	public DispersedSequence dispersedSubstring(GenomicCoordinate startCoord, GenomicCoordinate endCoord) {

		ContigSequence startFragment = findFragmentOf(fragments, startCoord.getContig());
		if (startFragment == null) {
			throw new CoordinateOutOfBoundsException(startCoord, this.startCoord, this.endCoord);
		}
		ContigSequence endFragment = findFragmentOf(fragments, endCoord.getContig());
		if (endFragment == null) {
			throw new CoordinateOutOfBoundsException(endCoord, this.startCoord, this.endCoord);
		}

		if (startCoord.equals(endCoord)) {
			return new DispersedSequence(new ArrayList<>());
		}

		if (startFragment.equals(endFragment)) {
			return new DispersedSequence(Arrays.asList(startFragment.contigSubsequence(startCoord, endCoord)));
		}

		ContigSequence startFragmentSubseq = startFragment.contigSubsequence(startCoord);
		ContigSequence endFragmentSubseq = endFragment.contigSubsequence(endFragment.startCoord(), endCoord);
		List<ContigSequence> interFragments = fragments.subList(
				fragments.indexOf(startFragment) + 1,
				fragments.indexOf(endFragment));

		return new DispersedSequence(Stream.concat(
						Stream.concat(Stream.of(startFragmentSubseq), interFragments.stream()),
						Stream.of(endFragmentSubseq))
				.collect(Collectors.toList()));
	}

	@Override
	public GenomicCoordinate startCoord() {
		return startCoord;
	}

	@Override
	public GenomicCoordinate endCoord() {
		return endCoord;
	}

	@Override
	public String sequence() {
		return sequence(fragments);
	}

	@Override
	public String substring(GenomicCoordinate startCoord, GenomicCoordinate endCoord) {
		return dispersedSubstring(startCoord, endCoord).sequence();
	}

	@Override
	public long length() {
		return sequence().length();
	}

	@Override
	public String toString() {
		return sequence();
	}

	private static final ContigSequence findFragmentOf(List<ContigSequence> fragments, Contig contig) {
		return fragments.stream().filter(PredicateUtils.isFragmentOf(contig)).findFirst().orElse(null);
	}

	private static final String sequence(List<ContigSequence> fragments) {

		return fragments.stream()
				.map(ContigSequence::sequence)
				.reduce(new String(), String::concat);
	}
}
