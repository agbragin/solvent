package pro.parseq.ghop.exceptions;

public class DataSourceNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -2313266025884838608L;

	private final long id;

	public DataSourceNotFoundException(long id) {

		super(String.format("DataSource of id: %d is not found", id));

		this.id = id;
	}

	public long getId() {
		return id;
	}
}
