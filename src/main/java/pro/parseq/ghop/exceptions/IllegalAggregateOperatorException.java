package pro.parseq.ghop.exceptions;

import java.util.Arrays;

import pro.parseq.ghop.datasources.filters.AggregateOperator;

public class IllegalAggregateOperatorException extends RuntimeException {

	private static final long serialVersionUID = 7595714742964744636L;

	private final String operator;

	public IllegalAggregateOperatorException(String operator) {

		super(String.format("Illegal aggregate operator: %s; available are: %s",
				operator, Arrays.asList(AggregateOperator.values())));

		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}
}
