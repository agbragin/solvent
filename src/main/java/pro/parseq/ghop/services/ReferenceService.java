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
}
