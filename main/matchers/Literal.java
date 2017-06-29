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

	int matches (char[] text, int textIdx) {
		if (textIdx < 0)
			throw new IllegalArgumentException("negative textIdx");
		
		int patternIdx = 0;

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
		temp.append(chars);
		return temp.toString();
	}

}
