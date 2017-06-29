package regex;

/* Searches a String for a given regular expression pattern. */
public class Search {
	
	private Regex pattern;
	private char[] text;
	private int pos;		// current search position in TEXT
	private String match;

	public Search(Regex r, String s) {
		pattern = r;
		text = s.toCharArray();
		pos = 0;
		match = null;
	}

	/* Returns true if there is a subsequence of characters in TEXT starting at or after
	 * POS that matches PATTERN. Returns false otherwise. Subsequent calls to FIND will
	 * begin searching after the location of the previous match. */
	public boolean find() {
		boolean found = false;

		while (!found && pos < text.length) {
			int next = pattern.find(text, pos);
			if (next == -1) {
				pos++;
			} else {
				found = true;
				match = String.valueOf(text, pos, next-pos);
				pos = next;
			}
		}

		if (!found)
			match = null;

		return found;
	}

	/* Returns the match that was found by the last invocation of FIND. If the last
	 * invocation was not successful, returns NULL. */
	public String getResult() {
		return match;
	}

}
