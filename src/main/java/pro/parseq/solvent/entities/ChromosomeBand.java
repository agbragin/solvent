package pro.parseq.solvent.entities;

import org.springframework.hateoas.core.Relation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import pro.parseq.solvent.utils.GenomicCoordinate;

@Relation(collectionRelation = "bands")
public class ChromosomeBand extends AbstractPropertiesAwareBand {

	private static final String NAME_ATTRIBUTE = "name";

	private final Contig contig;

	public ChromosomeBand(Track track, Contig contig) {

		super(track, new GenomicCoordinate(contig, 0),
				new GenomicCoordinate(contig, contig.getLength()),
				contig.getId(), ChromosomeBand.getProperties(contig));

		this.contig = contig;
	}

	public Contig getContig() {
		return contig;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ChromosomeBand)) {
			return false;
		}

		return contig.equals(((ChromosomeBand) obj).contig);
	}

	public static final JsonNode getProperties(Contig contig) {

		ObjectNode properties = JsonNodeFactory.instance.objectNode();
		properties.put(NAME_ATTRIBUTE, contig.getId());

		return properties;
	}
}
