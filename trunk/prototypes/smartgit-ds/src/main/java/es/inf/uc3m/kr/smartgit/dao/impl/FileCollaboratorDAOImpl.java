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

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dumpers.CollaboratorFields;

public class FileCollaboratorDAOImpl implements GithubDumperEntityDAO {
	
	protected static Logger logger = Logger.getLogger(FileCollaboratorDAOImpl.class);
	
	private CollaboratorService service;
	private String filename;

	public FileCollaboratorDAOImpl(CollaboratorService service, String filename){
		this.service = service;
		this.filename = filename;
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


	public void serialize(Map<String, Object> params) throws Exception{
		List<Map<Enum, String>> csvData = getDescription(params);
		DumperSerializer.write(this.filename,csvData,this.getFields());
	}
	
	
}
