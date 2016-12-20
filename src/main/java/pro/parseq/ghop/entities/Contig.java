package pro.parseq.ghop.entities;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Contig {

	/**
	 * Can't use here something like this: @JsonUnwrapped(prefix = "referenceGenome")
	 * due to: https://github.com/FasterXML/jackson-databind/issues/1467
	 * 
	 * TODO: change if it would be fixed
	 */
	private ReferenceGenome referenceGenome;

	private String id;
	private long length;

	protected Contig() {}

	public Contig(ReferenceGenome referenceGenome, String id) {
		this.referenceGenome = referenceGenome;
		this.id = id;
	}

	public Contig(ReferenceGenome referenceGenome, String id, long length) {

		this.referenceGenome = referenceGenome;
		this.id = id;
		this.length = length;
	}

	@JsonCreator
	public Contig(@JsonProperty("referenceGenomeId") String referenceGenomeId,
			@JsonProperty("id") String id, @JsonProperty("length") long length) {

		referenceGenome = new ReferenceGenome(referenceGenomeId);
		this.id = id;
		this.length = length;
	}

	public ReferenceGenome getReferenceGenome() {
		return referenceGenome;
	}

	public void setReferenceGenome(ReferenceGenome referenceGenome) {
		this.referenceGenome = referenceGenome;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Contig)) {
			return false;
		}

		return referenceGenome.equals(((Contig) obj).referenceGenome)
				&& id.equals(((Contig) obj).id);
	}

	@Override
	public String toString() {
		return new StringBuilder()
				.append(referenceGenome)
				.append(":")
				.append(id)
				.toString();
	}
}
