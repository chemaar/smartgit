package es.inf.uc3m.kr.smartgit.dao.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.service.GitHubService;

import es.inf.uc3m.kr.smartgit.dao.LinkTO;

public interface GithubDumperEntityDAO {

	public static final String USER_LOGIN_PARAM = "login";
	public static final String ALL_USER_LOGIN_PARAM = "logins";
	public static final String REPO_CONSTANT_PARAM = "repo";
	public static final String MILESTONE_STATE_CONSTANT_PARAM = "state";
	
	public GitHubService getService();
	public Enum[] getFields();
	public void serialize(Map<String, Object> params) throws Exception;
	public List<Map<Enum, String>> getDescription(Map<String, Object> params) throws IOException;
	public List<LinkTO> getLinks();

	public void setLinks(List<LinkTO> links) ;
}
