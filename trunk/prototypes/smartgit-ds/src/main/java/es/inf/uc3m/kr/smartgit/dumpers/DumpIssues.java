package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Download;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;

public class DumpIssues implements GitHubDumper {
	protected static Logger logger = Logger.getLogger(DumpIssues.class);
	IssueService service;

	public DumpIssues(){
	}


	
	public List<Map<Enum,String>> createDump(Map<String, Object> params) throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		IRepositoryIdProvider repo = (IRepositoryIdProvider) params.get(REPO_CONSTANT_PARAM);
		try{
		List<Issue> issues = ((IssueService) getService()).getIssues(repo, new HashMap<String,String>());
		long repoID = ((Repository)repo).getId();
		for(Issue issue: issues){
			//In case of needing memory, directly write here to a file...
			csvData.add(describe(issue,repoID));
		}
		}catch(org.eclipse.egit.github.core.client.RequestException e){
			logger.error(e);
		}
		return csvData;
	}

	private Map<Enum,String> describe(Issue issue, long id) {
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(IssueFields.ID_Repo,""+id); 
		values.put(IssueFields.ID,""+issue.getId());
		values.put(IssueFields.Title,issue.getTitle());
		values.put(IssueFields.Creator,(issue.getUser()!=null?issue.getUser().getLogin():null));
		values.put(IssueFields.Assignee,(issue.getAssignee()!=null?issue.getAssignee().getLogin():null));
		values.put(IssueFields.URL,issue.getUrl());
		values.put(IssueFields.Body,issue.getBody());
		String labelsAsStr = "[";
		for(Label label:issue.getLabels()){
			String labelAsStr = 
					"("+label.getColor()+","+label.getName()+","+label.getUrl()+")";
			labelsAsStr+=labelAsStr+",";
		}
		labelsAsStr+="]";
		//[(1,3,4),()]
		values.put(IssueFields.Labels,labelsAsStr);
		values.put(IssueFields.Comments,""+issue.getComments());
		values.put(IssueFields.Milestone,""+(issue.getMilestone()!=null?issue.getMilestone().getNumber():null));
		values.put(IssueFields.State,issue.getState());
		values.put(IssueFields.Created,(issue.getCreatedAt()!=null?issue.getCreatedAt().toString():null));
		values.put(IssueFields.Closed,(issue.getClosedAt()!=null?issue.getClosedAt().toString():null));
		values.put(IssueFields.Updated,(issue.getUpdatedAt()!=null?issue.getUpdatedAt().toString():null));
		return values;
	}

	
	@Override
	public GitHubService getService() {
		if(this.service == null){
			this.service = new IssueService(GithubConnectionHelper.createConnection());
		}
		return this.service;
	}

	@Override
	public Enum[] getFields() {
		return IssueFields.values();
	}

	
	public static void main(String []args) throws IOException{
		String DUMP_FILE="issues-dump";
		DumpRepository dumpRepository = new DumpRepository();
		RepositoryService repositoryService = (RepositoryService) dumpRepository.getService();
		GitHubDumper dumper = new DumpIssues();
		Map<String, Object> params = new HashMap<String,Object>();
		for(Repository repo:repositoryService.getRepositories()){
			logger.info("Processing repository with ID= "+repo.getId());
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
