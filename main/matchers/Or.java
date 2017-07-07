package regex;

import java.util.ArrayList;

abstract class Or extends Matcher{
	
	protected ArrayList<Matcher> matchers;

	Or() {
		matchers = new ArrayList<Matcher>();
	}

	public int matches(char[] text, int start) {
		int result = -1;
		// iterate through MATCHERS until one of them returns a match
		for (Matcher m : matchers) {
			result = m.matches(text, start);
			if (result != -1)
				break;
		}
		return result; 
	}

}

