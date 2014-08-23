package es.inf.uc3m.kr.smartgit.dao.neo4j;

import java.util.List;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

import es.inf.uc3m.kr.smartgit.dao.LinkTO;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class RepoOwnerLinkCreator implements LinkCreator{

	protected static Logger logger = Logger.getLogger(RepoOwnerLinkCreator.class);
	private GraphDatabaseService graphService;
	
	@Override
	public boolean createLinks(List<LinkTO> links) {
		boolean allCreated = true;
		this.graphService = Neo4jDatabaseConnector.getGraphDatabaseService();
		for(LinkTO link:links){
			if(link.idFrom!=null && link.idTo!=null && link.relation!=null){
				logger.debug("Creating link from repo "+link.idFrom+" to owner "+link.idTo);
				allCreated = allCreated && this.linkRepositoryOwner(link.idFrom, link.idTo);
			}
	}
		//Neo4jDatabaseConnector.returnGraphDatabaseService(this.graphService);
		return allCreated;
	}
	//Creation of links

	public boolean linkRepositoryOwner(String idRepository, String loginOwner){
		long internalIdRepository = Neo4jLinkUtils.lookForInternalRepositoryId(idRepository, graphService);
		long internalIdOwner =  Neo4jLinkUtils.lookForInternalUserId(loginOwner,graphService);		
		return Neo4jLinkUtils.createLink(internalIdRepository, internalIdOwner, RelTypes.OWNER,graphService);
	}

	public boolean linkRepositoryOther(String idRepository, String otherId,RelTypes relType){
		long internalIdRepository = Neo4jLinkUtils.lookForInternalRepositoryId(idRepository,graphService);
		long otherInternalId =  Neo4jLinkUtils.lookForInternalUserId(otherId,graphService);		
		return Neo4jLinkUtils.createLink(internalIdRepository, otherInternalId, relType,graphService);
	}

	

}
