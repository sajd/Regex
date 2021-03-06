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

	// Regex patterns that throw InvalidRegexException
	public Object[] invalidParams() {
		return new Object[] {
			// backslash
			new Object[] {"abc\\", "missing character after '\\'"},
			// \Q...\E
			new Object[] {"a\\\\Q\\E", "missing '\\Q' before '\\E'"},
			new Object[] {"\\Qab\\Q*\\E*\\E", "missing '\\Q' before '\\E'"},
			// octal and hexadecimal escapes
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
			// control characters
			new Object[] {"\\c", "missing control character"},
			new Object[] {"\\c0", "invalid control character"},
			new Object[] {"\\c\\0010", "invalid control character"},
			new Object[] {"\\c\\0101", "invalid control character"},
			new Object[] {"\\c@", "invalid control character"},
			new Object[] {"\\c[", "invalid control character"},
			new Object[] {"\\c`", "invalid control character"},
			new Object[] {"\\c{", "invalid control character"},
			// character classes
			new Object[] {"\\[[abc\\]\\)}", "character class not closed"},
			new Object[] {"[abc\\Q]\\E\\[[x])}", "character class not closed"},
			new Object[] {"[]", "character class not closed"},
			new Object[] {"asdf[", "character class not closed"},
			new Object[] {"[^]", "character class not closed"}
		};
	}

	// Regex patterns that use character classes
	public Object[] charClassParams() {
		return new Object[] {
			// simple char classes
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
				new String[] {"a", "\\", "\\"}},
			new Object[] {"[ab[cd]]", "[ab[cd]]",
				new String[] {"a", "b", "c", "d"}},
			new Object[] {"[[a]]", "[[a]]aa",
				new String[] {"a", "a", "a"}},
			new Object[] {"[+*]", "[*]",
				new String[] {"*"}},
			new Object[] {"[]x]", "[]x]",
				new String[] {"]", "x", "]"}},
			// char class with ranges
			new Object[] {"[0-9]", "0 3 [0-9] a2b2",
				new String[] {"0", "3", "0", "9", "2", "2"}},
			new Object[] {"[b-g]", "abggfbzg",
				new String[] {"b", "g", "g", "f", "b", "g"}},
			new Object[] {"[\\x59-{]", "aT\u0058{Y|",
				new String[] {"a", "{", "Y"}},
			new Object[] {"[ag-iz]", "azbgkhiz",
				new String[] {"a", "z", "g", "h", "i", "z"}},
			new Object[] {"[a\\-d]", "[a\\-d]abcd",
				new String[] {"a", "-", "d", "a", "d"}},
			new Object[] {"[a\\\\-a]", "[a\\\\-a]`",
				new String[] {"a", "\\", "\\", "a", "]", "`"}},
			new Object[] {"[-x]", "zyx[-x]",
				new String[] {"x", "-", "x"}},
			new Object[] {"[\\\\-]", "\\-[]",
				new String[] {"\\", "-"}},
			new Object[] {"[0-9a-fxA-FX]", "X039Afgx",
				new String[] {"X", "0", "3", "9", "A", "f", "x"}},
			new Object[] {"[+--]", "+'-",
				new String[] {"+", "-"}},
			new Object[] {"a-c]", "abc-a-c]",
				new String[] {"a-c]"}},
			new Object[] {"[-^]", "[^-]",
				new String[] {"^", "-"}},
			new Object[] {"[a\\Q-\\Ed]", "[a\\Q-\\Ed]abcd",
				new String[] {"a", "-", "d", "a", "d"}},
			new Object[] {"-", "---", 
				new String[] {"-", "-", "-"}},
			new Object[] {"---", "---",
				new String[] {"---"}},
			new Object[] {"[6-]", "ab-66",
				new String[] {"-", "6", "6"}},
			new Object[] {"[c-a]", "a-abc", 
				new String[] {"a", "-", "a", "c"}},
			new Object[] {"[a-f][^a-f]", "afgb",
				new String[] {"fg"}},
			// char class with negation
			new Object[] {"[^abc]", "[^abc] dcabB^",
				new String[] {"[", "^", "]", " ", "d", "B", "^"}},
			new Object[] {"a[^A]a", "AaaAaaa a a",
				new String[] {"aaa", "a a"}},
			new Object[] {"[^zz]", "\t\n ",
				new String[] {"\t", "\n", " "}},
			new Object[] {"[^x-z]", "azbcxy",
				new String[] {"a", "b", "c"}},
			new Object[] {"[^ad-z]", "abgIC",
				new String[] {"b", "I", "C"}},
			new Object[] {"[\\^0-9]", "[\\^0-9]a^b",
				new String[] {"^", "0", "9", "^"}},
			new Object[] {"[\\Q^ab\\Ec+]", "\\Q^ab\\Ec+c+",
				new String[] {"^", "a", "b", "c", "+", "c", "+"}},
			new Object[] {"[\\^-`]", "[\\^-`]^^_ab",
				new String[] {"^", "`", "^", "^", "_"}},
			new Object[] {"[]-^]", "[]-^], _abc",
				new String[] {"]", "^", "]"}},
			new Object[] {"[a^-a]", "[a^-a]_",
				new String[] {"a", "^", "a", "_"}},
			new Object[] {"[^^]", "^^^^",
				new String[] {}},
			new Object[] {"[x^]", "[x^]xyz`_",
				new String[] {"x", "^", "x"}},
			new Object[] {"[^]x^]", "[^]x]",
				new String[] {"["}},
			new Object[] {"[^-x][^x-]", "-a8\tbx",
				new String[] {"a8", "\tb"}},
			new Object[] {"[^[^ab]]", "abcz[^]0a\nb",
				new String[] {"a", "b", "a", "b"}},
			new Object[] {"q[^u]", "qu q q",
				new String[] {"q "}},
			new Object[] {"[^\\0101]\\0101", "A\\AB",
				new String[] {"\\A"}},
			new Object[] {"[ax]^[b-c]", "[x^cagxz]",
				new String[] {"x^c"}},
			new Object[] {"b[^c]", "bcbab",
				new String[] {"ba"}}
		};
	}

	// Regex patterns that create Literal tokens
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
			new Object[] {"abc", "cbaba",
						  new String[] {}},
			new Object[] {"asd\000asd", "lkklasd\000asdasdasd\000asdkll",
				          new String[] {"asd\000asd", "asd\000asd"}}};
	}


	// Regex patterns that use escape sequences
	public Object[] escapedParams() {
		return new Object[] {
			// backslash
			new Object[] {"\\\\", "\\a\\a\\\\",
				new String[] {"\\", "\\", "\\", "\\"}},
			// octal values
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
			// hexadecimal values
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
			// control characters
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
			// non-printable/whitespace escaped characters
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
			// escaped special characters
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
			// \Q...\E
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
