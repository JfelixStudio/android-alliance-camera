package android.alliance.exceptions;

public interface OnException {

	public void onException(Exception exception, String message, AllianceExceptionType type);
}
