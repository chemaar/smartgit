package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.RepositoryService;

import es.inf.uc3m.kr.smartgit.DumperSerializer;

public class SeedAggregatedDumper {

	public static void main(String []args) throws IOException{
		DumpRepository dumpRepository = new DumpRepository();
		RepositoryService repositoryService = (RepositoryService) dumpRepository.getService();
		//System.out.println(repositoryService.getRepositories("kvnsmth"));//Get repositories of an user
		List<Repository> repositories = repositoryService.getRepositories();
		//1-Repositories
		dumpRepositories("repo-dump.txt");
		//2-Users
		dumpUsers("user-dump.txt","chemaar");
		//3-Collaborators
		genericDump("collaborators-dump",repositories,new DumpCollaborators());
		//4-Downloads
		genericDump("downloads-dump",repositories,new DumpDownloads());
		//5-Issues
		genericDump("issues-dump",repositories,new DumpIssues());
		//6-Labels
		genericDump("labels-dump",repositories,new DumpLabels());
		//7-Milestones
		dumpMilestones("milestones-dump","all",repositories);
		//8-Commits
		genericDump("commits-dump",repositories,new DumpRepositoryCommit());
	
	}

	private static void genericDump(String file,List<Repository> repositories,GitHubDumper dumper) throws IOException {
		Map<String, Object> params = new HashMap<String,Object>();
		for(Repository repo:repositories){
			params.put(GitHubDumper.REPO_CONSTANT_PARAM,repo);
			DumperSerializer.serialize(dumper, file+"-"+repo.getId()+".txt",params);
			params.clear();
		}
	}
	

	private static void dumpMilestones(String file, String state,List<Repository> repositories) throws IOException {
		//String state = "all"; //open, closed, all
		GitHubDumper dumper = new DumpMilestones();
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GitHubDumper.MILESTONE_STATE_CONSTANT_PARAM,state);
		for(Repository repo:repositories){
			params.put(GitHubDumper.REPO_CONSTANT_PARAM,repo);
			DumperSerializer.serialize(dumper, file+"-"+repo.getId()+"-"+state+".txt",params);
			params.clear();
		}
	}


	private static void dumpUsers(String file, String login) throws IOException {
		GitHubDumper dumper = new DumpUser();
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GitHubDumper.USER_LOGIN_PARAM,login);
		DumperSerializer.serialize(dumper, file,params );
	}

	private static void dumpRepositories(String file) throws IOException {
		GitHubDumper dumper = new DumpRepository();
		DumperSerializer.serialize(dumper, file);
	}
}
