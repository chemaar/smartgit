package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.RepositoryService;

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;

public class DumpRepositoryCommit implements GitHubDumper {
	
	protected static Logger logger = Logger.getLogger(DumpRepositoryCommit.class);
	
	CommitService service;

	public DumpRepositoryCommit(){
	}


	
	public List<Map<Enum,String>> createDump(Map<String, Object> params) throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		IRepositoryIdProvider repo = (IRepositoryIdProvider) params.get(REPO_CONSTANT_PARAM);
		List<RepositoryCommit> commits = ((CommitService) getService()).getCommits(repo);
		long repoID = ((Repository)repo).getId();
		for(RepositoryCommit commit: commits){
			//In case of needing memory, directly write here to a file...
			csvData.add(describe(commit,repoID));
		}
		return csvData;
	}

	private Map<Enum,String> describe(RepositoryCommit commit, long id) {
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(CommitFields.ID_Repo,""+id); 
		values.put(CommitFields.SHA,commit.getSha());
		values.put(CommitFields.Login,(commit.getCommitter()!=null?commit.getCommitter().getLogin():null));
		values.put(CommitFields.URL,commit.getUrl());
		values.put(CommitFields.Files,""+(commit.getFiles()!=null?commit.getFiles().size():null));
		values.put(CommitFields.SHA_COMMIT,commit.getCommit().getSha());
		values.put(CommitFields.Email,commit.getCommit().getAuthor().getEmail());
		values.put(CommitFields.Name,commit.getCommit().getAuthor().getName());
		values.put(CommitFields.Date,commit.getCommit().getAuthor().getDate().toString());
		values.put(CommitFields.N_Comments,""+commit.getCommit().getCommentCount());
		values.put(CommitFields.Message,commit.getCommit().getMessage());
		values.put(CommitFields.URL_Commit,commit.getCommit().getUrl());
		return values;
	}

	
	@Override
	public GitHubService getService() {
		if(this.service == null){
			this.service = new CommitService(GithubConnectionHelper.createConnection());
		}
		return this.service;
	}

	@Override
	public Enum[] getFields() {
		return CommitFields.values();
	}

	
	public static void main(String []args) throws IOException{
		String DUMP_FILE="commits-dump";
		DumpRepository dumpRepository = new DumpRepository();
		RepositoryService repositoryService = (RepositoryService) dumpRepository.getService();
		GitHubDumper dumper = new DumpRepositoryCommit();
		Map<String, Object> params = new HashMap<String,Object>();
		for(Repository repo:repositoryService.getRepositories()){
			logger.info("Processing repository with ID="+repo.getId());
			params.put(REPO_CONSTANT_PARAM,repo);
			DumperSerializer.serialize(dumper, DUMP_FILE+"-"+repo.getId()+".txt",params);
			params.clear();
		}
	

	}

	@Override
	public List<Map<Enum, String>> createDump() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
