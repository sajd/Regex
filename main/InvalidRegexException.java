package regex;

public class InvalidRegexException extends IllegalArgumentException{

	InvalidRegexException() {
	}

	InvalidRegexException(String msg) {
		super(msg);
	}

}
