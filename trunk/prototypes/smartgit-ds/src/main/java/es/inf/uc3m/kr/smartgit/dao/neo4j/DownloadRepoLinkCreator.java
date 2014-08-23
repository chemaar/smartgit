package es.inf.uc3m.kr.smartgit.dao.neo4j;

import org.apache.log4j.Logger;

import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class DownloadRepoLinkCreator extends LinkCreatorAdapter{

	protected static Logger logger = Logger.getLogger(DownloadRepoLinkCreator.class);

	protected boolean link(String idFrom, String idTo) {
		long internalIdDownload =  Neo4jLinkUtils.lookForInternalDownloadId(idFrom,getGraphService());		
		long internalIdRepository = Neo4jLinkUtils.lookForInternalRepositoryId(idTo, getGraphService());
		return Neo4jLinkUtils.createLink(internalIdDownload, internalIdRepository, RelTypes.HAS_DOWNLOAD,getGraphService());
	}

	
	

}
