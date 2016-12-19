package pro.parseq.ghop.entities;

import java.util.List;

public class ReferenceGenomeContigs {

	private final List<String> contigs;

	public ReferenceGenomeContigs(List<String> contigs) {
		this.contigs = contigs;
	}

	public List<String> getContigs() {
		return contigs;
	}
}
