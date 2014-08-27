package es.inf.uc3m.kr.smartgit;

import org.eclipse.egit.github.core.client.GitHubClient;

public class GithubConnectionHelper {

	static GitHubClient client; 
	public static GitHubClient createConnection() {
		if(client ==null){
			client = new GitHubClient();
			client.setOAuth2Token(GighubConfigProperties.getString("GithubRepositoryDAO.TOKEN")); 
		}
		return client;
	}
}
