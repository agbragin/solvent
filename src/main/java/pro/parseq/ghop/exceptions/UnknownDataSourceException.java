package pro.parseq.ghop.exceptions;

public class UnknownDataSourceException extends RuntimeException {

	private static final long serialVersionUID = 8197101807905892787L;

	private final String id;

	public UnknownDataSourceException(String id) {

		super(String.format("Data source of id: %s is not found", id));

		this.id = id;
	}

	public String getId() {
		return id;
	}
}
