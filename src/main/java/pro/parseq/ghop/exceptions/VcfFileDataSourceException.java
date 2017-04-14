package pro.parseq.ghop.exceptions;

public class VcfFileDataSourceException extends RuntimeException {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4035443835893860573L;

	public VcfFileDataSourceException() {
		super();
	}
	
	public VcfFileDataSourceException(String message) {
		super(message);
	}
	
	public VcfFileDataSourceException(Exception exception) {
		super(exception);
	}

}
