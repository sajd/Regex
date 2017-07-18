package regex;

import java.util.*;

/* Matches sequence of literal characters. */
class Literal extends Matcher{
	private char[] chars;

	/* Beginning with the first element, for every consecutive token in TOKENS that 
	 * represents a literal character, adds that character to CHARS and removes the token.
	 * Throws ILLEGALARGUMENTEXCEPTION if the first token does not represent a literal 
	 * character. */
	Literal (ArrayList<Token> tokens) {
		if (tokens.size() == 0 || tokens.get(0).getType() != TokenType.Literal)
			throw new IllegalArgumentException("No character token");

		int count = 0;
		while (count < tokens.size() && tokens.get(count).getType() == TokenType.Literal) {
			count++;
		}

		chars = new char[count];
		for (int i = 0; i < count; i++) {
			chars[i] = tokens.get(0).getChar();
			tokens.remove(0);
		}
	}

	/* Create a Literal Matcher that matches the single character in TOKEN, whether or
	 * not TOKEN.type has a value of LITERAL.*/
	Literal (Token token) {
		chars = new char[1];
		chars[0] = token.getChar();
	}

	int matches (char[] text, int textIdx) {
		if (textIdx < 0)
			throw new IllegalArgumentException("negative textIdx");
		
		int patternIdx = 0;

		/* Iterate through CHARS and TEXT and check that corresponding characters
		 * are the same. */
		while (patternIdx < chars.length && textIdx < text.length) {
			if (chars[patternIdx] != text[textIdx]) {
				break;
			} else {
				patternIdx++;
				textIdx++;
			}
		}

		if (patternIdx == chars.length) 
			return textIdx;
		else
			return -1;
	}

	public String toString () {
		StringBuilder temp = new StringBuilder();
		for (char c : chars) {
			switch(c) {
				case '\t':
					temp.append("\\t");
					break;
				case '\n':
					temp.append("\\n");
					break;
				case '\r':
					temp.append("\\r");
					break;
				case '\f':
					temp.append("\\f");
					break;
				case '\u0007':
					temp.append("\\a");
					break;
				case '\u001B':
					temp.append("\\e");
					break;
				case '\\':
				case '^':
				case '$':
				case '.':
				case '|':
				case '?':
				case '*':
				case '+':
				case '(':
				case ')':
				case '[':
				case ']':
				case '{':
				case '}':
					temp.append('\\');
					temp.append(c);
					break;
				default:
					if (Character.isWhitespace(c) && c != ' ') {
						String str = Integer.toHexString((int)c);
						while (str.length() < 4)
							str = "0" + str;
						temp.append("\\u" + str);
					} else {
						temp.append(c);
					}
			}
		}
		return temp.toString();
	}

}
