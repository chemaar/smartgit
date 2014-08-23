package es.inf.uc3m.kr.smartgit.dao.impl.neo4j;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.junit.Test;
import org.neo4j.graphdb.GraphDatabaseService;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubDumperEntityDAO;
import es.inf.uc3m.kr.smartgit.dao.impl.GithubLabelDAOImpl;
import es.inf.uc3m.kr.smartgit.dao.neo4j.IssueRepoLinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.LabelRepoLinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDataSerializer;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector;
import es.inf.uc3m.kr.smartgit.dao.neo4j.RepoOwnerLinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class Neo4jLabelDAOImplTest {

	@Test
	public void test() throws Exception {
		//Firstly user and repos MUST exist
		RepositoryService repositoryService = new RepositoryService(GithubConnectionHelper.createConnection());
		//User already exist
		DataSerializer serializer = 
				new Neo4jDataSerializer(RelTypes.LABEL_NODE,false);
		List<String> logins = new LinkedList<String>();
		logins.add("chemaar");
		LabelService service = new LabelService(GithubConnectionHelper.createConnection());
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(GithubDumperEntityDAO.REPO_CONSTANT_PARAM,repositoryService.getRepositories().get(0));
		GithubDumperEntityDAO dao = new GithubLabelDAOImpl(service, serializer, new LabelRepoLinkCreator());
		dao.serialize(params);
	}

}
