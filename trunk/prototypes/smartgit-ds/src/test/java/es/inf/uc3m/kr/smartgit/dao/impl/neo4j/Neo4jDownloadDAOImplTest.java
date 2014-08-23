package es.inf.uc3m.kr.smartgit.dao.impl.neo4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubDownloadDAOImpl;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubDumperEntityDAO;
import es.inf.uc3m.kr.smartgit.dao.neo4j.DownloadRepoLinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDataSerializer;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector;
import es.inf.uc3m.kr.smartgit.dao.neo4j.RepoOwnerLinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class Neo4jDownloadDAOImplTest {

	@Test
	public void test() throws Exception {
		//Firstly user and repos MUST exist
		RepositoryService repositoryService = new RepositoryService(GithubConnectionHelper.createConnection());
		//User already exist
		DataSerializer serializer = 
				new Neo4jDataSerializer(RelTypes.DOWNLOAD_NODE,false);
		List<String> logins = new LinkedList<String>();
		logins.add("chemaar");
		DownloadService service = new DownloadService(GithubConnectionHelper.createConnection());
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repositoryService.getRepositories().get(0));
		GithubDumperEntityDAO dao = new GithubDownloadDAOImpl(service, serializer, new DownloadRepoLinkCreator());
		dao.serialize(params);
	}

}
