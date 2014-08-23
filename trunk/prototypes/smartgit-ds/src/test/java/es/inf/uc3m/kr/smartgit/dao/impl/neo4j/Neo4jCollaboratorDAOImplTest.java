package es.inf.uc3m.kr.smartgit.dao.impl.neo4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.service.CollaboratorService;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Test;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubCollaboratorDAOImpl;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubDumperEntityDAO;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubRepositoryCommitDAOImpl;
import es.inf.uc3m.kr.smartgit.dao.neo4j.CommitRepoLinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDataSerializer;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class Neo4jCollaboratorDAOImplTest {

	@Test
	public void test() throws Exception {
		throw new Exception("Not yet implemented");
		//Firstly user and repos MUST exist
//		RepositoryService repositoryService = new RepositoryService(GithubConnectionHelper.createConnection());
//		//User already exist
//		DataSerializer serializer = 
//				new Neo4jDataSerializer(RelTypes.REPO_NODE,false);
//		List<String> logins = new LinkedList<String>();
//		logins.add("chemaar");
//		CollaboratorService service = new CollaboratorService(GithubConnectionHelper.createConnection());
//		Map<String, Object> params = new HashMap<String,Object>();
//		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repositoryService.getRepositories().get(0));
//		GithubDumperEntityDAO dao = new GithubCollaboratorDAOImpl(service, serializer);
//		dao.serialize(params);
	}

}
