package regex;

import java.util.ArrayList;

class CharClass extends Or {

	CharClass(ArrayList<Token> tokens) {
		if (tokens.size() == 0 || tokens.get(0).getType() != TokenType.ClassOpen)
			throw new IllegalArgumentException("No open char class token");
		tokens.remove(0);
		
		// Special cases for first character in character class
		if (tokens.size() == 0)
			throw new InvalidRegexException("character class not closed");
		if (tokens.get(0).getType() == TokenType.ClassClose) {
			tokens.get(0).toLiteral();
		}

		// Add the rest of the tokens to MATCHERS until encountering a CLASSCLOSE Token
		while (tokens.size() > 1 && tokens.get(0).getType() != TokenType.ClassClose) {
			TokenType type = tokens.get(0).getType();
			switch(type) {
				case Literal:
					matchers.add(new Literal(tokens));
					break;
				case ClassOpen:
					matchers.add(new CharClass(tokens));
					break;
				default:
					tokens.get(0).toLiteral();
			}

		}

		// Check that a ']' was found to close the class
		if (tokens.size() > 0 && tokens.get(0).getType() == TokenType.ClassClose)
			tokens.remove(0);
		else
			throw new InvalidRegexException("character class not closed");
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		for (Matcher m : matchers)
			sb.append(m.toString());
		sb.append(']');
		return sb.toString();
	}
}
