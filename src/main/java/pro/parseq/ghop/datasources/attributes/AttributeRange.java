package pro.parseq.ghop.datasources.attributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class AttributeRange<T extends Comparable<T>> {

	private final T lowerBound;
	private final T upperBound;

	private final InclusionType inclusionType;

	private final List<T> values;

	public AttributeRange(T lowerBound, T upperBound, InclusionType inclusionType) {

		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
		this.inclusionType = inclusionType;
		this.values = null;
	}

	public AttributeRange(T[] values) {
		this(Arrays.stream(values)
				.collect(Collectors.toList()));
	}

	/**
	 * Create attribute range for a list of values.
	 * 
	 * Since values are comparable lower and upper bound can be naturally defined.
	 * 
	 * @param values
	 */
	public AttributeRange(List<T> values) {

		Collections.sort(values);

		this.lowerBound = values.get(0);
		this.upperBound = values.get(values.size() - 1);
		this.inclusionType = InclusionType.CLOSED;
		this.values = values;
	}

	public T getLowerBound() {
		return lowerBound;
	}

	public T getUpperBound() {
		return upperBound;
	}

	public InclusionType getInclusionType() {
		return inclusionType;
	}

	public List<T> getValues() {
		return values;
	}
}
