package es.inf.uc3m.kr.smartgit;

import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

public class GithubRepositoryDAO {
	RepositoryService service;
	UserService userService;
	CollaboratorService collaboratorService;
	CommitService  commitService;
	GitHubClient client;
	public GithubRepositoryDAO(){
		this.client = createConnection();
		this.service = new RepositoryService(client);
		this.userService = new UserService(client);
		this.collaboratorService = new CollaboratorService(client);
		this.commitService = new CommitService(client);
        
	}
	private GitHubClient createConnection() {
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(GighubConfigProperties.getString("GithubRepositoryDAO.TOKEN")); //$NON-NLS-1$
		return client;
	}
	public void printRepository() throws IOException{
		List<Repository> repos = this.service.getRepositories();
//		for(Repository repo: repos){
//			describe(repo);
//		}
		describe(repos.get(0));
	}
	
	private void describe(Repository repo) throws IOException {
		System.out.println("---------REPO------------------"); //$NON-NLS-1$
		System.out.println("Name: "+repo.getName()); //$NON-NLS-1$
		System.out.println("Id: "+repo.getId()); //$NON-NLS-1$
		System.out.println("Created: "+repo.getCreatedAt());		 //$NON-NLS-1$
		System.out.println("Description: "+repo.getDescription()); //$NON-NLS-1$
		System.out.println("Homepage: "+repo.getHomepage()); //$NON-NLS-1$
		System.out.println("HTML URL: "+repo.getHtmlUrl()); //$NON-NLS-1$
		System.out.println("GIT URL: "+repo.getGitUrl()); //$NON-NLS-1$
		System.out.println("Language: "+repo.getLanguage()); //$NON-NLS-1$
		System.out.println("Open issues: "+repo.getOpenIssues()); //$NON-NLS-1$
		System.out.println("Size: "+repo.getSize()); //$NON-NLS-1$
		System.out.println("Source: "+repo.getSource()); //$NON-NLS-1$
		System.out.println("Watchers: "+repo.getWatchers()); //$NON-NLS-1$
		System.out.println("Owner: "+repo.getOwner().getId()); //$NON-NLS-1$
		List<User> collaborators = this.collaboratorService.getCollaborators(repo);
		System.out.println("Number of collaborators: "+collaborators.size()); //$NON-NLS-1$
		
		List<RepositoryCommit> commits = this.commitService.getCommits(repo);
		System.out.println("Number of commits: "+commits.size()); //$NON-NLS-1$
		describe(commits.get(0));
		
		
		//Describe owner user
		describe(userService.getUser(repo.getOwner().getLogin()));

	}
	private void describe(RepositoryCommit repositoryCommit) {
		System.out.println("----------Commit-----------------"); //$NON-NLS-1$
		System.out.println("SHA: "+repositoryCommit.getSha()); //$NON-NLS-1$
		System.out.println("URL: "+repositoryCommit.getUrl()); //$NON-NLS-1$
		System.out.println("Author id: "+repositoryCommit.getAuthor().getId()); //$NON-NLS-1$
		System.out.println("Stats: "+repositoryCommit.getStats()); //$NON-NLS-1$
		System.out.println("Commiter ID: "+repositoryCommit.getCommitter().getId()); //$NON-NLS-1$
		System.out.println("Comment count: "+repositoryCommit.getCommit().getCommentCount()); //$NON-NLS-1$
		System.out.println("Message: "+repositoryCommit.getCommit().getMessage()); //$NON-NLS-1$
		
	}
	private void describe(User user) {
		System.out.println("----------OWNER-----------------"); //$NON-NLS-1$
		System.out.println("Name: "+user.getName()); //$NON-NLS-1$
		System.out.println("Login: "+user.getLogin()); //$NON-NLS-1$
		System.out.println("Created: "+user.getCreatedAt()); //$NON-NLS-1$
		System.out.println("ID: "+user.getId()); //$NON-NLS-1$
		System.out.println("Location: "+user.getLocation()); //$NON-NLS-1$
		System.out.println("Avatar: "+user.getAvatarUrl()); //$NON-NLS-1$
		System.out.println("Blog: "+user.getBlog()); //$NON-NLS-1$
		System.out.println("Collaborators: "+user.getCollaborators()); //$NON-NLS-1$
		System.out.println("Company: "+user.getCompany()); //$NON-NLS-1$
		System.out.println("Disk usage: "+user.getDiskUsage()); //$NON-NLS-1$
		System.out.println("Email: "+user.getEmail()); //$NON-NLS-1$
		System.out.println("Followers: "+user.getFollowers()); //$NON-NLS-1$
		System.out.println("Following: "+user.getFollowing()); //$NON-NLS-1$
		System.out.println("Gravatar id: "+user.getGravatarId()); //$NON-NLS-1$

		System.out.println("Public Repos: "+user.getPublicRepos()); //$NON-NLS-1$
		System.out.println("Private Repos: "+user.getTotalPrivateRepos()); //$NON-NLS-1$
		
		
		
		
		
	}
	public static void main(String []args) throws IOException{
		GithubRepositoryDAO dao = new GithubRepositoryDAO();
		dao.printRepository();
	}

}
