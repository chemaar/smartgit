package es.inf.uc3m.kr.smartgit.dao.impl;

import java.util.Map;

import org.eclipse.egit.github.core.service.GitHubService;

public interface GithubDumperEntityDAO {

	public static final String USER_LOGIN_PARAM = "login";
	public static final String ALL_USER_LOGIN_PARAM = "logins";
	public static final String REPO_CONSTANT_PARAM = "repo";
	public static final String MILESTONE_STATE_CONSTANT_PARAM = "state";
	
	public GitHubService getService();
	public Enum[] getFields();
	public void serialize(Map<String, Object> params) throws Exception;

}
