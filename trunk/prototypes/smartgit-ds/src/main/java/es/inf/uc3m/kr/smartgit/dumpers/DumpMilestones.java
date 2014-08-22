package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Milestone;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.RepositoryService;

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.fields.MilestoneFields;

public class DumpMilestones implements GitHubDumper {

	protected static Logger logger = Logger.getLogger(DumpMilestones.class);
	MilestoneService service;

	public DumpMilestones(){
	}



	public List<Map<Enum,String>> createDump(Map<String, Object> params) throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		IRepositoryIdProvider repo = (IRepositoryIdProvider) params.get(REPO_CONSTANT_PARAM);
		try{
			List<Milestone> milestones = ((MilestoneService) getService()).getMilestones(repo, 
					(String) params.get(MILESTONE_STATE_CONSTANT_PARAM));
			long repoID = ((Repository)repo).getId();
			for(Milestone milestone: milestones){
				//In case of needing memory, directly write here to a file...
				csvData.add(describe(milestone,repoID));
			}
		}catch(org.eclipse.egit.github.core.client.RequestException e){
			logger.error(e);
		}

		return csvData;
	}

	private Map<Enum,String> describe(Milestone milestone, long id) {
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(MilestoneFields.ID_Repo,""+id); 
		values.put(MilestoneFields.ID_Repo,""+milestone.getNumber());
		values.put(MilestoneFields.Title,milestone.getTitle());
		values.put(MilestoneFields.Description,milestone.getDescription());
		values.put(MilestoneFields.Creator,milestone.getCreator().getLogin());
		values.put(MilestoneFields.Created,(milestone.getCreatedAt()!=null?milestone.getCreatedAt().toString():null));
		values.put(MilestoneFields.Due_ON,(milestone.getDueOn()!=null?milestone.getDueOn().toString():null));
		values.put(MilestoneFields.Open_Issues,""+milestone.getOpenIssues());
		values.put(MilestoneFields.Closed_Issues,""+milestone.getClosedIssues());
		values.put(MilestoneFields.URL,milestone.getUrl());		

		return values;
	}


	@Override
	public GitHubService getService() {
		if(this.service == null){
			this.service = new MilestoneService(GithubConnectionHelper.createConnection());
		}
		return this.service;
	}

	@Override
	public Enum[] getFields() {
		return MilestoneFields.values();
	}


	public static void main(String []args) throws IOException{
		String DUMP_FILE="milestones-dump";
		String state = "all"; //open, closed, all
		DumpRepository dumpRepository = new DumpRepository();
		RepositoryService repositoryService = (RepositoryService) dumpRepository.getService();
		GitHubDumper dumper = new DumpMilestones();
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(MILESTONE_STATE_CONSTANT_PARAM,state);
		for(Repository repo:repositoryService.getRepositories()){
			logger.info("Processing repository with ID= "+repo.getId());
			params.put(REPO_CONSTANT_PARAM,repo);
			DumperSerializer.serialize(dumper, DUMP_FILE+"-"+repo.getId()+"-"+state+".txt",params);
			params.clear();
		}


	}

	@Override
	public List<Map<Enum, String>> createDump() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
