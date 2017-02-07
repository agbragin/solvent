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
package pro.parseq.ghop.utils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.UnknownContigException;
import pro.parseq.ghop.exceptions.UnknownReferenceGenomeException;
import pro.parseq.ghop.services.ReferenceService;

@Component
public class GenomicCoordinateComparator implements Comparator<GenomicCoordinate> {

	@Autowired
	private ReferenceService referenceService;

	@Override
	public int compare(GenomicCoordinate o1, GenomicCoordinate o2) {

		int referenceGenomesComparisonResult = referenceGenomeNamesComparison(
				o1.getContig().getReferenceGenome().getId(),
				o2.getContig().getReferenceGenome().getId());
		if (referenceGenomesComparisonResult != 0) {
			return referenceGenomesComparisonResult;
		}
		int contigsComparisonResult = contigNamesComparison(
				o1.getContig().getId(), o2.getContig().getId(),
				o1.getContig().getReferenceGenome().getId());
		if (contigsComparisonResult != 0) {
			return contigsComparisonResult;
		}

		return ((Long) o1.getCoord()).compareTo(o2.getCoord());
	}

	private int referenceGenomeNamesComparison(String rg1, String rg2) {

		// This will validate specified genomes names and throw exception if fail
		referenceGenomeNamesValidation(rg1, rg2);

		return comparisonNormalizer(rg1.compareTo(rg2));
	}

	private int contigNamesComparison(String c1, String c2, String rg) {

		// This will validate specified contig names and throw exception if fail
		contigNamesValidation(rg, c1, c2);

		List<String> availableContigs = referenceService.getContigs(rg)
				.stream().map(Contig::getId).collect(Collectors.toList());
		Integer c1Idx = availableContigs.indexOf(c1);
		Integer c2Idx = availableContigs.indexOf(c2);

		return c1Idx.compareTo(c2Idx);
	}

	private void referenceGenomeNamesValidation(String... referenceGenomeNames) {
		Arrays.asList(referenceGenomeNames).stream().forEach(this::referenceGenomeNameValidation);
	}

	private void referenceGenomeNameValidation(String referenceGenomeName) {

		Set<String> availableReferenceGenomes = referenceService.getReferenceGenomes()
				.stream().map(ReferenceGenome::getId).collect(Collectors.toSet());
		if (!availableReferenceGenomes.contains(referenceGenomeName)) {
			throw new UnknownReferenceGenomeException(referenceGenomeName, availableReferenceGenomes);
		}
	}

	private void contigNamesValidation(String referenceGenomeName, String... contigNames) {
		Arrays.asList(contigNames).stream().forEach(contigName -> contigNameValidation(contigName, referenceGenomeName));
	}

	private void contigNameValidation(String contigName, String referenceGenomeName) {

		List<String> availableContigs = referenceService.getContigs(referenceGenomeName)
				.stream().map(Contig::getId).collect(Collectors.toList());
		if (!availableContigs.contains(contigName)) {
			throw new UnknownContigException(referenceGenomeName, contigName, availableContigs);
		}
	}

	private static final int comparisonNormalizer(int comparisonResult) {

		if (comparisonResult < 0) {
			return -1;
		}
		if (comparisonResult > 0) {
			return 1;
		}

		return 0;
	}
}
