package pro.parseq.ghop.entities;

import java.util.List;

/**
 * This is a service class to proper API JSON response serialization 
 * 
 * @author Alexander Afanasyev <a href="mailto:aafanasyev@parseq.pro">aafanasyev@parseq.pro</a>
 */
public class ReferenceGenomeContigs {

	// This would serialize a list of strings into 'contigs' array instead of default 'stringList'
	private final List<String> contigs;

	public ReferenceGenomeContigs(List<String> contigs) {
		this.contigs = contigs;
	}

	public List<String> getContigs() {
		return contigs;
	}
}
