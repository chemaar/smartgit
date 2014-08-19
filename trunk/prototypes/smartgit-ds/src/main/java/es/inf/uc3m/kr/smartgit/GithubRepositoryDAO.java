package es.inf.uc3m.kr.smartgit;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Download;
import org.eclipse.egit.github.core.Issue;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.event.Event;
import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.EventService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;

public class GithubRepositoryDAO {
	RepositoryService service;
	UserService userService;
	CollaboratorService collaboratorService;
	CommitService  commitService;
	DownloadService downloadService;
	EventService eventService;
	IssueService issueService;
	LabelService labelService;
	MilestoneService milestoneService;
	
	
	GitHubClient client;
	public GithubRepositoryDAO(){
		this.client = GithubConnectionHelper.createConnection();
		this.service = new RepositoryService(client);
		this.userService = new UserService(client);
		this.collaboratorService = new CollaboratorService(client);
		this.commitService = new CommitService(client);
		this.downloadService = new DownloadService(client);
		this.eventService = new EventService(client);
		this.issueService = new IssueService(client);
		this.labelService = new LabelService(client);
        
	}

	public void printRepository() throws IOException{
		List<Repository> repos = this.service.getRepositories();
//		for(Repository repo: repos){
//			describe(repo);
//		}
		describe(repos.get(0));
	}
	
	private void describe(Repository repo) throws IOException {
		System.out.println("---------REPO------------------"); 
		System.out.println("Name: "+repo.getName()); 
		System.out.println("Id: "+repo.getId()); 
		System.out.println("Created: "+repo.getCreatedAt());		 
		System.out.println("Description: "+repo.getDescription()); 
		System.out.println("Homepage: "+repo.getHomepage()); 
		System.out.println("HTML URL: "+repo.getHtmlUrl()); 
		System.out.println("GIT URL: "+repo.getGitUrl()); 
		System.out.println("Language: "+repo.getLanguage()); 
		System.out.println("Open issues: "+repo.getOpenIssues()); 
		System.out.println("Size: "+repo.getSize()); 
		System.out.println("Source: "+repo.getSource()); 
		System.out.println("Watchers: "+repo.getWatchers()); 
		System.out.println("Owner: "+repo.getOwner().getId()); 
		List<User> collaborators = this.collaboratorService.getCollaborators(repo);
		System.out.println("Number of collaborators: "+collaborators.size()); 
		
		//Describe commits
		List<RepositoryCommit> commits = this.commitService.getCommits(repo);
		System.out.println("Number of commits: "+commits.size()); 
		describe(commits.get(0));
		
		//Describe downloads
		
		List<Download> downloads = this.downloadService.getDownloads(repo);
		describe(downloads.size()>0?downloads.get(0):null);
		
	
	
		//List issues: repo, username at least
		//this.issueService.getIssues();
		List<Issue> issues = issueService.getIssues(repo, new HashMap<String,String>());
		describe(issues.size()>0?issues.get(0):null);
		
		List<Label> labels = this.labelService.getLabels(repo);
		describe(labels.size()>0?labels.get(0):null);
		
		//Describe owner user
		describe(userService.getUser(repo.getOwner().getLogin()));

	}
	private void describe(Label label) {
		if(label !=null){
			System.out.println(label.getName());
			System.out.println(label.getColor());
		}
		
	}
	private void describe(Issue issue) {
		if(issue !=null){
			System.out.println(issue.getId());
		}
		
	}
	private void describe(Download download) {
		if(download != null){
			System.out.println("----------Download-----------------"); 
			System.out.println("Name: "+download.getName()); 
			System.out.println("Id: "+download.getId()); 
			System.out.println("Description: "+download.getDescription()); 
			System.out.println("Size: "+download.getSize());
			System.out.println("Download Count: "+download.getDownloadCount());
		}
	}
	private void describe(RepositoryCommit repositoryCommit) {
		System.out.println("----------Commit-----------------"); 
		System.out.println("SHA: "+repositoryCommit.getSha()); 
		System.out.println("URL: "+repositoryCommit.getUrl()); 
		System.out.println("Author id: "+repositoryCommit.getAuthor().getId()); 
		System.out.println("Stats: "+repositoryCommit.getStats()); 
		System.out.println("Commiter ID: "+repositoryCommit.getCommitter().getId()); 
		System.out.println("Comment count: "+repositoryCommit.getCommit().getCommentCount()); 
		System.out.println("Message: "+repositoryCommit.getCommit().getMessage()); 
		
	}
	private void describe(User user) {
		System.out.println("----------OWNER-----------------"); 
		System.out.println("Name: "+user.getName()); 
		System.out.println("Login: "+user.getLogin()); 
		System.out.println("Created: "+user.getCreatedAt()); 
		System.out.println("ID: "+user.getId()); 
		System.out.println("Location: "+user.getLocation()); 
		System.out.println("Avatar: "+user.getAvatarUrl()); 
		System.out.println("Blog: "+user.getBlog()); 
		System.out.println("Collaborators: "+user.getCollaborators()); 
		System.out.println("Company: "+user.getCompany()); 
		System.out.println("Disk usage: "+user.getDiskUsage()); 
		System.out.println("Email: "+user.getEmail()); 
		System.out.println("Followers: "+user.getFollowers()); 
		System.out.println("Following: "+user.getFollowing()); 
		System.out.println("Gravatar id: "+user.getGravatarId()); 
		System.out.println("Public Repos: "+user.getPublicRepos()); 
		System.out.println("Private Repos: "+user.getTotalPrivateRepos()); 
		
		
		
		
		
	}
	public static void main(String []args) throws IOException{
		GithubRepositoryDAO dao = new GithubRepositoryDAO();
		dao.printRepository();
	}

}
