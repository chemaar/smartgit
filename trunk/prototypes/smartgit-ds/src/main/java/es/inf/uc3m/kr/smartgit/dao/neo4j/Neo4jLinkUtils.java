package es.inf.uc3m.kr.smartgit.dao.neo4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;

import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class Neo4jLinkUtils {

	static Map<String, Long> repoIds = new HashMap<String, Long>();

	protected static Logger logger = Logger.getLogger(Neo4jLinkUtils.class);
	//FIXME: Control failures


	public static boolean createLink(long fromId, long toId, RelTypes relType,GraphDatabaseService graphService){
		try ( Transaction tx = graphService.beginTx() ){
			Node from = graphService.getNodeById(fromId);
			Node to = graphService.getNodeById(toId);
			if(from != null && to !=null){
				from.createRelationshipTo(to,relType);
			}
			tx.success();
		}
		return Boolean.TRUE;
	}

	public static void cleanCache(){
		repoIds.clear();
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
		//A kind of cache FIXME: should be cleaned after each user
		List<Long> ids = new LinkedList<Long>();
		if(repoIds.containsKey(repoId)){
			logger.debug("Returning from cache: "+repoId);
			ids.add(repoIds.get(repoId));
		}else{
			String query = "match (n:REPO_NODE) where n.ID={id} return id(n);";
			Map<String, Object> params = new HashMap<String, Object>();
			params.put( "id", repoId );
			long repoIdAsLong = runQuery(graphService,query, params, "id(n)").get(0);
			repoIds.put(repoId,repoIdAsLong);
			ids.add(repoIdAsLong);
		}
		return ids;
	}

	private static List<Long> runQuery(GraphDatabaseService graphDb,String query, Map<String, Object> params,String var) {
		List<Long> internalIds = new LinkedList<Long>();
		ExecutionEngine engine = new ExecutionEngine(graphDb);
		try (Transaction tx = graphDb.beginTx()) {
			logger.debug("Running link query: "+query+" and params "+params);
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



	public static long lookForInternalDownloadId(String idDownload,
			GraphDatabaseService graphService) {
		List<Long> ids = getInternalDownloadIds(idDownload,graphService);
		return ids.get(0);
	}

	private static List<Long> getInternalDownloadIds(String idDownload,
			GraphDatabaseService graphService) {
		String query = "match (n:DOWNLOAD_NODE) where n.ID={id} return id(n);";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "id", idDownload );
		return runQuery(graphService,query, params, "id(n)");
	}

	public static long lookForInternalIssueId(String idIssue,
			GraphDatabaseService graphService) {
		List<Long> ids = getInternalIssueIds(idIssue,graphService);
		return ids.get(0);
	}

	private static List<Long> getInternalIssueIds(String idIssue,
			GraphDatabaseService graphService) {
		String query = "match (n:ISSUE_NODE) where n.ID={id} return id(n);";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "id", idIssue );
		return runQuery(graphService,query, params, "id(n)");
	}


	public static long lookForInternalLabelId(String idLabel,
			GraphDatabaseService graphService) {
		List<Long> ids = getInternalLabelIds(idLabel,graphService);
		return ids.get(0);
	}

	private static List<Long> getInternalLabelIds(String idLabel,
			GraphDatabaseService graphService) {
		String query = "match (n:LABEL_NODE) where n.ID={id} return id(n);";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "id", idLabel );
		return runQuery(graphService,query, params, "id(n)");
	}


	public static long lookForInternalMilestoneId(String idMilestone,
			GraphDatabaseService graphService) {
		List<Long> ids = getInternalMilestoneIds(idMilestone,graphService);
		return ids.get(0);
	}

	private static List<Long> getInternalMilestoneIds(String idMilestone,
			GraphDatabaseService graphService) {
		String query = "match (n:MILESTONE_NODE) where n.ID={id} return id(n);";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "id", idMilestone );
		return runQuery(graphService,query, params, "id(n)");
	}

	public static long lookForInternalCommitId(String idCommit,
			GraphDatabaseService graphService) {
		List<Long> ids = getInternalCommitIds(idCommit,graphService);
		return ids.get(0);
	}

	private static List<Long> getInternalCommitIds(String idCommit,
			GraphDatabaseService graphService) {
		String query = "match (n:COMMIT_NODE) where n.SHA={id} return id(n);";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "id", idCommit );
		return runQuery(graphService,query, params, "id(n)");
	}


}
