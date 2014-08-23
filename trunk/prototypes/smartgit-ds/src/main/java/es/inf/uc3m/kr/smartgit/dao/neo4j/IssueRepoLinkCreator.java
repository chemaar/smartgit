package es.inf.uc3m.kr.smartgit.dao.neo4j;

import org.apache.log4j.Logger;

import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class IssueRepoLinkCreator extends LinkCreatorAdapter{

	protected static Logger logger = Logger.getLogger(IssueRepoLinkCreator.class);
	
	protected boolean link(String idFrom, String idTo) {
		long internalIdIssue =  Neo4jLinkUtils.lookForInternalIssueId(idFrom,getGraphService());		
		long internalIdRepository = Neo4jLinkUtils.lookForInternalRepositoryId(idTo, getGraphService());
		return Neo4jLinkUtils.createLink(internalIdIssue, internalIdRepository, RelTypes.HAS_DOWNLOAD,getGraphService());
	}

	
	

}
