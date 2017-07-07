package regex;

import java.util.ArrayList;

enum TokenType { Literal, ClassOpen, ClassClose }

/* Represent different kinds of symbols in a regular expression. The REGEX constructor 
 * transforms the String representation of a regex pattern into an ArrayList of tokens,
 * which is then used by MATCHER.PARSE to create a list of matchers that will find 
 * portions of text that match a given regular expression. */
class Token {
	private char c;
	private TokenType type;

	// Constructor for creating LITERAL tokens
	Token(char c) {
		this.c = c;
		type = TokenType.Literal;
	}

	// Constructor for creating all other tokens
	Token(TokenType t) {
		this.type = t;
		switch (type) {
			case ClassOpen:
				this.c = '[';
				break;
			case ClassClose:
				this.c = ']';
				break;
			case Literal:
				throw new IllegalArgumentException("Must provide char for literal tokens.");
			default:
				throw new IllegalArgumentException("unrecognized token");
		}
	}

	char getChar() {
		return c;
	}

	TokenType getType() {
		return type;
	}

	void toLiteral() {
		type = TokenType.Literal;
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
				case '[':
					tokens.add(new Token(TokenType.ClassOpen));
					break;
				case ']':
					tokens.add(new Token(TokenType.ClassClose));
					break;
				case '\\':
					tokenizeEscapes(pattern, tokens);
					break;
				default:
					tokens.add(new Token(c));
			}
		}

		return tokens;
	}

	/* Creates Literal tokens from characters in PATTERN. Removes those
	 * characters from PATTERN and adds the tokens to TOKENS. */
	private static void tokenizeEscapes (ArrayList<Character> pattern, ArrayList<Token> tokens) {
		if (pattern.isEmpty()) // no character after backslash
			throw new InvalidRegexException("missing character after '\\'");

		String hexStr = "";
		int codePoint = 0;
		char c = pattern.remove(0);
		switch (c) {
			case '0':
				/* Expects octal value n, nn, or mnn where 0 <= n <= 7, 
				 * and 0 <= m <= 3 */
				boolean valid = false;
				int octVal = 0;
				try {
					for (int i = 0; i < 3 && octVal < 040; i++) {
						int n = Integer.parseInt(pattern.get(0) + "", 8);
						octVal = octVal * 8 + n;
						pattern.remove(0);
						valid = true;
					}
				} catch (IndexOutOfBoundsException | NumberFormatException e) {
				}

				if (!valid)
					throw new InvalidRegexException("invalid octal value");
				tokens.add(new Token((char)octVal));
				break;
			case 'x':
				/* Expects unicode character as hexadecimal in one of two formats*/
				hexStr = "";
				try {
					// remove hex digits from PATTERN and append them to HEXSTR
					char c1 = pattern.remove(0);
					if (c1 == '{') {
						// Expects {h...h} where h is a hex digit
						int idx = pattern.indexOf('}');
						if (idx == -1)
							throw new InvalidRegexException("missing '}' after hexadecimal value");
						while (idx > 0) {
							hexStr += pattern.remove(0);
							idx--;
						}
						pattern.remove(0);
					} else {
						// Expects hh where h is a hex digit
						hexStr += c1;
						hexStr += pattern.remove(0);
					}
					
					// parse HEXSTR to char(s) and add corresponding tokens
					codePoint = Integer.parseInt(hexStr, 16);
					if (!Character.isValidCodePoint(codePoint))
						throw new InvalidRegexException("invalid hexadecimal value");
					char[] chars = Character.toChars(codePoint);
					for (char c2 : chars)
						tokens.add(new Token(c2));
				} catch (IndexOutOfBoundsException | NumberFormatException e) {
					throw new InvalidRegexException("invalid hexadecimal value");
				}

				break;
			case 'u':
				/* Expects four digit hexadecimal value */
				hexStr = "";
				codePoint = 0;
				try {
					for (int i = 0; i < 4; i++)
						hexStr += pattern.remove(0);
					codePoint = Integer.parseInt(hexStr, 16);
				} catch (IndexOutOfBoundsException | NumberFormatException e) {
					throw new InvalidRegexException("invalid hexadecimal value");
				}
				tokens.add(new Token((char)codePoint));
				break;
			case 'c':
				/* Expects A-Z or a-z for ASCII control character. */
				try {
					char ctrlChar = pattern.remove(0);
					if (ctrlChar < 'A' || ctrlChar > 'z' || (ctrlChar > 'Z' && ctrlChar < 'a'))
						throw new InvalidRegexException("invalid control character");
					// convert a-z to A-Z, then convert A-Z to 0x01 - 0x1A
					tokens.add(new Token((char)(Character.toUpperCase(ctrlChar) - '@')));
				} catch (IndexOutOfBoundsException e) {
					throw new InvalidRegexException("missing control character");
				}
				break;
			case 't':
				tokens.add(new Token('\t'));
				break;
			case 'n':
				tokens.add(new Token('\n'));
				break;
			case 'r':
				tokens.add(new Token('\r'));
				break;
			case 'f':
				tokens.add(new Token('\f'));
				break;
			case 'a':
				tokens.add(new Token('\u0007'));
				break;
			case 'e':
				tokens.add(new Token('\u001B'));
				break;
			case 'Q':
				/* turn characters in PATTERN into Literal tokens until finding
				 * "\E" or until end of PATTERN */
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
				/* If escaped character has no special meaning, treat as a literal. */
				tokens.add(new Token(c));
		}
	}
}

