package es.inf.uc3m.kr.smartgit;

import org.eclipse.egit.github.core.client.GitHubClient;

public class GithubConnectionHelper {

	public static GitHubClient createConnection() {
		GitHubClient client = new GitHubClient();
		client.setOAuth2Token(GighubConfigProperties.getString("GithubRepositoryDAO.TOKEN")); 
		return client;
	}
}
