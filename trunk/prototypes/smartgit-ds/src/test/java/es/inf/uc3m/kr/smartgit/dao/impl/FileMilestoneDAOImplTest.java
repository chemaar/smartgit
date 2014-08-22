package es.inf.uc3m.kr.smartgit.dao.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.egit.github.core.service.MilestoneService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Assert;
import org.junit.Test;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;

public class FileMilestoneDAOImplTest {

	@Test
	public void test() throws Exception {
		String DUMP_FILE="milestone-dump.txt";
		DataSerializer serializer = new FileDataSerializer(DUMP_FILE);
		MilestoneService service = new MilestoneService(GithubConnectionHelper.createConnection());
		RepositoryService repositoryService = new RepositoryService(GithubConnectionHelper.createConnection());
		GithubDumperEntityDAO dao = new GithubMilestoneDAOImpl(service, serializer);
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repositoryService.getRepositories().get(0));
		dao.serialize(params);
		Assert.assertTrue(new File(DUMP_FILE).exists());
	}

}
