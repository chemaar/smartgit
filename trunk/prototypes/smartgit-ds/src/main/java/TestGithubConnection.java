
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Key;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.SearchRepository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

public class TestGithubConnection {

	/**
	 * Access token: d326b9517fb5bfddebdde67d3b81d896ce779a3e
	 * @param args
	 * @throws IOException
	 */
	public static void main(String []args) throws IOException{
		//Create a client with connection
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token("d326b9517fb5bfddebdde67d3b81d896ce779a3e");
		
		//1-Working with repositories
        RepositoryService service = new RepositoryService(client);
        
        //Search Query
        Map<String, String> searchQuery = new HashMap<String, String>();
        searchQuery.put("keyword","java"); 
        List<SearchRepository> searchRes = service.searchRepositories(searchQuery);
        System.out.println("Search result "+searchRes.toString());
        
        //List repositories of an user
        for (Repository repo : service.getRepositories("chemaar"))
          System.out.println(repo.getName() + " Watchers: " + repo.getWatchers());
        
        //2-Working with users
        UserService userService = new UserService(client);
        System.out.println(userService.getUser().getName());
        //FIXME: Get all users is missing!
        
        //3-
        
        
	}
}
