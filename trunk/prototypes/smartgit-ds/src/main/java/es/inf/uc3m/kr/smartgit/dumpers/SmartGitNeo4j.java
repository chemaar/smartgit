package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;


public class SmartGitNeo4j {

	public enum RelTypes implements RelationshipType{
		REPO_NODE
	}

	private static final String SMARTGIT_DB = "target/smartgit-db";
	private GraphDatabaseService graphDb;
	private long smartgitNodeId;


	public void setUp() throws IOException{
		FileUtils.deleteRecursively( new File( SMARTGIT_DB ) );
		this.graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( SMARTGIT_DB );
		try ( Transaction tx = graphDb.beginTx() ){
			Node github = this.graphDb.createNode();
			this.smartgitNodeId = github.getId();
			tx.success();
		}
		registerShutdownHook();
	}

	private void registerShutdownHook()	{
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

	public void serialize(GitHubDumper dumper) throws IOException{
		List<Map<Enum, String>> repositories = dumper.createDump();
		Enum[] fields = dumper.getFields();
		try ( Transaction tx = graphDb.beginTx() ){
			Node github =  graphDb.getNodeById( this.smartgitNodeId );
			for(Map<Enum, String> repoData:repositories){
				Node repo = graphDb.createNode();
				for(int i = 0; i<fields.length;i++){
					Enum property = fields[i];
					String value = repoData.get(fields[i]);
					repo.setProperty( property.name(), (value==null?"N/A":value) );
				}
				github.createRelationshipTo( repo, RelTypes.REPO_NODE);
			}
			tx.success();
		}
	}


	public void shutdown(){
		graphDb.shutdown();
	}

	public static void main( String[] args ) throws IOException {
		SmartGitNeo4j smartgit = new SmartGitNeo4j();
		smartgit.setUp();
		GitHubDumper dumper = new DumpRepository();
		smartgit.serialize(dumper);	
		smartgit.shutdown();
	}


}
