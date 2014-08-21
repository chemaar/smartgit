package es.inf.uc3m.kr.smartgit.dumpers;

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

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.UserLoginIDProperties;

public class SeedAggregatedDumper {

	protected static Logger logger = Logger.getLogger(SeedAggregatedDumper.class);
	public static void main(String []args) throws IOException{
		String dirName = "dumps/";
		//FIXME: Extract to a fil
		Enumeration<String> usersLogin = UserLoginIDProperties.RESOURCE_BUNDLE.getKeys();
		String userLogin;
		//Dumpers
		DumpRepository dumpRepository = new DumpRepository();
		DumpCollaborators dumperCollaborators = new DumpCollaborators();
		DumpDownloads dumperDownloads = new DumpDownloads();
		DumpIssues dumperIssues = new DumpIssues();
		DumpLabels dumperLabels = new DumpLabels();
		DumpRepositoryCommit dumperRepositoryCommits = new DumpRepositoryCommit();
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
				DumperSerializer.serialize(dumpRepository, dirName+userLogin+"-repositories-dump.txt",params);
				//FIXME: This generates a file per user, all users in just one file
				//2-Users
				//logger.info("\t...description of user with login: "+userLogin);
				//AggregatedDumper.dumpUsers(dirName+userLogin+"-dump.txt", userLogin);
				//3-Collaborators
				logger.info("\t...collaborators of user with login: "+userLogin);
			
				AggregatedDumper.genericDump(dirName+userLogin+"-collaborators-dump",repositories,dumperCollaborators);
				//4-Downloads
				logger.info("\t...downloads of user with login: "+userLogin);

				AggregatedDumper.genericDump(dirName+userLogin+"-downloads-dump",repositories,dumperDownloads);
				//5-Issues
				logger.info("\t...issues of user with login: "+userLogin);

				AggregatedDumper.genericDump(dirName+userLogin+"-issues-dump",repositories,dumperIssues);
				//6-Labels
				logger.info("\t...labels of user with login: "+userLogin);

				AggregatedDumper.genericDump(dirName+userLogin+"-labels-dump",repositories,dumperLabels);
				//7-Milestones
				logger.info("\t...milestones of user with login: "+userLogin);
				AggregatedDumper.dumpMilestones(dirName+userLogin+"-milestones-dump","all",repositories);
				//8-Commits
				logger.info("\t...commits of user with login: "+userLogin);

				AggregatedDumper.genericDump(dirName+userLogin+"-commits-dump",repositories,dumperRepositoryCommits);
				
				logger.info("End processing user with login: "+userLogin);
				
			}
			logger.info("End time "+GregorianCalendar.getInstance().getTime().toString());
			long end = System.currentTimeMillis();
			logger.info("TIME OF PROCESSING: "+((end-start)/1000)+" seconds.");
			
			//FIXME: Extract to another file?
			//Describing all users
			logger.info("Start time to describe users "+GregorianCalendar.getInstance().getTime().toString());
			List<String> logins = Collections.list(UserLoginIDProperties.RESOURCE_BUNDLE.getKeys());
			logger.info("Creating dump for: "+logins.size()+" users.");
			GitHubDumper dumper = new DumpAllUsers();
			Map<String, Object> params = new HashMap<String,Object>();
			params.put(GitHubDumper.ALL_USER_LOGIN_PARAM,logins);
			DumperSerializer.serialize(dumper, dirName+"all-users-dump",params);
			logger.info("End time to describe users "+GregorianCalendar.getInstance().getTime().toString());
		}catch(Exception e){
			logger.error(e);
		}
	


	}

}
