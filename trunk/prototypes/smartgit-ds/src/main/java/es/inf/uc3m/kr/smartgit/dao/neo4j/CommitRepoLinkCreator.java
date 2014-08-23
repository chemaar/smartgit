package es.inf.uc3m.kr.smartgit.dao.neo4j;

import org.apache.log4j.Logger;

import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class CommitRepoLinkCreator extends LinkCreatorAdapter{

	protected static Logger logger = Logger.getLogger(CommitRepoLinkCreator.class);
	

	protected boolean link(String idFrom, String idTo,  RelTypes relation) {
		long internalIdRepository = Neo4jLinkUtils.lookForInternalRepositoryId(idFrom, getGraphService());
		long internalIdCommit =  Neo4jLinkUtils.lookForInternalCommitId(idTo,getGraphService());		
		logger.debug("Creating link of type  "+relation+" between repo: "+internalIdRepository+" commit: "+internalIdCommit);
		return Neo4jLinkUtils.createLink(internalIdRepository, internalIdCommit, relation,getGraphService());
	}

	
	

}
