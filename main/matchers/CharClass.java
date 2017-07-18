package regex;

import java.util.ArrayList;

class CharClass extends Or {
	private boolean negated;

	CharClass(ArrayList<Token> tokens) {
		Token curr, next;
		if (tokens.size() == 0 || tokens.get(0).getType() != TokenType.ClassOpen)
			throw new IllegalArgumentException("No open char class token");
		tokens.remove(0);
		
		// ']', '-', '^' behave differently when they are the first character 
		// in the character class
		if (tokens.size() == 0)
			throw new InvalidRegexException("character class not closed");
		curr = tokens.get(0);
		// set NEGATED to true if '^' is first token, false otherwise
		if (curr.getType() == TokenType.Caret) {
			tokens.remove(0);
			curr = tokens.get(0);
			negated = true;
		} else {
			negated = false;
		}
		// if ']' or '-' are first token, change them to literals
		switch (curr.getType()) {
			case ClassClose:
			case Range:
				curr.toLiteral();
				break;
		}

		// Add the rest of the tokens to MATCHERS until encountering a CLASSCLOSE Token
		while (tokens.size() > 1 && curr.getType() != TokenType.ClassClose) {
			/* Check if the next token is a RANGE token. If so, construct a RANGE
			 * matcher from this token and the one after the RANGE token. If the RANGE
			 * constructor fails, change the RANGE token to a LITERAL. */
			next = tokens.get(1); // loop entered only if there exists at least 2 elements in tokens
			if (next.getType() == TokenType.Range) {
				try {
					Range range = new Range(curr, tokens.get(2));
					tokens.remove(0);
					tokens.remove(0);
					tokens.remove(0);
					matchers.add(range);
				} catch (ArrayIndexOutOfBoundsException | IllegalArgumentException e) {
					next.toLiteral();
				}
			} else {
				switch(curr.getType()) {
					case Literal:
						curr = tokens.remove(0);
						matchers.add(new Literal(curr));
						break;
					case ClassOpen:
						matchers.add(new CharClass(tokens));
						break;
					default:
						tokens.get(0).toLiteral();
				}
			}
			curr = tokens.get(0);
		}

		// Check that a ']' was found to close the class
		if (tokens.size() > 0 && curr.getType() == TokenType.ClassClose)
			tokens.remove(0);
		else
			throw new InvalidRegexException("character class not closed");
	}

	@Override
	public int matches(char[] text, int start) {
		int result = super.matches(text, start);
		if (negated) {
			if (result == -1 && start < text.length)
				return start + 1;
			else
				return -1;
		} else {
			return result;
		}
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('[');
		if (negated)
			sb.append('^');
		for (Matcher m : matchers)
			sb.append(m.toString());
		sb.append(']');
		return sb.toString();
	}
}
