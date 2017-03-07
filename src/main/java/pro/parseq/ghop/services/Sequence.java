package pro.parseq.ghop.services;

import pro.parseq.ghop.exceptions.CoordinateOutOfBoundsException;
import pro.parseq.ghop.utils.GenomicCoordinate;

/**
 * Abstraction of a reference genome nucleotide sequence
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 */
public interface Sequence {

	/**
	 * Sequence start position (1-based, inclusive)
	 * 
	 * @return {@link GenomicCoordinate} representing sequence's start position
	 */
	GenomicCoordinate startCoord();

	/**
	 * Sequence end position (1-based, exclusive)
	 * 
	 * @return {@link GenomicCoordinate} representing sequence's end position
	 */
	GenomicCoordinate endCoord();

	/**
	 * Nucleotide sequence
	 * 
	 * @return {@link String} representation of nucleotide sequence
	 */
	String sequence();

	/**
	 * Nucleotide subsequence
	 * 
	 * @param startCoord Subsequence start {@link GenomicCoordinate} (1-based, inclusive)
	 * @param endCoord Subesequence end {@link GenomicCoordinate} (1-based, exclusive)
	 * @return {@link String} representation of nucleotide subsequence
	 * @throws CoordinateOutOfBoundsException if specified substring coordinates are out of original sequence range
	 */
	String substring(GenomicCoordinate startCoord, GenomicCoordinate endCoord);

	/**
	 * @return Sequence length
	 */
	long length();
}
