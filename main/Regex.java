package regex;

import java.util.*;

/* Represents a regular expression pattern. */
public class Regex {
	private ArrayList<Matcher> matchers;	// pieces of the regex
	
	public Regex(String pattern) throws InvalidRegexException{
		matchers = Matcher.parse(tokenize(pattern));
	}

	/* RETURNS an ArrayList of tokens that represent the input PATTERN. */
	private ArrayList<Token> tokenize (String pattern) throws InvalidRegexException{
		ArrayList<Token> tokens = new ArrayList<Token>();

		for (int i = 0; i < pattern.length(); i++) {
			Token tok = new Token(pattern.charAt(i));
			tokens.add(tok);
		}

		return tokens;
	}

	/* Searches text for the regular expression beginning at index START.
	 * Returns the index up to which a match was found, and -1 if no match was found. */
	int find (char[] text, int start) {
		if (start < 0)
			throw new IllegalArgumentException("negative start index");

		boolean failed = false;
		int i = 0;
		int next = start;

		// iterate through MATCHERS
		while (!failed && i < matchers.size()) {
			Matcher matcher = matchers.get(i);
			next = matcher.matches(text, next);
			if (next == -1)
				failed = true;
			i++;
		}
		
		// must also fail to find a match if there are no MATCHERS
		if (failed || matchers.size() == 0)
			return -1;
		else
			return next;
	}

	public String toString() {
		StringBuilder temp = new StringBuilder();
		for (Matcher m : matchers) 
			temp.append(m.toString());
		return temp.toString();
	}
	
}

