package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.service.GitHubService;

public interface GitHubDumper {

	public static final String USER_LOGIN_PARAM = "login";
	public static final String REPO_CONSTANT_PARAM = "repo";
	
	public GitHubService getService();
	public Enum[] getFields();
	public List<Map<Enum, String>> createDump() throws IOException;
	public List<Map<Enum, String>> createDump(Map<String,Object> params) throws IOException;
}
