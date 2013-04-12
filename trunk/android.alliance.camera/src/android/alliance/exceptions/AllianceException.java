package android.alliance.exceptions;


public class AllianceException extends Exception {

	public AllianceExceptionType type;
	public String message;
	
	public AllianceException(Exception exception, String message, AllianceExceptionType type){
		super(exception);	
	
		this.message = message;
		this.type = type;
	}
	
	public AllianceException(String message, AllianceExceptionType type){
		
		this.message = message;
		this.type = type;
	}
}
