package es.inf.uc3m.kr.smartgit;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;

public class DumpRepository {

	RepositoryService service;
	GitHubClient client;

	
	public DumpRepository(){
		this.client = GithubConnectionHelper.createConnection();
		this.service = new RepositoryService(client);
	}
	
	public List<Map<Enum,String>> createDump() throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		List<Repository> repos = this.service.getRepositories();
		for(Repository repo: repos){
			//In case of needing memory, directly write here to a file...
			csvData.add(describe(repo));
		}
		return csvData;
	}

	private Map<Enum,String> describe(
			Repository repository) {
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(RepositoryFields.ID, String.valueOf(repository.getId()));
		values.put(RepositoryFields.Name, repository.getName());
		values.put(RepositoryFields.Created, repository.getCreatedAt().toString());
		values.put(RepositoryFields.Description, repository.getDescription());
		values.put(RepositoryFields.Homepage, repository.getHomepage());
		values.put(RepositoryFields.HTML_URL, repository.getHtmlUrl());
		values.put(RepositoryFields.GIT_URL, repository.getGitUrl());
		values.put(RepositoryFields.Language, repository.getLanguage());
		values.put(RepositoryFields.Open_Issues, String.valueOf(repository.getOpenIssues()));
		values.put(RepositoryFields.Size, String.valueOf(repository.getSize()));
		values.put(RepositoryFields.Watchers, String.valueOf(repository.getWatchers()));
		values.put(RepositoryFields.Forks, String.valueOf(repository.getForks()));
		values.put(RepositoryFields.Owner_ID, String.valueOf(repository.getOwner().getId()));
		return values;
	}
	
	public static void main(String []args) throws IOException{
		String DUMP_FILE="repo-dump.txt";
		String SEPARATOR = ";";
		PrintWriter pw = new PrintWriter(new File(DUMP_FILE));
		DumpRepository dumper = new DumpRepository();
		List<Map<Enum, String>> repositories = dumper.createDump();
		RepositoryFields[] fields = RepositoryFields.values();
		//1-Create header
		for(int i = 0; i<fields.length;i++){
			pw.print(fields[i].name()+SEPARATOR);
		}
		//2-Serialize fields
		pw.println("");
		for(Map<Enum, String> repo:repositories){
			for(int i = 0; i<fields.length;i++){
				pw.print(repo.get(fields[i])+SEPARATOR);
			}
			pw.println("");
		}
		pw.close();
	}
}
