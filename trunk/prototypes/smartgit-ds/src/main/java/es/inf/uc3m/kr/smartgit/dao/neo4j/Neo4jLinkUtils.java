package es.inf.uc3m.kr.smartgit.dao.neo4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;

import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class Neo4jLinkUtils {

	//FIXME: Control failures
	

	public static boolean createLink(long fromId, long toId, RelTypes relType,GraphDatabaseService graphService){
		try ( Transaction tx = graphService.beginTx() ){
			Node from = graphService.getNodeById(fromId);
			Node to = graphService.getNodeById(toId);
			if(from != null && to !=null){
				from.createRelationshipTo(to,RelTypes.OWNER);
			}
			tx.success();
		}
		return Boolean.TRUE;
	}

	
	
		public static long lookForInternalRepositoryId(String idRepository, GraphDatabaseService graphService) {
			List<Long> ids = getInternalRepoIds(idRepository,graphService);
			return ids.get(0);
		}

		public static long lookForInternalUserId(String loginOwner, GraphDatabaseService graphService) {
			List<Long> ids = getInternalUserIds(loginOwner,graphService);
			return ids.get(0);
		}



		public static List<Long> getInternalUserIds(String userLogin,GraphDatabaseService graphService){
			String query = "match (n:USER_NODE) where n.Login={login} return id(n);";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put( "login", userLogin );
			return runQuery(graphService,query, params, "id(n)");
		}

		public static List<Long> getInternalRepoIds(String repoId,GraphDatabaseService graphService){
			String query = "match (n:REPO_NODE) where n.ID={id} return id(n);";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put( "id", repoId );
			return runQuery(graphService,query, params, "id(n)");
		}

		private static List<Long> runQuery(GraphDatabaseService graphDb,String query, Map<String, Object> params,String var) {
			List<Long> internalIds = new LinkedList<Long>();
			ExecutionEngine engine = new ExecutionEngine(graphDb);
			try (Transaction tx = graphDb.beginTx()) {
				ExecutionResult result = engine.execute(query, params);
				// extract the data out of the result, you cannot iterate over it outside of a tx
				Iterator<Long> n_column = result.columnAs(var);
				for ( Long id : IteratorUtil.asIterable(n_column ) ) {
					internalIds.add(id);
				}
				tx.success();
			}
			return internalIds;
		}

}
