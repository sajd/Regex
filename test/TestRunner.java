package regex;

import java.util.List;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class TestRunner {
	
	public static void main(String[] args) {
		Result result = JUnitCore.runClasses(TestSuiteRegex.class);

		for (Failure f : result.getFailures())
			System.out.println(f);

		int run = result.getRunCount();
		int failed = result.getFailureCount();
		int ignored = result.getIgnoreCount();
		System.out.println(run + " run. " + failed + " failed. " + ignored + " ignored.");
	}
}
