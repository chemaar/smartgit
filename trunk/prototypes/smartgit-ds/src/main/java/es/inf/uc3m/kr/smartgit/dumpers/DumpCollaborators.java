package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;

public class DumpCollaborators extends DumpUser {

	public static final String REPO_CONSTANT = "repo";
	
	CollaboratorService service;


	public DumpCollaborators(){
	}


	
	public List<Map<Enum,String>> createDump(Map<String, Object> params) throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		IRepositoryIdProvider repo = (IRepositoryIdProvider) params.get(REPO_CONSTANT);
		List<User> collaborators = ((CollaboratorService) getService()).getCollaborators(repo);
		long repoID = ((Repository)repo).getId();
		for(User collaborator: collaborators){
			//In case of needing memory, directly write here to a file...
			//Collaborator only returns some fields, full description using the user service
			User user = ((UserService) super.getService()).getUser(collaborator.getLogin());
			csvData.add(describe(user,repoID));
		}
		return csvData;
	}

	private Map<Enum,String> describe(User user, long id) {
		Map<Enum,String> values = super.describe(user);
		values.put(CollaboratorFields.ID_Repo, String.valueOf(id));
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
		List<UserFields> userFields = Arrays.asList(UserFields.values());
		List<CollaboratorFields> collaboratorFields = Arrays.asList(CollaboratorFields.values());
		List<Enum> both = new LinkedList<Enum>(userFields);
		both.addAll(collaboratorFields);
		return both.toArray(new Enum[both.size()]);
	}


	
	public static void main(String []args) throws IOException{
		String DUMP_FILE="collaborators-dump";
		DumpRepository dumpRepository = new DumpRepository();
		RepositoryService repositoryService = (RepositoryService) dumpRepository.getService();
		GitHubDumper dumper = new DumpCollaborators();
		Map<String, Object> params = new HashMap<String,Object>();
		for(Repository repo:repositoryService.getRepositories()){
			System.out.println("Processing "+repo.getId());
			params.put(REPO_CONSTANT,repo);
			DumperSerializer.serialize(dumper, DUMP_FILE+"-"+repo.getId()+".txt",params);
			params.clear();
		}
	

	}
}
