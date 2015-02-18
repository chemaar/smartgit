package main;

import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.RequestException;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.UserLoginIDProperties;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.dao.impl.FileDataSerializer;
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
import es.inf.uc3m.kr.smartgit.dao.neo4j.RepoOwnerLinkCreator;
import es.inf.uc3m.kr.smartgit.dumpers.SmartGitNeo4j.RelTypes;

public class FileMainAggregatedFullUserDescribe {

	protected static Logger logger = Logger.getLogger(FileMainAggregatedFullUserDescribe.class);
	public static String OUTPUT_DIR="G:\\smartgit\\";
	public static String OUTPUT_EXT=".csv";
	private GithubRepositoryDAOImpl repositoryDao;
	private GithubRepositoryCommitDAOImpl commitDAO;
	private GithubLabelDAOImpl labelDAO;
	private GithubDownloadDAOImpl downloadsDAO;
	private GithubIssueDAOImpl issuesDAO;
	private GithubMilestoneDAOImpl milestonesDAO;

	public FileMainAggregatedFullUserDescribe(){
		this.initCommitDAO();
		this.initDownloadsDAO();
		this.initIssuesDAO();
		this.initLabelDAO();
		this.initMilestonesDAO();
		this.initRepositoryDAO();
	}


	public static void main(String []args) throws Exception {
		FileMainAggregatedFullUserDescribe main = new FileMainAggregatedFullUserDescribe();
		System.out.println("Starting..."+GregorianCalendar.getInstance().getTime().toString());
		Enumeration<String> usersLogin = UserLoginIDProperties.RESOURCE_BUNDLE.getKeys();
		String userLogin;
		try{
			logger.info("Start time "+GregorianCalendar.getInstance().getTime().toString());
			DataSerializer serializer = new FileDataSerializer(
					OUTPUT_DIR+RelTypes.USER_NODE.name()+OUTPUT_EXT);
			List<String> logins = new LinkedList<String>();
			UserService service = new UserService(GithubConnectionHelper.createConnection());
			GithubDumperEntityDAO userDAO = new GithubUserDAOImpl(service, serializer);
			while(usersLogin.hasMoreElements()){
				try{
					userLogin = usersLogin.nextElement();
					logger.info("Processing user with login: "+userLogin);				
					logins.add(userLogin);
					Map<String, Object> params = new HashMap<String,Object>();
					params.put(GithubDumperEntityDAO.ALL_USER_LOGIN_PARAM,logins);
					userDAO.serialize(params);
					params.clear();
					params = null;

					logger.info("\t...repositories of user with login: "+userLogin);
					main.createRepos(userLogin);
					//Create commits for repos
					RepositoryService repositoryService = new RepositoryService(GithubConnectionHelper.createConnection());

					for(Repository repo:repositoryService.getRepositories(userLogin)){
						try{	
							logger.info("\t...issues of user with login: "+userLogin+" in repository "+repo.getId());
							main.createIssues(userLogin, repo);
							main.waitNext();
							logger.info("\t...milestones of user with login: "+userLogin+" in repository "+repo.getId());
							main.createMilestones(userLogin, repo);
							main.waitNext();
							logger.info("\t...commits of user with login: "+userLogin+" in repository "+repo.getId());
							//main.createCommits(userLogin, repo);
							main.waitNext();
							logger.info("\t...labels of user with login: "+userLogin+" in repository "+repo.getId());
							main.createLabels(userLogin, repo);
							main.waitNext();
							logger.info("\t...downloads of user with login: "+userLogin+" in repository "+repo.getId());
							main.createDownloads(userLogin, repo);
							main.waitNext();
							repo = null;
						}catch(Exception e){
							logger.error("Exception describing a repository..."+e);
						}
					}
					logger.info("End processing user with login: "+userLogin);
					logins.clear();
					userLogin = null;
				}catch(Exception e){
					logger.error("Exception describing user..."+e);
				}
			}
		}catch(Exception e){
			logger.error(e);
		}

		//Create repos

		System.out.println(GregorianCalendar.getInstance().getTime().toString());

	}

	public void waitNext(){
		try {
			TimeUnit.SECONDS.sleep(2);
			System.gc();
		} catch (InterruptedException e) {
			//Handle exception
		}
	}
	public void initRepositoryDAO(){
		DataSerializer serializer = 
				new FileDataSerializer(OUTPUT_DIR+RelTypes.REPO_NODE.name()+OUTPUT_EXT);
		RepositoryService service = new RepositoryService(GithubConnectionHelper.createConnection());
		this.repositoryDao = new GithubRepositoryDAOImpl(service, serializer, new RepoOwnerLinkCreator());
	}

	public void initCommitDAO(){
		DataSerializer serializer = 
				new FileDataSerializer(OUTPUT_DIR+RelTypes.COMMIT_NODE+OUTPUT_EXT);
		CommitService service = new CommitService(GithubConnectionHelper.createConnection());
		this.commitDAO = new GithubRepositoryCommitDAOImpl(service, serializer, new CommitRepoLinkCreator());

	}

	public void initLabelDAO(){
		DataSerializer serializer = 
				new FileDataSerializer(OUTPUT_DIR+RelTypes.LABEL_NODE+OUTPUT_EXT);
		LabelService service = new LabelService(GithubConnectionHelper.createConnection());
		this.labelDAO = new GithubLabelDAOImpl(service, serializer, new LabelRepoLinkCreator());
	}

	public void initDownloadsDAO(){
		DataSerializer serializer = 
				new FileDataSerializer(OUTPUT_DIR+RelTypes.DOWNLOAD_NODE+OUTPUT_EXT);
		DownloadService service = new DownloadService(GithubConnectionHelper.createConnection());
		this.downloadsDAO = new GithubDownloadDAOImpl(service, serializer, new DownloadRepoLinkCreator());	
	}

	public void initIssuesDAO(){
		DataSerializer serializer = 
				new FileDataSerializer(OUTPUT_DIR+RelTypes.ISSUE_NODE+OUTPUT_EXT);
		IssueService service = new IssueService(GithubConnectionHelper.createConnection());
		this.issuesDAO = new GithubIssueDAOImpl(service, serializer, new IssueRepoLinkCreator());
	}

	public void initMilestonesDAO(){
		DataSerializer serializer = 
				new FileDataSerializer(OUTPUT_DIR+RelTypes.MILESTONE_NODE+OUTPUT_EXT);
		MilestoneService service = new MilestoneService(GithubConnectionHelper.createConnection());
		this.milestonesDAO = new GithubMilestoneDAOImpl(service, serializer, new MilestoneRepoLinkCreator());
	}


	public void createRepos(String login) throws Exception{
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.USER_LOGIN_PARAM,login);
		this.repositoryDao.serialize(params);
		params.clear();
		params = null;
	}

	public void createCommits(String login, Repository repo) throws Exception{
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repo);
		this.commitDAO.serialize(params);
		params.clear();
		params = null;
	}

	public  void createLabels(String login, Repository repo) throws Exception{
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repo);
		this.labelDAO.serialize(params);
		params.clear();
		params = null;

	}

	public  void createDownloads(String login, Repository repo) throws Exception{
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repo);
		this.downloadsDAO.serialize(params);

	}
	public  void createIssues(String login, Repository repo) throws Exception{
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repo);
		this.issuesDAO.serialize(params);
		params.clear();
		params = null;

	}

	public void createMilestones(String login, Repository repo) throws Exception{
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repo);
		this.milestonesDAO.serialize(params);
		params.clear();
		params = null;

	}

}
