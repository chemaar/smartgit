package es.inf.uc3m.kr.smartgit.dao.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Assert;
import org.junit.Test;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;

public class FileLabelDAOImplTest {

	@Test
	public void test() throws Exception {
		String DUMP_FILE="label-dump.txt";
		DataSerializer serializer = new FileDataSerializer(DUMP_FILE,"FIXME");
		LabelService service = new LabelService(GithubConnectionHelper.createConnection());
		RepositoryService repositoryService = new RepositoryService(GithubConnectionHelper.createConnection());
		GithubDumperEntityDAO dao = new GithubLabelDAOImpl(service, serializer);
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repositoryService.getRepositories().get(0));
		dao.serialize(params);
		Assert.assertTrue(new File(DUMP_FILE).exists());
	}

}
