package es.inf.uc3m.kr.smartgit.dao.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDataSerializer;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector;
import es.inf.uc3m.kr.smartgit.dao.neo4j.RepoOwnerLinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class Neo4jRepositoryDAOImplTest {

	@Test
	public void test() throws Exception {
		//User already exist
		DataSerializer serializer = 
				new Neo4jDataSerializer(RelTypes.REPO_NODE,false);
		List<String> logins = new LinkedList<String>();
		logins.add("chemaar");
		RepositoryService service = new RepositoryService(GithubConnectionHelper.createConnection());
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.USER_LOGIN_PARAM,logins);
		GithubDumperEntityDAO dao = new GithubRepositoryDAOImpl(service, serializer, new RepoOwnerLinkCreator());
		dao.serialize(params);

	}

}
