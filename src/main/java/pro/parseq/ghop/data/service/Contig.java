package pro.parseq.ghop.data.service;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Relation(collectionRelation = "contigs")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Contig {

	private String id;
	private long length;

	protected Contig() {}

	public Contig(String id, long length) {
		this.id = id;
		this.length = length;
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
		return id.hashCode();
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

		return id.equals(((Contig) obj).id);
	}

	@Override
	public String toString() {
		return id;
	}
}
