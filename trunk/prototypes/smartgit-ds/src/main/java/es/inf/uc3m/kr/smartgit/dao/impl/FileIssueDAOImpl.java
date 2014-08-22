package es.inf.uc3m.kr.smartgit.dao.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.IssueService;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dumpers.IssueFields;

public class FileIssueDAOImpl extends FileGithubDumperEntityDAOAdapter  {
	
	protected static Logger logger = Logger.getLogger(FileIssueDAOImpl.class);
	
	private IssueService service;


	public FileIssueDAOImpl(IssueService service, String filename){
		this.service = service;
		setFileName(filename);
	}
	
	public List<Map<Enum,String>> getDescription(Map<String, Object> params) throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		try{
			IRepositoryIdProvider repo = (IRepositoryIdProvider) params.get(REPO_CONSTANT_PARAM);
			List<Issue> issues = ((IssueService) getService()).getIssues(repo, new HashMap<String,String>());
			long repoID = ((Repository)repo).getId();
			for(Issue issue: issues){
				//In case of needing memory, directly write here to a file...
				csvData.add(describe(issue,repoID));
			}
		}catch(Exception e){
			logger.error(e);
			throw e;
		}
		return csvData;
	}

	private Map<Enum,String> describe(Issue issue, long id) {
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(IssueFields.Type, IssueFields.Issue.name());
		values.put(IssueFields.ID_Repo,""+id); 
		values.put(IssueFields.ID,""+issue.getId());
		values.put(IssueFields.Title,issue.getTitle());
		values.put(IssueFields.Creator,(issue.getUser()!=null?issue.getUser().getLogin():null));
		values.put(IssueFields.Assignee,(issue.getAssignee()!=null?issue.getAssignee().getLogin():null));
		values.put(IssueFields.URL,issue.getUrl());
		values.put(IssueFields.Body,issue.getBody());
		String labelsAsStr = "[";
		if(issue.getLabels()!=null){
			for(Label label:issue.getLabels()){
				String labelAsStr = 
						"("+label.getColor()+","+label.getName()+","+label.getUrl()+")";
				labelsAsStr+=labelAsStr+",";
			}
		}
		labelsAsStr+="]";
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

	
}
