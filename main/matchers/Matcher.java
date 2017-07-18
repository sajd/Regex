package regex;

import java.util.*;

/* Base class for different matcher classes. Each regular expression pattern type has an
 * associated subclass of MATCHER, with its own method for determining whether the pattern
 * matches a specified text. */
public abstract class Matcher {
	/* START is the index of TEXT at which to begin searching for a match.
	 * Returns the index up to which a match was found. If no match was found, -1 is
	 * returned. */
	abstract int matches (char[] text, int start);

	/* Takes as input the ArrayList of tokens created from the string representation of
	 * the regex pattern. Returns an ArrayList of Matchers that is a member of the REGEX
	 * object. */
	static ArrayList<Matcher> parse(ArrayList<Token> tokens) {
		ArrayList<Matcher> matchers = new ArrayList<Matcher>();

		while (tokens.size() > 0) {
			/* Uses the first element of TOKENS to determine which Matcher to construct and
			 * add to MATCHERS. Throws an ILLEGALSTATEEXCEPTION if the token's type does
			 * not match any of the cases. */
			TokenType type = tokens.get(0).getType();
			switch (type) {
				case ClassOpen:
					matchers.add(new CharClass(tokens));
					break;
				case Range:
				case Caret:
				case ClassClose:
					// outside of the CharClass constructor, '^', '-', and ']' are literals
					tokens.get(0).toLiteral();
					break;
				case Literal:
					matchers.add(new Literal(tokens));
					break;
				default:
					throw new IllegalStateException("Unexpected token type.");
			}

		}

		return matchers;
	}
}
