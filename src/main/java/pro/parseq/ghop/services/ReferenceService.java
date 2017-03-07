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

import java.util.List;
import java.util.Set;

import pro.parseq.ghop.entities.Contig;
import pro.parseq.ghop.entities.ReferenceGenome;
import pro.parseq.ghop.exceptions.UnknownContigException;
import pro.parseq.ghop.utils.GenomicCoordinate;

/**
 * Service contract to access reference genomes' information
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 */
public interface ReferenceService {

	/**
	 * Returns a set of available reference genomes
	 * 
	 * @return {@link Set} of available {@link ReferenceGenome}
	 */
	Set<ReferenceGenome> getReferenceGenomes();

	/**
	 * Lists contigs for specified reference genome (order is critical - it would be used to compare genomic coordinates)
	 * 
	 * @param referenceGenomeName Reference genome to list contigs for
	 * @return {@link List} of {@link Contig} for specified reference genome
	 * @throws ReferenceGenomeNotFoundException if specified reference genome is not in the set of available reference genomes
	 */
	List<Contig> getContigs(String referenceGenomeName);

	/**
	 * Returns information about contig's length
	 * 
	 * @param referenceGenomeName Reference genome to list contigs for
	 * @param contigId Contig to get length of
	 * @return Contig's length
	 * @throws ReferenceGenomeNotFoundException if specified reference genome is not in the set of available reference genomes
	 * @throws UnknownContigException if specified contig is not in the list of reference genome's contigs
	 */
	long getContigLength(String referenceGenomeName, String contigId);

	/**
	 * Retrieves reference genome's sequence for specified parameters
	 * (return value is bounded by "the leftmost" and "the rightmost"
	 * coordinates of the current reference genome)
	 * 
	 * @param coord Bearing {@link GenomicCoordinate}
	 * @param prefixSize Number of nucleotides to retrieve before the bearing coordinate
	 * @param suffixSize Number of nucleotides to retrieve after the bearing coordinate
	 * @return {@link DispersedSequence} representing nucleotides sequence
	 */
	DispersedSequence getSequence(GenomicCoordinate coord, int prefixSize, int suffixSize);

	/**
	 * Determines genomic coordinate after offset apply to the specified
	 * (return value is bounded by "the leftmost" and "the rightmost"
	 * coordinates of the current reference genome)
	 * 
	 * @param coord {@link GenomicCoordinate} to apply {@code offset} to
	 * @param offset Offset to apply (could be both positive or negative)
	 * @return {@link GenomicCoordinate} after {@code offset} apply
	 */
	GenomicCoordinate shiftCoordinate(GenomicCoordinate coord, int offset);
}
