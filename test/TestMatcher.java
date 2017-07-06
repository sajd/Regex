package regex;

import java.util.ArrayList;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import junitparams.*;
import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class TestMatcher {
	private static final String U010000 = new String(Character.toChars(0x010000));
	private static final String U1001A3 = new String(Character.toChars(0x1001a3));
	private static final String U10FFFF = new String(Character.toChars(0x10ffff));

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private void helper(String pattern, String text, String[] expected) {
		Regex reg = new Regex(pattern);
		Search search = new Search(reg, text);
		ArrayList<String> actual = new ArrayList<String>();
		while(search.find())
			actual.add(search.getResult());
		assertArrayEquals(expected, actual.toArray(new String[]{}));
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
	@Parameters(method = "charClassParams")
	public void findCharClass(String pattern, String text, String[] expected) {
		helper(pattern, text, expected);
	}

	@Test
	@Parameters(method = "invalidParams")
	public void invalidPatterns(String pattern, String message) {
		thrown.expect(InvalidRegexException.class);
		thrown.expectMessage(message);
		Regex r = new Regex(pattern);
	}

	public Object[] invalidParams() {
		return new Object[] {
			new Object[] {"abc\\", "missing character after '\\'"},
			new Object[] {"a\\\\Q\\E", "missing '\\Q' before '\\E'"},
			new Object[] {"\\Qab\\Q*\\E*\\E", "missing '\\Q' before '\\E'"},
			new Object[] {"\\0", "invalid octal value"},
			new Object[] {"\\0a", "invalid octal value"},
			new Object[] {"\\0\\", "invalid octal value"},
			new Object[] {"\\08", "invalid octal value"},
			new Object[] {"\\xfg", "invalid hexadecimal value"},
			new Object[] {"\\ufffg", "invalid hexadecimal value"},
			new Object[] {"\\u\\1111", "invalid hexadecimal value"},
			new Object[] {"\\x{}", "invalid hexadecimal value"},
			new Object[] {"\\x{g}", "invalid hexadecimal value"},
			new Object[] {"\\x{10ffffg}", "invalid hexadecimal value"},
			new Object[] {"\\x{10ffff", "missing '}' after hexadecimal value"},
			new Object[] {"\\c", "missing control character"},
			new Object[] {"\\c0", "invalid control character"},
			new Object[] {"\\c\\0010", "invalid control character"},
			new Object[] {"\\c\\0101", "invalid control character"},
			new Object[] {"\\c@", "invalid control character"},
			new Object[] {"\\c[", "invalid control character"},
			new Object[] {"\\c`", "invalid control character"},
			new Object[] {"\\c{", "invalid control character"},
			new Object[] {"\\[[abc\\]\\)}", "character class not closed"},
			new Object[] {"\\[abc\\Q]\\E\\[[x])}", "character class not closed"},
			new Object[] {"[]", "character class not closed"},
			new Object[] {"asdf[", "character class not closed"}
		};
	}

	public Object[] charClassParams() {
		return new Object[] {
			new Object[] {"[a+]", "aa+",
				new String[]{"a", "a", "+"}},
			new Object[] {"[+a]", "aa+",
				new String[]{"a", "a", "+"}},
			new Object[] {"[\\Q].]\\E]", "]a].]",
				new String[] {"]", "]", ".", "]"}},
			new Object[] {"[a]", "aAaa",
				new String[] {"a", "a", "a"}},
			new Object[] {"[aca]", "acab",
				new String[] {"a", "c", "a"}},
			new Object[] {"[abc]", "[abc] ABc",
				new String[] {"a", "b", "c", "c"}},
			new Object[] {"\\[abc]", "[abc]abc",
				new String[] {"[abc]"}},
			new Object[] {"[\\t\\041]", "\t\041\\t041",
				new String[] {"\t", "\041"}},
			new Object[] {"\\[ab]", "\\[ab] a b []",
				new String[] {"[ab]"}},
			new Object[] {"gr[ae]y", "graey gray grey",
				new String[] {"gray", "grey"}},
			new Object[] {"li[cs]en[cs]e", "license lisence licence lisense licsencse",
				new String[] {"license", "lisence", "licence", "lisense"}},
			new Object[] {"[a\\\\]", "a\\\\",
				new String[] {"a", "\\", "\\"}}
		};
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
			new Object[] {"\\00", "\000 \000\000  \000",
				new String[] {"\000", "\000", "\000", "\000"}},
			new Object[] {"\\07", "\007 \007\007  \007",
				new String[] {"\007", "\007", "\007", "\007"}},
			new Object[] {"\\008", "\010 \\008 \0008",
				new String[] {"\0008"}},
			new Object[] {"\\024", "\024\024 \024 \024",
				new String[] {"\024", "\024", "\024", "\024"}},
			new Object[] {"\\0377", "\377 \377 \377\377",
				new String[] {"\377", "\377", "\377", "\377"}},
			new Object[] {"\\0400", "\0400 \0400 \u0100  \0400", 
				new String[] {"\0400", "\0400", "\0400"}},
			new Object[] {"\\01110", "\1110 \1110 \u0248  \1110", 
				new String[] {"\1110", "\1110", "\1110"}},
			new Object[] {"\\x00", "\000 \000\000  \000",
				new String[] {"\000", "\000", "\000", "\000"}},
			new Object[] {"\\xFF", "\u00ff\uff00 \u00ff \u00ff\u00ff",
				new String[] {"\u00ff", "\u00ff", "\u00ff", "\u00ff"}},
			new Object[] {"\\xff", "\u00ff\uff00 \u00ff \u00ff\u00ff",
				new String[] {"\u00ff", "\u00ff", "\u00ff", "\u00ff"}},
			new Object[] {"\\u00000", "\u00000 \u00000000 \u00000",
				new String[] {"\u00000", "\u00000", "\u00000"}}, 
			new Object[] {"\\uffff0", "\uffff0 \uffff0000 \uffff0",
				new String[] {"\uffff0", "\uffff0", "\uffff0"}}, 
			new Object[] {"\\uFFFF0", "\uFFFF0 \uFFFF0000 \uFFFF0",
				new String[] {"\uFFFF0", "\uFFFF0", "\uFFFF0"}}, 
			new Object[] {"\\x{0}", "\u00000 \u00000000 \u00000",
				new String[] {"\u0000", "\u0000", "\u0000"}}, 
			new Object[] {"\\x{4e}", "N N4e N",
				new String[] {"N", "N", "N"}}, 
			new Object[] {"\\x{FFFF}", "\ufffff \uffffffff \uffff",
				new String[] {"\uffff", "\uffff", "\uffff"}}, 
			new Object[] {"\\x{10000}", U010000 + " " + U010000 + " " + U010000,
				new String[] {U010000, U010000, U010000}}, 
			new Object[] {"\\x{1001A3}", U1001A3 + U10FFFF + U1001A3,
				new String[] {U1001A3, U1001A3}}, 
			new Object[] {"\\x{10FFFF}", "F" + U10FFFF + "asfd87" + U010000 + U10FFFF,
				new String[] {U10FFFF, U10FFFF}}, 

			new Object[] {"\\cA", "\u0001 \\cA\u0001 \u0001",
				new String[] {"\u0001", "\u0001", "\u0001"}}, 
			new Object[] {"\\cZ", "\u001A \\cZ\u001A \u001A",
				new String[] {"\u001A", "\u001A", "\u001A"}}, 
			new Object[] {"\\ca", "\u0001 \\ca\u0001 \u0001",
				new String[] {"\u0001", "\u0001", "\u0001"}}, 
			new Object[] {"\\cz", "\u001A \\cz\u001A \u001A",
				new String[] {"\u001A", "\u001A", "\u001A"}}, 
			new Object[] {"\\cc\\cRR", "\u0012\u0003 cR\\cc\\cR\u0003\u0012R",
				new String[] {"\u0003\u0012R"}}, 
			
			new Object[] {"\\t\\t", "\t\t \\t\\t\t\t \t\t",
				new String[] {"\t\t", "\t\t", "\t\t"}},
			new Object[] {"\\t\\n", "\t\n \t\n\t\n \t\n",
				new String[] {"\t\n", "\t\n", "\t\n", "\t\n"}},
			new Object[] {"\\r", "\r \n\r\n \r",
				new String[] {"\r", "\r", "\r"}},
			new Object[] {"\\f", "\f\f \f ff\r\n\f",
				new String[] {"\f", "\f", "\f", "\f"}},
			new Object[] {"\\a", "\u0007 \u0007 \u0007",
				new String[] {"\u0007", "\u0007", "\u0007"}},
			new Object[] {"\\e", "\u001b",
				new String[] {"\u001b"}},
			new Object[] {"\\e", "\u001b \u001b \u001b",
				new String[] {"\u001b", "\u001b", "\u001b"}},
			
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
				new String[] {"*", "*"}},
			new Object [] {"\\Q*\\\\E", "Q *\\E * E",
				new String[] {"*\\"}}
		};
	}
}
