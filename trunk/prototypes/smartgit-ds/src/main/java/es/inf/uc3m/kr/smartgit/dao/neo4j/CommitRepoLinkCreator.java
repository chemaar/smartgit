package es.inf.uc3m.kr.smartgit.dao.neo4j;

import org.apache.log4j.Logger;

import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class CommitRepoLinkCreator extends LinkCreatorAdapter{

	protected static Logger logger = Logger.getLogger(CommitRepoLinkCreator.class);
	

	protected boolean link(String idFrom, String idTo) {
		long internalIdCommit =  Neo4jLinkUtils.lookForInternalCommitId(idFrom,getGraphService());		
		long internalIdRepository = Neo4jLinkUtils.lookForInternalRepositoryId(idTo, getGraphService());
		return Neo4jLinkUtils.createLink(internalIdCommit, internalIdRepository, RelTypes.HAS_DOWNLOAD,getGraphService());
	}

	
	

}
