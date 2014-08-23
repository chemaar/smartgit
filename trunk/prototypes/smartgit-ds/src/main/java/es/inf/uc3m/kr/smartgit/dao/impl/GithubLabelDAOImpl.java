package es.inf.uc3m.kr.smartgit.dao.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.to.LinkTO;
import es.inf.uc3m.kr.smartgit.dao.fields.LabelFields;
import es.inf.uc3m.kr.smartgit.dao.neo4j.LinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class GithubLabelDAOImpl extends GithubDumperEntityDAOAdapter  {
	
	protected static Logger logger = Logger.getLogger(GithubLabelDAOImpl.class);
	
	private LabelService service;


	public GithubLabelDAOImpl(LabelService service, DataSerializer serializer, LinkCreator linkCreator){
		this.service = service;
		setSerializer(serializer);
		setLinkCreator(linkCreator);
	}
	
	public GithubLabelDAOImpl(LabelService service, DataSerializer serializer){
		this.service = service;
		setSerializer(serializer);
	}
	
	public List<Map<Enum,String>> getDescription(Map<String, Object> params) throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		try{
			IRepositoryIdProvider repo = (IRepositoryIdProvider) params.get(REPO_CONSTANT_PARAM);
			List<Label> labels = ((LabelService) getService()).getLabels(repo);
			long repoID = ((Repository)repo).getId();
			logger.debug("The repository with id "+repoID+" has "+labels.size()+" labels.");
			for(Label label: labels){
				//In case of needing memory, directly write here to a file...
				csvData.add(describe(label,repoID));
				LinkTO link = new LinkTO();
				link.idFrom = String.valueOf(repoID);
				link.idTo = String.valueOf(label.hashCode());
				link.relation = RelTypes.HAS_LABEL;
				getLinks().add(link);
			}
		}catch(Exception e){
			logger.error(e);
			throw e;
		}

		return csvData;
	}

	private Map<Enum,String> describe(Label label, long id) {
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(LabelFields.Type, LabelFields.Label.name());
		values.put(LabelFields.ID_Repo,""+id); 
		values.put(LabelFields.ID,""+label.hashCode()); 
		values.put(LabelFields.Name,label.getName());
		values.put(LabelFields.Color,label.getColor());
		values.put(LabelFields.URL,label.getUrl());
		return values;
	}


	@Override
	public GitHubService getService() {
		if(this.service == null){
			this.service = new LabelService(GithubConnectionHelper.createConnection());
		}
		return this.service;
	}

	@Override
	public Enum[] getFields() {
		return LabelFields.values();
	}

	
}
