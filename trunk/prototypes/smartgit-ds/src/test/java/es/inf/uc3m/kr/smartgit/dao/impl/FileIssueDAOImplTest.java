package es.inf.uc3m.kr.smartgit.dao.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Assert;
import org.junit.Test;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;

public class FileIssueDAOImplTest {

	@Test
	public void test() throws Exception {
		String DUMP_FILE="issue-dump.txt";
		DataSerializer serializer = new FileDataSerializer(DUMP_FILE);
		IssueService service = new IssueService(GithubConnectionHelper.createConnection());
		RepositoryService repositoryService = new RepositoryService(GithubConnectionHelper.createConnection());
		GithubDumperEntityDAO dao = new GithubIssueDAOImpl(service, serializer);
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repositoryService.getRepositories().get(0));
		dao.serialize(params);
		Assert.assertTrue(new File(DUMP_FILE).exists());
	}

}
