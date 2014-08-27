package es.inf.uc3m.kr.smartgit.dao.impl;

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

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.to.LinkTO;
import es.inf.uc3m.kr.smartgit.dao.fields.LabelFields;
import es.inf.uc3m.kr.smartgit.dao.fields.MilestoneFields;
import es.inf.uc3m.kr.smartgit.dao.neo4j.LinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class GithubMilestoneDAOImpl extends GithubDumperEntityDAOAdapter  {
	
	protected static Logger logger = Logger.getLogger(GithubMilestoneDAOImpl.class);
	
	private MilestoneService service;

	
	public GithubMilestoneDAOImpl(MilestoneService service, DataSerializer serializer, LinkCreator linkCreator){
		this.service = service;
		setSerializer(serializer);
		setLinkCreator(linkCreator);
	}
	

	public GithubMilestoneDAOImpl(MilestoneService service, DataSerializer serializer){
		this.service = service;
		setSerializer(serializer);
	}
	
	public List<Map<Enum,String>> getDescription(Map<String, Object> params) throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		IRepositoryIdProvider repo = (IRepositoryIdProvider) params.get(REPO_CONSTANT_PARAM);
		try{
			List<Milestone> milestones = ((MilestoneService) getService()).getMilestones(repo, 
					(String) params.get(MILESTONE_STATE_CONSTANT_PARAM));
			long repoID = ((Repository)repo).getId();
			logger.debug("The repository with id "+repoID+" has "+milestones.size()+" milestones.");
			for(Milestone milestone: milestones){
				//In case of needing memory, directly write here to a file...
				csvData.add(describe(milestone,repoID));
				LinkTO link = new LinkTO();
				link.idFrom = String.valueOf(repoID);
				link.idTo = String.valueOf(milestone.hashCode());
				link.relation = RelTypes.HAS_MILESTONE;
				getLinks().add(link);
//				milestone = null;
//				link = null;
			}
			milestones.clear();
			milestones = null;
		}catch(org.eclipse.egit.github.core.client.RequestException e){
			logger.error(e);
		}

		return csvData;
	}

	private Map<Enum,String> describe(Milestone milestone, long id) {
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(MilestoneFields.ID_Repo,""+id); 
		values.put(MilestoneFields.ID,""+milestone.hashCode());
		values.put(MilestoneFields.Number,""+milestone.getNumber());
		values.put(MilestoneFields.Title,milestone.getTitle());
		values.put(MilestoneFields.Description,milestone.getDescription());
		values.put(MilestoneFields.Creator, (milestone.getCreator()!=null?milestone.getCreator().getLogin():null));
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

	
}
