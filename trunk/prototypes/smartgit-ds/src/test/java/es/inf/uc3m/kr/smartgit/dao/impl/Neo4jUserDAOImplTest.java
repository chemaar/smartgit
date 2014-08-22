package es.inf.uc3m.kr.smartgit.dao.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.service.UserService;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDataSerializer;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class Neo4jUserDAOImplTest {

	@Test
	public void test() throws Exception {
	
		DataSerializer serializer = new Neo4jDataSerializer(RelTypes.USER_NODE,true);
		List<String> logins = new LinkedList<String>();
		logins.add("chemaar");
		UserService service = new UserService(GithubConnectionHelper.createConnection());
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.ALL_USER_LOGIN_PARAM,logins);
		GithubDumperEntityDAO dao = new GithubUserDAOImpl(service, serializer);
		dao.serialize(params);


	}

}
