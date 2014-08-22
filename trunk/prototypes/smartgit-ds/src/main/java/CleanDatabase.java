import java.io.IOException;

import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector;


public class CleanDatabase {

	public static void main(String []args) throws IOException{
		Neo4jDatabaseConnector.getGraphDatabaseService(true);
	}
}
