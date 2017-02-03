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
