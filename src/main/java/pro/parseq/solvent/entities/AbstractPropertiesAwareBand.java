package pro.parseq.solvent.entities;

import java.util.Arrays;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import pro.parseq.solvent.utils.GenomicCoordinate;
import pro.parseq.solvent.utils.PropertiesAware;

public abstract class AbstractPropertiesAwareBand extends AbstractBand implements PropertiesAware {

	private final static Logger logger = LoggerFactory.getLogger(AbstractPropertiesAwareBand.class);
	
	private static final long serialVersionUID = -25765442137549700L;
	
	private static final ObjectMapper objectMapper = new ObjectMapper();
	
	private byte[] propertiesBytes;

	public AbstractPropertiesAwareBand(Track track,
			GenomicCoordinate startCoord, GenomicCoordinate endCoord,
			String name, JsonNode properties) {

		super(track, startCoord, endCoord, name);

		try {
			this.propertiesBytes = objectMapper.writeValueAsBytes(properties);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Malformed band properties", e); 
		}
	}
	
	@Override
	public JsonNode getProperties() {
		try {
			return objectMapper.readTree(propertiesBytes);
		} catch (Exception e) {
			throw new IllegalStateException("Error reading object properties", e);
		}
	}

	public void setProperties(JsonNode properties) {
		try {
			this.propertiesBytes = objectMapper.writeValueAsBytes(properties);
		} catch (JsonProcessingException e) {
			throw new IllegalArgumentException("Malformed band properties", e); 
		}
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AbstractPropertiesAwareBand)) {
			return false;
		}
		
		BedBand other = (BedBand) obj; 
		
		return getTrack().equals(other.getTrack())
				&& getStartCoord().equals(other.getStartCoord())
				&& getEndCoord().equals(other.getEndCoord())
				&& getName().equals(other.getName())
				&& getProperties().equals(other.getProperties());
	}
	
	@Override
	public int hashCode() {
		logger.debug("Build hash for: {}", this);
		return new HashCodeBuilder(21, 81)
					.append(getTrack())
					.append(getStartCoord())
					.append(getEndCoord())
					.append(getName())
					.append(getProperties())
					.toHashCode();
		
	}
}
