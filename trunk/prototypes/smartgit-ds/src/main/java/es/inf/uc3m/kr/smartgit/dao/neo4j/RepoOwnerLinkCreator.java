package es.inf.uc3m.kr.smartgit.dao.neo4j;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;

import es.inf.uc3m.kr.smartgit.dao.LinkTO;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class RepoOwnerLinkCreator implements LinkCreator{

	private GraphDatabaseService graphService;
	
	@Override
	public boolean createLinks(List<LinkTO> links) {
		boolean allCreated = true;
		this.graphService = Neo4jDatabaseConnector.getGraphDatabaseService();
		for(LinkTO link:links){
			if(link.idFrom!=null && link.idTo!=null && link.relation!=null){
				System.out.println("Creating link from repo "+link.idFrom+" to owner "+link.idTo);
				allCreated = allCreated && this.linkRepositoryOwner(link.idFrom, link.idTo);
			}
	}
		//Neo4jDatabaseConnector.returnGraphDatabaseService(this.graphService);
		return allCreated;
	}
	//Creation of links

	public boolean linkRepositoryOwner(String idRepository, String loginOwner){
		long internalIdRepository = lookForInternalRepositoryId(idRepository);
		long internalIdOwner =  lookForInternalUserId(loginOwner);		
		return this.createLink(internalIdRepository, internalIdOwner, RelTypes.OWNER);
	}

	public boolean linkRepositoryOther(String idRepository, String otherId,RelTypes relType){
		long internalIdRepository = lookForInternalRepositoryId(idRepository);
		long otherInternalId =  lookForInternalUserId(otherId);		
		return this.createLink(internalIdRepository, otherInternalId, relType);
	}


	public boolean createLink(long fromId, long toId, RelTypes relType){
		try ( Transaction tx = this.graphService.beginTx() ){
			Node from = this.graphService.getNodeById(fromId);
			Node to = this.graphService.getNodeById(toId);
			System.out.println("Creating link from "+from.getId()+"  "+to.getId());
			if(from != null && to !=null){
				from.createRelationshipTo(to,RelTypes.OWNER);
			}
			tx.success();
		}
		return Boolean.TRUE;
	}



	//Helper


	//FIXME: Control failures
	public long lookForInternalRepositoryId(String idRepository) {
		List<Long> ids = getInternalRepoIds(idRepository);
		return ids.get(0);
	}

	public long lookForInternalUserId(String loginOwner) {
		List<Long> ids = getInternalUserIds(loginOwner);
		return ids.get(0);
	}



	public List<Long> getInternalUserIds(String userLogin){
		String query = "match (n:USER_NODE) where n.Login={login} return id(n);";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "login", userLogin );
		return runQuery(this.graphService,query, params, "id(n)");
	}

	public List<Long> getInternalRepoIds(String repoId){
		String query = "match (n:REPO_NODE) where n.ID={id} return id(n);";
		Map<String, Object> params = new HashMap<String, Object>();
		params.put( "id", repoId );
		return runQuery(this.graphService,query, params, "id(n)");
	}

	private static List<Long> runQuery(GraphDatabaseService graphDb,String query, Map<String, Object> params,String var) {
		List<Long> internalIds = new LinkedList<Long>();
		ExecutionEngine engine = new ExecutionEngine(graphDb);
		try (Transaction tx = graphDb.beginTx()) {
			ExecutionResult result = engine.execute(query, params);
			// extract the data out of the result, you cannot iterate over it outside of a tx
			Iterator<Long> n_column = result.columnAs(var);
			for ( Long id : IteratorUtil.asIterable(n_column ) ) {
				internalIds.add(id);
			}
			tx.success();
		}
		return internalIds;
	}


}
