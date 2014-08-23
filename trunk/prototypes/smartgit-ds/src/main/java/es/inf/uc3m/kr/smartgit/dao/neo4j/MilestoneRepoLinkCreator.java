package es.inf.uc3m.kr.smartgit.dao.neo4j;

import org.apache.log4j.Logger;

import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class MilestoneRepoLinkCreator extends LinkCreatorAdapter{

	protected static Logger logger = Logger.getLogger(MilestoneRepoLinkCreator.class);

	protected boolean link(String idFrom, String idTo,  RelTypes relation) {
		long internalIdRepository = Neo4jLinkUtils.lookForInternalRepositoryId(idFrom, getGraphService());		
		long internalIdMilestone = Neo4jLinkUtils.lookForInternalMilestoneId(idTo, getGraphService());
		return Neo4jLinkUtils.createLink(internalIdRepository,internalIdMilestone, relation,getGraphService());
	}

	
	

}
