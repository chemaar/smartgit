package es.inf.uc3m.kr.smartgit.dao.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.RepositoryService;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.to.LinkTO;
import es.inf.uc3m.kr.smartgit.dao.fields.RepositoryFields;
import es.inf.uc3m.kr.smartgit.dao.neo4j.LinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class GithubRepositoryDAOImpl extends GithubDumperEntityDAOAdapter {
	protected static Logger logger = Logger.getLogger(GithubRepositoryDAOImpl.class);
	
	private RepositoryService service;

	public GithubRepositoryDAOImpl(RepositoryService service, DataSerializer serializer, LinkCreator linkCreator){
		this.service = service;
		setSerializer(serializer);
		setLinkCreator(linkCreator);
	}
	
	

	public GithubRepositoryDAOImpl(RepositoryService service, DataSerializer serializer){
		this.service = service;
		setSerializer(serializer);
	}
	
	
	public List<Map<Enum, String>> getDescription(Map<String, Object> params)
			throws IOException {
		List<Map<Enum,String>> csvData = new LinkedList<>();
		try{
			List<Repository> repos;
			if (params != null){
				 repos = ((RepositoryService) getService()).
						getRepositories((String)params.get(USER_LOGIN_PARAM));
			}else{
				repos = ((RepositoryService) getService()).getRepositories();
			}
		
			for(Repository repo: repos){
				logger.debug("Describing repository with id "+repo.getId()+" and name "+repo.getName());
				csvData.add(describe(repo));
				LinkTO link = new LinkTO();
				link.idFrom = String.valueOf(repo.getId());
				link.idTo = (repo.getOwner()!=null?String.valueOf(repo.getOwner().getLogin()):null);
				link.relation = RelTypes.OWNER;
				getLinks().add(link);
//				repo = null;
//				link = null;
			}
			repos.clear();
			repos = null;
		}catch(Exception e){
			logger.error(e);
			throw e;
		}
		return csvData;
	}

	private Map<Enum,String> describe(Repository repository) {
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(RepositoryFields.Type, RepositoryFields.Repository.name());
		values.put(RepositoryFields.ID, String.valueOf(repository.getId()));
		values.put(RepositoryFields.Name, repository.getName());
		values.put(RepositoryFields.Created, (repository.getCreatedAt()!=null?repository.getCreatedAt().toString():null));
		values.put(RepositoryFields.Description, repository.getDescription());
		values.put(RepositoryFields.Homepage, repository.getHomepage());
		values.put(RepositoryFields.HTML_URL, repository.getHtmlUrl());
		values.put(RepositoryFields.GIT_URL, repository.getGitUrl());
		values.put(RepositoryFields.Language, repository.getLanguage());
		values.put(RepositoryFields.Open_Issues, String.valueOf(repository.getOpenIssues()));
		values.put(RepositoryFields.Size, String.valueOf(repository.getSize()));
		values.put(RepositoryFields.Watchers, String.valueOf(repository.getWatchers()));
		values.put(RepositoryFields.Forks, String.valueOf(repository.getForks()));
		values.put(RepositoryFields.Owner_ID, (repository.getOwner()!=null?String.valueOf(repository.getOwner().getId()):null));
		values.put(RepositoryFields.Owner_Login, 
				(repository.getOwner()!=null?String.valueOf(repository.getOwner().getLogin()):null));
		return values;
	}

	
	@Override
	public GitHubService getService() {
		if(this.service == null){
			this.service = new RepositoryService(GithubConnectionHelper.createConnection());
		}
		return this.service;
	}

	@Override
	public Enum[] getFields() {
		return RepositoryFields.values();
	}
	
	
}
