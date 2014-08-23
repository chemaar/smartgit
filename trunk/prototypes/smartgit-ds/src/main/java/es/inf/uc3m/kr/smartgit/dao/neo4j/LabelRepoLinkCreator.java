package es.inf.uc3m.kr.smartgit.dao.neo4j;

import org.apache.log4j.Logger;

import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class LabelRepoLinkCreator extends LinkCreatorAdapter{

	protected static Logger logger = Logger.getLogger(LabelRepoLinkCreator.class);

	protected boolean link(String idFrom, String idTo) {
		long internalIdLabel =  Neo4jLinkUtils.lookForInternalLabelId(idFrom,getGraphService());		
		long internalIdRepository = Neo4jLinkUtils.lookForInternalRepositoryId(idTo, getGraphService());
		return Neo4jLinkUtils.createLink(internalIdLabel, internalIdRepository, RelTypes.HAS_DOWNLOAD,getGraphService());
	}

	
	

}
