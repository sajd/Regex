package regex;

class Range extends Matcher {
	
	private char begin, end;

	Range(Token b, Token e) {
		if (!(b.isRangeBoundary() && e.isRangeBoundary()))
			throw new IllegalArgumentException();

		begin = b.getChar();
		end = e.getChar();

		if (begin > end)
			throw new IllegalArgumentException();
	}

	/* Matches any character in between BEGIN and END, inclusive. */
	int matches (char[] text, int start) {
		if (start >= text.length)
			return -1;
		char c = text[start];
		if (c >= begin && c <= end)
			return start + 1;
		else
			return -1;
	}
}
