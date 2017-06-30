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
	static ArrayList<Token> tokenize (String pat) throws InvalidRegexException{
		ArrayList<Character> pattern = new ArrayList<Character>();
		for (int i = 0; i < pat.length(); i++) 
			pattern.add(pat.charAt(i));
		ArrayList<Token> tokens = new ArrayList<Token>();

		while (!pattern.isEmpty()) {
			char c = pattern.remove(0);
			switch(c) {
				case '\\':
					tokenizeEscapes(pattern, tokens);
					break;
				default:
					tokens.add(new Token(c));
			}
		}

		return tokens;
	}

	private static void tokenizeEscapes (ArrayList<Character> pattern, ArrayList<Token> tokens) {
		if (pattern.isEmpty())
			throw new InvalidRegexException("missing character after '\\'");

		char c = pattern.remove(0);
		switch (c) {
			case 'Q':
				while (!pattern.isEmpty()) {
					char cc = pattern.remove(0);
					if (cc == '\\') {
						if (!pattern.isEmpty() && pattern.get(0) == 'E') {
							pattern.remove(0);
							break;
						} else {
							tokens.add(new Token(cc));
						}
					} else {
						tokens.add(new Token(cc));
					}
				}
				break;
			case 'E':
				throw new InvalidRegexException("missing '\\Q' before '\\E'");
			default:
				tokens.add(new Token(c));
		}
	}
}

