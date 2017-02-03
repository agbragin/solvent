package pro.parseq.ghop.exceptions;

import java.util.Arrays;

import pro.parseq.ghop.datasources.DataSourceType;

public class IllegalDataSourceTypeException extends RuntimeException {

	private static final long serialVersionUID = -7716363815707968776L;

	private final String type;

	public IllegalDataSourceTypeException(String type) {

		super(String.format("Illegal data source type: %s; available are: %s",
				type, Arrays.asList(DataSourceType.values())));

		this.type = type;
	}

	public String getType() {
		return type;
	}
}
