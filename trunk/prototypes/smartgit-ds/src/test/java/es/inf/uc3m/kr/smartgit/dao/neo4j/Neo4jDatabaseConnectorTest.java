package es.inf.uc3m.kr.smartgit.dao.neo4j;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

public class Neo4jDatabaseConnectorTest {

	@Test
	public void testCreateNew() throws IOException {
		 GraphDatabaseService service = Neo4jDatabaseConnector.getGraphDatabaseService(true);
		 Assert.assertNotNull(service);
		 Neo4jDatabaseConnector.returnGraphDatabaseService(service);
		 service = Neo4jDatabaseConnector.getGraphDatabaseService();
		 Assert.assertNotNull(service);
		 Neo4jDatabaseConnector.returnGraphDatabaseService(service);
	}

}
