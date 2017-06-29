package regex;

import org.junit.Test;
import org.junit.runner.RunWith;
import junitparams.*;
import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class TestMatcher {
	
	@Test
	@Parameters(method = "literalParams")
	public void findLiteral(String pattern, String text, String[] expected) {
		Regex reg = new Regex(pattern);
		Search search = new Search(reg, text);
		for (String e : expected) {
			assertTrue(search.find());
			assertEquals(e, search.getResult());
		}
		assertFalse(search.find());
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

}
