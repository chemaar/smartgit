package es.inf.uc3m.kr.smartgit.graph;


import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;

import es.inf.uc3m.kr.smartgit.graph.loader.GephiFileResourceLoaderImpl;
import es.inf.uc3m.kr.smartgit.graph.loader.GraphLoader;

public class GraphMetricsServiceTest {

	@Test
	public void test() {
		GraphLoader loader = new GephiFileResourceLoaderImpl();
		loader.load("polblogs.gml");
		GraphMetricsService metrics = new GraphMetricsService(loader);
		Assert.assertEquals(1492, metrics.calculateMetrics().size());
	}

}
