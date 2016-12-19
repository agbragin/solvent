package pro.parseq.ghop.entities;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Relation(collectionRelation = "referenceGenomes")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceGenome {

	private String id;

	protected ReferenceGenome() {}

	public ReferenceGenome(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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
		if (!(obj instanceof ReferenceGenome)) {
			return false;
		}

		return id.equals(((ReferenceGenome) obj).id);
	}

	@Override
	public String toString() {
		return id;
	}
}
