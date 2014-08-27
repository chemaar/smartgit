package es.inf.uc3m.kr.smartgit.dao.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.GitHubService;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.dao.fields.CollaboratorFields;
import es.inf.uc3m.kr.smartgit.to.LinkTO;

public class GithubCollaboratorDAOImpl extends GithubDumperEntityDAOAdapter {
	
	protected static Logger logger = Logger.getLogger(GithubCollaboratorDAOImpl.class);
	
	private CollaboratorService service;
	

	public GithubCollaboratorDAOImpl(CollaboratorService service, DataSerializer serializer){
		this.service = service;
		setSerializer(serializer);
	}
	
	
	public List<Map<Enum,String>> getDescription(Map<String, Object> params) throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		try{
			IRepositoryIdProvider repo = (IRepositoryIdProvider) params.get(REPO_CONSTANT_PARAM);
			List<User> collaborators = ((CollaboratorService) getService()).getCollaborators(repo);
			long repoID = ((Repository)repo).getId();
			for(User collaborator: collaborators){
				//In case of needing memory, directly write here to a file...
				//Collaborator only returns some fields, full description using the user service
				//FIXME: if there is a lot of collaborators: Repository access blocked (403)
				//User user = ((UserService) super.getService()).getUser(collaborator.getLogin());
				csvData.add(describe(collaborator,repoID));
			}
			collaborators.clear();
			collaborators = null;
		}catch(Exception e){
			logger.error(e);
			throw e;
		}
		
		return csvData;
	}

	private Map<Enum,String> describe(User user, long id) {
		//Map<Enum,String> values = super.describe(user);
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(CollaboratorFields.Type, CollaboratorFields.Collaborator.name());
		values.put(CollaboratorFields.ID_Repo, String.valueOf(id));
		values.put(CollaboratorFields.Login, user.getLogin());
		return values;
	}

	
	@Override
	public GitHubService getService() {
		if(this.service == null){
			this.service = new CollaboratorService(GithubConnectionHelper.createConnection());
		}
		return this.service;
	}

	@Override
	public Enum[] getFields() {
//		List<UserFields> userFields = Arrays.asList(UserFields.values());
//		List<CollaboratorFields> collaboratorFields = Arrays.asList(CollaboratorFields.values());
//		List<Enum> both = new LinkedList<Enum>(userFields);
//		both.addAll(collaboratorFields);
//		return both.toArray(new Enum[both.size()]);
		return CollaboratorFields.values();
	}
	
	
}
