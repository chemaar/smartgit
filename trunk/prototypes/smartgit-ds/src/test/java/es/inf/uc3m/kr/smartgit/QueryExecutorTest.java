package es.inf.uc3m.kr.smartgit;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public class QueryExecutorTest {

	@Test
	public void test() {
		QueryExecutor qe = new QueryExecutor();
		try {
			qe.execute();
		} catch (IOException e) {
			assertTrue(Boolean.FALSE);
		}
	}

}
