package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.kernel.impl.util.FileUtils;

import es.inf.uc3m.kr.smartgit.UserLoginIDProperties;


public class SmartGitNeo4j {

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

	public void serialize(String label, GitHubDumper dumper, RelTypes type, Map<String, Object> params) throws IOException{
		List<Map<Enum, String>> propertyValues = (params==null?dumper.createDump():dumper.createDump(params));
		Enum[] fields = dumper.getFields();
		try ( Transaction tx = graphDb.beginTx() ){
			Node github =  graphDb.getNodeById( this.smartgitNodeId );
			for(Map<Enum, String> repoData:propertyValues){
				Node currentNode = graphDb.createNode();
				currentNode.addLabel(DynamicLabel.label(label));
				for(int i = 0; i<fields.length;i++){
					Enum property = fields[i];
					String value = repoData.get(fields[i]);
					currentNode.setProperty( property.name(), (value==null?"N/A":value) );
				}
				github.createRelationshipTo(currentNode,type);
			}
			tx.success();
		}
	}


	public void shutdown(){
		graphDb.shutdown();
	}

	public static void main( String[] args ) throws IOException {
		SmartGitNeo4j smartgit = new SmartGitNeo4j();
		//SET-UP DATABASE
		smartgit.setUp();

		Enumeration<String> usersLogin = UserLoginIDProperties.RESOURCE_BUNDLE.getKeys();
		String userLogin;
		//Dumpers
		DumpRepository dumpRepository = new DumpRepository();
		DumpCollaborators dumperCollaborators = new DumpCollaborators();
		DumpDownloads dumperDownloads = new DumpDownloads();
		DumpIssues dumperIssues = new DumpIssues();
		DumpLabels dumperLabels = new DumpLabels();
		DumpRepositoryCommit dumperRepositoryCommits = new DumpRepositoryCommit();
		
		//Describing all users
		//Create users
		logger.info("Start time to describe users "+GregorianCalendar.getInstance().getTime().toString());
		List<String> logins = Collections.list(UserLoginIDProperties.RESOURCE_BUNDLE.getKeys());
		logger.info("Creating dump for: "+logins.size()+" users.");
		GitHubDumper dumper = new DumpAllUsers();
		Map<String, Object> paramsUser = new HashMap<String,Object>();
		paramsUser.put(GitHubDumper.ALL_USER_LOGIN_PARAM,logins);
		smartgit.serialize(RelTypes.USER_NODE.name(),dumper, RelTypes.USER_NODE, paramsUser);	
		logger.info("End time to describe users "+GregorianCalendar.getInstance().getTime().toString());
		
		long start = System.currentTimeMillis();
		try{
			logger.info("Start time "+GregorianCalendar.getInstance().getTime().toString());
			while(usersLogin.hasMoreElements()){
				userLogin = usersLogin.nextElement();
				logger.info("Processing user with login: "+userLogin);
				
				Map<String, Object> params = new HashMap<String,Object>();
				params.put(GitHubDumper.USER_LOGIN_PARAM,userLogin);
				
				//A-Look for the repositories of the user with login=userLogin
				
				RepositoryService repositoryService = (RepositoryService) dumpRepository.getService();
				List<Repository> repositories = repositoryService.getRepositories(userLogin);
				
				//B-Extract and dump
				//1-Repositories
				logger.info("\t...repositories of user with login: "+userLogin);
				smartgit.serialize(RelTypes.REPO_NODE.name(),dumpRepository, RelTypes.REPO_NODE,null);	
				//FIXME: This generates a file per user, all users in just one file
				//2-Users
				//logger.info("\t...description of user with login: "+userLogin);
				//AggregatedDumper.dumpUsers(dirName+userLogin+"-dump.txt", userLogin);
				//3-Collaborators
				logger.info("\t...collaborators of user with login: "+userLogin);
				smartgit.genericDump(RelTypes.COLLABORATOR_NODE.name(),dumperCollaborators, RelTypes.COLLABORATOR_NODE, repositories);

				//4-Downloads
				logger.info("\t...downloads of user with login: "+userLogin);
				smartgit.genericDump(RelTypes.DOWNLOAD_NODE.name(),dumperDownloads, RelTypes.DOWNLOAD_NODE, repositories);
				
				//5-Issues
				logger.info("\t...issues of user with login: "+userLogin);
				smartgit.genericDump(RelTypes.ISSUE_NODE.name(),dumperIssues, RelTypes.ISSUE_NODE, repositories);
				
				//6-Labels
				logger.info("\t...labels of user with login: "+userLogin);
				smartgit.genericDump(RelTypes.LABEL_NODE.name(),dumperLabels, RelTypes.LABEL_NODE, repositories);
				
				//7-Milestones
				logger.info("\t...milestones of user with login: "+userLogin);
				smartgit.dumpMilestones("all", repositories);
				//8-Commits
				logger.info("\t...commits of user with login: "+userLogin);
				smartgit.genericDump(RelTypes.COMMIT_NODE.name(),dumperRepositoryCommits, RelTypes.COMMIT_NODE, repositories);
				
				//FIXME: Create links
				//User->repos
				//repos->collaborators
				//...
				
				logger.info("End processing user with login: "+userLogin);
				
			}
			logger.info("End time "+GregorianCalendar.getInstance().getTime().toString());
			long end = System.currentTimeMillis();
			logger.info("TIME OF PROCESSING: "+((end-start)/1000)+" seconds.");
			
		}catch(Exception e){
			logger.error(e);
		}
	
		//SHUTDOWN DATABASE
		smartgit.shutdown();
	}

	public void genericDump(
			String label,
			GitHubDumper dumper, 
			RelTypes relType,
			List<Repository> repositories) throws IOException {
		Map<String, Object> params = new HashMap<String,Object>();
		for(Repository repo:repositories){
			params.put(GitHubDumper.REPO_CONSTANT_PARAM,repo);
			this.serialize(label,dumper,relType,params);	
			params.clear();
		}
		
	}
	
	public void dumpMilestones(String state,List<Repository> repositories) throws IOException {
		//String state = "all"; //open, closed, all
		GitHubDumper dumper = new DumpMilestones();
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GitHubDumper.MILESTONE_STATE_CONSTANT_PARAM,state);
		for(Repository repo:repositories){
			params.put(GitHubDumper.REPO_CONSTANT_PARAM,repo);
			this.serialize(RelTypes.MILESTONE_NODE.name(),dumper,RelTypes.MILESTONE_NODE,params);	
			params.clear();
		}
	}


	
	//FIXME:
//	private String runCypher(String query, Map params) {
//		ExecutionEngine engine = new ExecutionEngine(graphDb);
//		String id = "";
//		try (Transaction tx = graphDb.beginTx()) {
//			ExecutionResult result = engine.execute(query, params);
//			// extract the data out of the result, you cannot iterate over it outside of a tx
//			for ( Map<String, Object> row : result ){
//				for ( Entry<String, Object> column : row.entrySet() ){
//					id = column.getValue().toString();
//				}
//			}
//			tx.success();
//			return id;
//		}
//	}
//
//	public String loginToNeo4jID(String login){
//		String neo4Jid = null;
//		String query = "match (n:USER_NODE) where n.Login={login} return id(n);";
//		Map<String, Object> params = new HashMap<String, Object>();
//		params.put( "login", login );
//		System.out.println(runCypher(query,params));
//		return neo4Jid;
//	}
//	
	
	
}
