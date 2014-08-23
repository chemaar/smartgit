package es.inf.uc3m.kr.smartgit.dao.impl.neo4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import org.junit.Test;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubDownloadDAOImpl;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubDumperEntityDAO;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubIssueDAOImpl;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubLabelDAOImpl;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubMilestoneDAOImpl;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubRepositoryCommitDAOImpl;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubRepositoryDAOImpl;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubUserDAOImpl;
import es.inf.uc3m.kr.smartgit.dao.neo4j.CommitRepoLinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.DownloadRepoLinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.IssueRepoLinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.LabelRepoLinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.MilestoneRepoLinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDataSerializer;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;
import es.inf.uc3m.kr.smartgit.dao.neo4j.RepoOwnerLinkCreator;

public class FullUserDescribeTest {

	@Test
	public void test() throws Exception {
		Neo4jDatabaseConnector.getGraphDatabaseService(true);
		//Create user
		DataSerializer serializer = new Neo4jDataSerializer(RelTypes.USER_NODE,true);
		List<String> logins = new LinkedList<String>();
		String login = "chemaar";
		logins.add(login);
		UserService service = new UserService(GithubConnectionHelper.createConnection());
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.ALL_USER_LOGIN_PARAM,logins);
		GithubDumperEntityDAO dao = new GithubUserDAOImpl(service, serializer);
		dao.serialize(params);
		//Create repos
		createRepos(login);
		//Create commits for repos
		RepositoryService repositoryService = new RepositoryService(GithubConnectionHelper.createConnection());
		for(Repository repo:repositoryService.getRepositories()){
			createIssues(login, repo);
			createMilestones(login, repo);
			createCommits(login, repo);
			createLabels(login, repo);
			createDownloads(login, repo);
		}


	}

	public static void createRepos(String login) throws Exception{
		DataSerializer serializer = 
				new Neo4jDataSerializer(RelTypes.REPO_NODE,false);
		List<String> logins = new LinkedList<String>();
		logins.add(login);
		RepositoryService service = new RepositoryService(GithubConnectionHelper.createConnection());
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.USER_LOGIN_PARAM,logins);
		GithubDumperEntityDAO dao = new GithubRepositoryDAOImpl(service, serializer, new RepoOwnerLinkCreator());
		dao.serialize(params);
	}

	public static void createCommits(String login, Repository repo) throws Exception{
		DataSerializer serializer = 
				new Neo4jDataSerializer(RelTypes.COMMIT_NODE,false);
		List<String> logins = new LinkedList<String>();
		logins.add(login);
		CommitService service = new CommitService(GithubConnectionHelper.createConnection());
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repo);
		GithubDumperEntityDAO dao = new GithubRepositoryCommitDAOImpl(service, serializer, new CommitRepoLinkCreator());
		dao.serialize(params);

	}

	public static void createLabels(String login, Repository repo) throws Exception{
		DataSerializer serializer = 
				new Neo4jDataSerializer(RelTypes.LABEL_NODE,false);
		List<String> logins = new LinkedList<String>();
		logins.add(login);
		LabelService service = new LabelService(GithubConnectionHelper.createConnection());
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repo);
		GithubDumperEntityDAO dao = new GithubLabelDAOImpl(service, serializer, new LabelRepoLinkCreator());
		dao.serialize(params);

	}

	public static void createDownloads(String login, Repository repo) throws Exception{
		DataSerializer serializer = 
				new Neo4jDataSerializer(RelTypes.DOWNLOAD_NODE,false);
		List<String> logins = new LinkedList<String>();
		logins.add(login);
		DownloadService service = new DownloadService(GithubConnectionHelper.createConnection());
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repo);
		GithubDumperEntityDAO dao = new GithubDownloadDAOImpl(service, serializer, new DownloadRepoLinkCreator());
		dao.serialize(params);

	}
	public static void createIssues(String login, Repository repo) throws Exception{
		DataSerializer serializer = 
				new Neo4jDataSerializer(RelTypes.ISSUE_NODE,false);
		List<String> logins = new LinkedList<String>();
		logins.add(login);
		IssueService service = new IssueService(GithubConnectionHelper.createConnection());
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repo);
		GithubDumperEntityDAO dao = new GithubIssueDAOImpl(service, serializer, new IssueRepoLinkCreator());
		dao.serialize(params);

	}
	
	public static void createMilestones(String login, Repository repo) throws Exception{
		DataSerializer serializer = 
				new Neo4jDataSerializer(RelTypes.MILESTONE_NODE,false);
		List<String> logins = new LinkedList<String>();
		logins.add(login);
		MilestoneService service = new MilestoneService(GithubConnectionHelper.createConnection());
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repo);
		GithubDumperEntityDAO dao = new GithubMilestoneDAOImpl(service, serializer, new MilestoneRepoLinkCreator());
		dao.serialize(params);

	}

}
