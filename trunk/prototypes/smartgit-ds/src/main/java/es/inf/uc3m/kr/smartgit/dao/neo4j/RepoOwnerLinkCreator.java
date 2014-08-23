package es.inf.uc3m.kr.smartgit.dao.neo4j;

import org.apache.log4j.Logger;

import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class RepoOwnerLinkCreator extends LinkCreatorAdapter{

	protected static Logger logger = Logger.getLogger(RepoOwnerLinkCreator.class);

	//Creation of links

	protected boolean link(String idFrom, String idTo){
		long internalIdRepository = Neo4jLinkUtils.lookForInternalRepositoryId(idFrom, getGraphService());
		long internalIdOwner =  Neo4jLinkUtils.lookForInternalUserId(idTo,getGraphService());		
		return Neo4jLinkUtils.createLink(internalIdRepository, internalIdOwner, RelTypes.OWNER,getGraphService());
	}

	public boolean linkRepositoryOther(String idRepository, String otherId,RelTypes relType){
		long internalIdRepository = Neo4jLinkUtils.lookForInternalRepositoryId(idRepository,getGraphService());
		long otherInternalId =  Neo4jLinkUtils.lookForInternalUserId(otherId,getGraphService());		
		return Neo4jLinkUtils.createLink(internalIdRepository, otherInternalId, relType,getGraphService());
	}


	

}
