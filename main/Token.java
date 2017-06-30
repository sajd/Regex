package regex;

import java.util.ArrayList;

enum TokenType { Literal }

/* Represent different kinds of symbols in a regular expression. The REGEX constructor 
 * transforms the String representation of a regex pattern into an ArrayList of tokens,
 * which is then used by MATCHER.PARSE to create a list of matchers that will find 
 * portions of text that match a given regular expression. */
class Token {
	private char c;
	private TokenType type;

	Token(char c) {
		this.c = c;
		type = TokenType.Literal;
	}

	char getChar() {
		return c;
	}

	TokenType getType() {
		return type;
	}

	/* RETURNS an ArrayList of tokens that represent the input PATTERN. */
	static ArrayList<Token> tokenize (String pattern) throws InvalidRegexException{
		ArrayList<Token> tokens = new ArrayList<Token>();

		for (int i = 0; i < pattern.length(); i++) {
			Token tok = new Token(pattern.charAt(i));
			tokens.add(tok);
		}

		return tokens;
	}
}

