package regex;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import junitparams.*;
import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class TestMatcher {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private void helper(String pattern, String text, String[] expected) {
		Regex reg = new Regex(pattern);
		Search search = new Search(reg, text);
		for (String e : expected) {
			assertTrue(search.find());
			assertEquals(e, search.getResult());
		}
		assertFalse(search.find());
	}
	
	@Test
	@Parameters(method = "literalParams")
	public void findLiteral(String pattern, String text, String[] expected) {
		helper(pattern, text, expected);
	}

	@Test
	@Parameters(method = "escapedParams")
	public void findEscaped(String pattern, String text, String[] expected) {
		helper(pattern, text, expected);
	}

	@Test
	public void missingEscapeChar() {
		thrown.expect(InvalidRegexException.class);
		thrown.expectMessage("missing character after '\\'");
		Regex r = new Regex("abc\\");
	}
	
	@Test
	public void missingEscapeQ() {
		thrown.expect(InvalidRegexException.class);
		thrown.expectMessage("missing '\\Q' before '\\E'");
		Regex r = new Regex("a\\\\Q\\E");
	}

	@Test
	public void nestedEscape() {
		thrown.expect(InvalidRegexException.class);
		thrown.expectMessage("missing '\\Q' before '\\E'");
		Regex r = new Regex("\\Qab\\Q*\\E*\\E");
	}

	public Object[] literalParams() {
		return new Object[] {
			new Object[] {"t", "akjsf ie. adfi *()ll",
				          new String[] {}},
			new Object[] {"t", "asdf lk rtr l",
				          new String[] {"t"}},
			new Object[] {"t", "tsdfa kjttt t",
						  new String[] {"t", "t", "t", "t", "t"}},
			new Object[] {"t", "t asd",
				  		  new String[] {"t"}},
			new Object[] {"t", "asd t",
						  new String[] {"t"}},
			new Object[] {"\n", "\nas\n", 
						  new String[] {"\n", "\n"}},
			new Object[] {"\000", "asd\000\000",
						  new String[] {"\000", "\000"}},
			new Object[] {"\u0dd1", "\u0dd1asd\u0dd1",
						  new String[] {"\u0dd1", "\u0dd1"}},
			new Object[] {"asd\000asd", "asd\000asD",
						  new String[] {}},
			new Object[] {"asd\000asd", "asd\000asd\000asd \000asd",
				          new String[] {"asd\000asd"}},
			new Object[] {"asd\000asd", "asd\000asdasd\000asd",
				          new String[] {"asd\000asd", "asd\000asd"}},
			new Object[] {"asd\000asd", "lkklasd\000asdasdasd\000asdkll",
				          new String[] {"asd\000asd", "asd\000asd"}}};
	}


	public Object[] escapedParams() {
		return new Object[] {
			new Object[] {"\\\\", "\\a\\a\\\\",
				new String[] {"\\", "\\", "\\", "\\"}},
			/*
			new Object[] {"\\^", "a^^",
				new String[] {"^", "^"}},
			new Object[] {"\\$", "$$",
				new String[] {"$", "$"}},
			new Object[] {"\\.", ".",
				new String[] {"."}},
			new Object[] {"\\|", "ab\n|c",
				new String[] {"|"}},
			new Object[] {"\\?", "a ? b ?",
				new String[] {"?", "?"}},
			new Object[] {"\\*", "* 23*",
				new String[] {"*", "*"}},
			new Object[] {"\\+\\+", "+ ++ +",
				new String[] {"++"}},
			new Object[] {"\\(", "()",
				new String[] {"("}},
			new Object[] {"\\)", "()",
				new String[] {")"}},
			new Object[] {"\\[", "({[ [ab",
				new String[] {"[", "["}},
			new Object[] {"\\{", "\\{",
				new String[] {"{"}},
			*/
			new Object[] {"\\Q\\E", "\\Q\\EQE",
				new String[] {}},
			new Object[] {"\\Q", "\\Q",
				new String[] {}},
			new Object[] {"\\Q\\^$.|?\\Q\\na\\E", "Q\\^$.|?\\Q\\naE",
				new String[] {"\\^$.|?\\Q\\na"}},
			new Object[] {"\\Qabc", "cbabc",
				new String[] {"abc"}},
			new Object [] {"\\Q\\Q\\E.\\Q.\\E", "b\\Qa.b",
				new String[] {"\\Qa."}},
			new Object [] {"\\Q\\Q\\E.\\Q.\\E", "\\Q\\E.\\Q..",
				new String[] {"\\Q.."}},
			new Object [] {"\\Q*\\E", "\\Qabc\\E QabcE Q*E \\Q*\\E",
				new String[] {"*", "*"}}
		};
	}
}
