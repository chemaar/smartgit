package es.inf.uc3m.kr.smartgit.dao.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Assert;
import org.junit.Test;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;

public class FileDownloadDAOImplTest {

	@Test
	public void test() throws Exception {
		String DUMP_FILE="download-dump.txt";
		DataSerializer serializer = new FileDataSerializer(DUMP_FILE);
		DownloadService service = new DownloadService(GithubConnectionHelper.createConnection());
		RepositoryService repositoryService = new RepositoryService(GithubConnectionHelper.createConnection());
		GithubDumperEntityDAO dao = new GithubDownloadDAOImpl(service, serializer);
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repositoryService.getRepositories().get(0));
		dao.serialize(params);
		Assert.assertTrue(new File(DUMP_FILE).exists());
	}

}
