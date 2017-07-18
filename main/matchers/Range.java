package regex;

class Range extends Matcher {
	
	private char begin, end;
	private boolean negated;

	Range(Token b, Token e, boolean negated) {
		if (!(b.isRangeBoundary() && e.isRangeBoundary()))
			throw new IllegalArgumentException();

		begin = b.getChar();
		end = e.getChar();

		if (begin > end)
			throw new IllegalArgumentException();

		this.negated = negated;
	}

	/* Matches any character in between BEGIN and END, inclusive. */
	int matches (char[] text, int start) {
		char c = text[start];
		if (negated && c < begin || c > end)
			return start + 1;
		else if (!negated && c >= begin && c <= end)
			return start + 1;
		else
			return -1;
	}
}
