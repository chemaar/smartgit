package es.inf.uc3m.kr.smartgit.dao.neo4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.util.MilestoneComparator;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;

import es.inf.uc3m.kr.smartgit.dao.fields.CommitFields;
import es.inf.uc3m.kr.smartgit.dao.fields.DownloadFields;
import es.inf.uc3m.kr.smartgit.dao.fields.IssueFields;
import es.inf.uc3m.kr.smartgit.dao.fields.LabelFields;
import es.inf.uc3m.kr.smartgit.dao.fields.MilestoneFields;
import es.inf.uc3m.kr.smartgit.dao.fields.RepositoryFields;
import es.inf.uc3m.kr.smartgit.dao.fields.UserFields;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class Neo4jLinkUtils {

	private static final int MAX_REPOS = 1000;
	static int MAX_COMMITS = 10000;
	static Map<String, Long> repoIds = new HashMap<String, Long>(MAX_REPOS);
	static Map<String, Long> commitIds = new HashMap<String, Long>();

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
		commitIds.clear();
	}



	public static long lookForInternalRepositoryId(String idRepository, GraphDatabaseService graphService) {
		logger.debug("Getting internal id for repository "+idRepository);
		return getInternalRepoIdsAPI(idRepository, graphService);
		//		List<Long> ids = getInternalRepoIds(idRepository,graphService);
		//		long id = ids.get(0);
		//		ids.clear();
		//		ids = null;
		//		return id;
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
			if(repoIds.size()>MAX_REPOS){
				repoIds.clear();
			}
			repoIds.put(repoId,repoIdAsLong);
			ids.add(repoIdAsLong);
		}
		return ids;
	}



	public static long lookForInternalUserId(String loginOwner, GraphDatabaseService graphService) {
		return getInternalUserIdsAPI(loginOwner,graphService);
		//		List<Long> ids = getInternalUserIds(loginOwner,graphService);
		//		long id = ids.get(0);
		//		ids.clear();
		//		ids = null;
		//		return id;
	}

	public static List<Long> getInternalUserIds(String userLogin,GraphDatabaseService graphService){
		String query = "match (n:USER_NODE) where n.Login={login} return id(n);";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "login", userLogin );
		return runQuery(graphService,query, params, "id(n)");
	}


	private static List<Long> runQuery(GraphDatabaseService graphDb,String query, Map<String, Object> params,String var) {
		List<Long> internalIds = new LinkedList<Long>();
		ExecutionEngine engine = new ExecutionEngine(graphDb);
		//	try (Transaction tx = graphDb.beginTx()) {
		try{
			logger.debug("Running link query: "+query+" and params "+params);
			ExecutionResult result = engine.execute(query, params);
			// extract the data out of the result, you cannot iterate over it outside of a tx
			Iterator<Long> n_column = result.columnAs(var);
			for ( Long id : IteratorUtil.asIterable(n_column ) ) {
				internalIds.add(id);
				id = null;
			}
			n_column.remove();
			n_column = null;
			result = null;
			query = null;
			engine = null;
		}catch(Exception e){
			//logger.error(e);
		}
		//	tx.success();

		//	}
		if(params !=null){
			params.clear();
			params = null;
		}
		var = null;
		return internalIds;
	}



	public static long lookForInternalDownloadId(String idDownload,
			GraphDatabaseService graphService) {
		return getInternalDownloadIdsAPI(idDownload, graphService);
		//		List<Long> ids = getInternalDownloadIds(idDownload,graphService);
		//		long id = ids.get(0);
		//		ids.clear();
		//		ids = null;
		//		return id;
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
		return getInternalIssueIdsAPI(idIssue, graphService);
		//		List<Long> ids = getInternalIssueIds(idIssue,graphService);
		//		long id = ids.get(0);
		//		ids.clear();
		//		ids = null;
		//		return id;
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
		return getInternalLabelIdsAPI(idLabel, graphService);
		//		List<Long> ids = getInternalLabelIds(idLabel,graphService);
		//		long id = ids.get(0);
		//		ids.clear();
		//		ids = null;
		//		return id;
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
		return getInternalMilestoneIdsAPI(idMilestone, graphService);
		//		List<Long> ids = getInternalMilestoneIds(idMilestone,graphService);
		//		long id = ids.get(0);
		//		ids.clear();
		//		ids = null;
		//		return id;
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
		return getInternalCommitIdsAPI(idCommit, graphService);
		//		List<Long> ids = getInternalCommitIds(idCommit,graphService);
		//		long id = ids.get(0);
		//		ids.clear();
		//		ids = null;
		//		return id;
	}

	private static List<Long> getInternalCommitIds(String idCommit,
			GraphDatabaseService graphService) {
		String query = "match (n:COMMIT_NODE) where n.SHA={id} return id(n);";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "id", idCommit );
		return runQuery(graphService,query, params, "id(n)");
	}

	//Withouth CYPHER QUERIES

	public static long getInternalRepoIdsAPI(String repoId,GraphDatabaseService graphService){
		//A kind of cache FIXME: should be cleaned after each user
		if(repoIds.containsKey(repoId)){
			logger.debug("Returning from cache: "+repoId);
			return repoIds.get(repoId);
		}else{
			logger.debug("Getting from API");
			Label label = DynamicLabel.label( RelTypes.REPO_NODE.name() );
			long repoIdAsLong = -1;
			try ( Transaction tx = graphService.beginTx() )	{
				ResourceIterator<Node> results = graphService.
						findNodesByLabelAndProperty(
								label, 
								RepositoryFields.ID.name(), 
								repoId).iterator();

				while ( results.hasNext() )	{
					repoIdAsLong = results.next().getId();
				}
				results.close();
				results = null;
				tx.success();
				tx.close();
			}

			if(repoIds.size()>MAX_REPOS){
				repoIds.clear();
			}
			repoIds.put(repoId,repoIdAsLong);
			return repoIdAsLong;
		}
	}

	public static long getInternalUserIdsAPI(String userLogin,GraphDatabaseService graphService){
		Label label = DynamicLabel.label( RelTypes.USER_NODE.name() );
		long repoIdAsLong = -1;
		try ( Transaction tx = graphService.beginTx() )	{
			ResourceIterator<Node> results = graphService.
					findNodesByLabelAndProperty(
							label, 
							UserFields.Login.name(), 
							userLogin).iterator();

			while ( results.hasNext() )	{
				repoIdAsLong = results.next().getId();
			}
			results.close();
			results = null;
			tx.success();
			tx.close();
		}
		//FIXME: Check if it is necessary
		//		results.close();
		//		results = null;
		return repoIdAsLong;
	}

	private static long getInternalDownloadIdsAPI(String idDownload,GraphDatabaseService graphService) {
		Label label = DynamicLabel.label( RelTypes.DOWNLOAD_NODE.name() );
		long repoIdAsLong = -1;
		try ( Transaction tx = graphService.beginTx() )	{
			ResourceIterator<Node> results = graphService.
					findNodesByLabelAndProperty(
							label, 
							DownloadFields.ID.name(), 
							idDownload).iterator();
			while ( results.hasNext() )	{
				repoIdAsLong = results.next().getId();
			}
			results.close();
			results = null;
			tx.success();
			tx.close();
		}
		return repoIdAsLong;
	}

	private static long getInternalIssueIdsAPI(String idIssue,
			GraphDatabaseService graphService) {
		Label label = DynamicLabel.label( RelTypes.ISSUE_NODE.name() );
		long repoIdAsLong = -1;
		try ( Transaction tx = graphService.beginTx() )	{
			ResourceIterator<Node> results = graphService.
					findNodesByLabelAndProperty(
							label, 
							IssueFields.ID.name(), 
							idIssue).iterator();
			while ( results.hasNext() )	{
				repoIdAsLong = results.next().getId();
			}
			results.close();
			results = null;
			tx.success();
			tx.close();
				}
		return repoIdAsLong;
	}

	private static long getInternalLabelIdsAPI(String idLabel,
			GraphDatabaseService graphService) {
		Label label = DynamicLabel.label( RelTypes.LABEL_NODE.name() );
		long repoIdAsLong = -1;
		try ( Transaction tx = graphService.beginTx() )	{
			ResourceIterator<Node> results = graphService.
					findNodesByLabelAndProperty(
							label, 
							LabelFields.ID.name(), 
							idLabel).iterator();

			while ( results.hasNext() )	{
				repoIdAsLong = results.next().getId();
			}
			results.close();
			results = null;
			tx.success();
			tx.close();
		}
		return repoIdAsLong;
	}
	private static long getInternalMilestoneIdsAPI(String idMilestone,
			GraphDatabaseService graphService) {
		Label label = DynamicLabel.label( RelTypes.MILESTONE_NODE.name() );
		long repoIdAsLong = -1;
		try ( Transaction tx = graphService.beginTx() )	{
			ResourceIterator<Node> results = graphService.
					findNodesByLabelAndProperty(
							label, 
							MilestoneFields.ID.name(), 
							idMilestone).iterator();

			while ( results.hasNext() )	{
				repoIdAsLong = results.next().getId();
			}
			results.close();
			results = null;
			tx.success();
			tx.close();
		}
		return repoIdAsLong;
	}

	private static long getInternalCommitIdsAPI(String idCommit,
			GraphDatabaseService graphService) {
		Label label = DynamicLabel.label( RelTypes.COMMIT_NODE.name() );
		long repoIdAsLong = -1;
		try ( Transaction tx = graphService.beginTx() )	{
			ResourceIterator<Node> results = graphService.
					findNodesByLabelAndProperty(
							label, 
							CommitFields.SHA.name(), 
							idCommit).iterator();
			while ( results.hasNext() )	{
				repoIdAsLong = results.next().getId();
			}
			results.close();
			results = null;
			tx.success();
			tx.close();
		}
		return repoIdAsLong;
	}
}
