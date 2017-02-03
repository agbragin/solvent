package pro.parseq.ghop.exceptions;

public class UnknownAttributeFilterAggregateException extends RuntimeException {

	private static final long serialVersionUID = -4086222820579320980L;

	private final Long id;

	public UnknownAttributeFilterAggregateException(Long id) {

		super(String.format("Attribute filter aggregate id: %d is not found",
				id));

		this.id = id;
	}

	public Long getId() {
		return id;
	}
}
