package es.inf.uc3m.kr.smartgit.dao.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.UserService;
import org.junit.Assert;
import org.junit.Test;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;

public class FileRepositoryDAOImplTest {

	@Test
	public void test() throws Exception {
		String DUMP_FILE="repository-dump.txt";
		DataSerializer serializer = new FileDataSerializer(DUMP_FILE);
		List<String> logins = new LinkedList<String>();
		logins.add("chemaar");
		RepositoryService service = new RepositoryService(GithubConnectionHelper.createConnection());
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.ALL_USER_LOGIN_PARAM,logins);
		GithubDumperEntityDAO dao = new GithubRepositoryDAOImpl(service, serializer);
		dao.serialize(params);
		Assert.assertTrue(new File(DUMP_FILE).exists());
	}

}
