package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.RepositoryService;

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;

public class DumpRepository implements GitHubDumper {

	protected static Logger logger = Logger.getLogger(DumpRepository.class);

	RepositoryService service;

	public DumpRepository(){

	}

	public List<Map<Enum,String>> createDump() throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		try{
			List<Repository> repos = ((RepositoryService) getService()).getRepositories();
			for(Repository repo: repos){
				//In case of needing memory, directly write here to a file...
				csvData.add(describe(repo));
			}
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
		values.put(RepositoryFields.Owner_Login, (repository.getOwner()!=null?String.valueOf(repository.getOwner().getLogin()):null));
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

	@Override
	public List<Map<Enum, String>> createDump(Map<String, Object> params)
			throws IOException {
		List<Map<Enum,String>> csvData = new LinkedList<>();
		try{
			List<Repository> repos = ((RepositoryService) getService()).
					getRepositories((String)params.get(GitHubDumper.USER_LOGIN_PARAM));
			for(Repository repo: repos){
				csvData.add(describe(repo));
			}
		}catch(Exception e){
			logger.error(e);
			throw e;
		}
		return csvData;
	}

	public static void main(String []args) throws IOException{
		GitHubDumper dumper = new DumpRepository();
		String DUMP_FILE="repo-dump.txt";
		DumperSerializer.serialize(dumper, DUMP_FILE);
	}
}
