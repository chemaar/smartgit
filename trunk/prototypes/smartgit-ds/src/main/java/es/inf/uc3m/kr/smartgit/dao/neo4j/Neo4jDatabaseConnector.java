package es.inf.uc3m.kr.smartgit.dao.neo4j;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;

import es.inf.uc3m.kr.smartgit.dumpers.SmartGitNeo4j;

public class Neo4jDatabaseConnector {

	protected static Logger logger = Logger.getLogger(SmartGitNeo4j.class);
	public enum RelTypes implements RelationshipType{
		REPO_NODE,
		USER_NODE,
		COLLABORATOR_NODE,
		LABEL_NODE,
		MILESTONE_NODE,
		ISSUE_NODE,
		COMMIT_NODE,
		DOWNLOAD_NODE
	}

	private static final String SMARTGIT_DB = "target/smartgit-db";
	private static GraphDatabaseService graphDb;
	private static long smartgitNodeId;

	private Neo4jDatabaseConnector(){

	}

	public static GraphDatabaseService getGraphDatabaseService(boolean fromNew) throws IOException{
		if(graphDb == null){
			if(fromNew) {
				FileUtils.deleteRecursively( new File( SMARTGIT_DB ) );
			}
			graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( SMARTGIT_DB );
			createInitialNode();
			registerShutdownHook();
		}
		return graphDb;
	}

	public static long getInitialNode(){
		return smartgitNodeId;
	}
	
	private static void createInitialNode() {
		try ( Transaction tx = graphDb.beginTx() ){
			Node github = graphDb.createNode();
			smartgitNodeId = github.getId();
			tx.success();
		}

	}

	public static GraphDatabaseService getGraphDatabaseService(){
		if(graphDb == null){
			graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( SMARTGIT_DB );
			createInitialNode();
			registerShutdownHook();
		}
		return graphDb;
	}

	public static void returnGraphDatabaseService(GraphDatabaseService service){
		service.shutdown();
	}
	private  static void registerShutdownHook()	{
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running example before it's completed)
		Runtime.getRuntime()
		.addShutdownHook( new Thread()
		{
			@Override
			public void run()
			{
				graphDb.shutdown();
			}
		} );
	}




}
