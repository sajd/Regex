package regex;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

public class TestRegex {
	
	@Test
	public void testConstructorEmpty() {
		String pattern = "";
		String text = "test\ntest";
		Regex r = new Regex(pattern);
		Search s = new Search (r, text);
		assertFalse(s.find());
		
		r = new Regex(pattern);
		s = new Search(r, "");
		assertFalse(s.find());
	}

	@Test
	public void testConstructor() {
		String pattern = "lksdjaf lskdfj";
		Regex r = new Regex(pattern);
		assertEquals(pattern, r.toString());
	}

	@Test
	public void testConstructorSpecial() {
		String pattern = "fas[jk]h*\\\\";
		Regex r = new Regex(pattern);
		assertEquals(pattern, r.toString());
	}

	@Test
	public void testEmptyTextAndNull() {
		String pattern = "\000";
		String text = "";
		Regex r = new Regex(pattern);
		Search s = new Search(r, text);
		assertFalse(s.find());
	}


	@Test
	public void testEmptyTextAndNotNull() {
		String pattern = "p";
		String text = "";
		Regex r = new Regex(pattern);
		Search s = new Search(r, text);
		assertFalse(s.find());
	}

	@Test
	public void testGetResult() {
		Regex r = new Regex("test");
		Search s = new Search(r, "testingtest");
		assertNull(s.getResult());
		s.find();
		s.find();
		assertEquals("test", s.getResult());
		s.find();
		assertNull(s.getResult());
	}

	@Test
	public void testGetResult2() {
		Regex r = new Regex("test");
		Search s = new Search(r, "estt");
		s.find();
		assertNull(s.getResult());
	}

}
