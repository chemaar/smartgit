package es.inf.uc3m.kr.smartgit.dao.neo4j;

import java.util.List;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.GraphDatabaseService;

import es.inf.uc3m.kr.smartgit.dao.LinkTO;

public abstract class LinkCreatorAdapter  implements LinkCreator{

	private GraphDatabaseService graphService;
	
	protected static Logger logger = Logger.getLogger(LinkCreatorAdapter.class);
	@Override
	public boolean createLinks(List<LinkTO> links) {
		boolean allCreated = true;
		this.graphService = getGraphService();
		for(LinkTO link:links){
			if(link.idFrom!=null && link.idTo!=null && link.relation!=null){
				logger.debug("Creating link from repo "+link.idFrom+" to owner "+link.idTo);
				allCreated = allCreated && this.link(link.idFrom, link.idTo);
			}
	}
		//Neo4jDatabaseConnector.returnGraphDatabaseService(this.graphService);
		return allCreated;
	}
	
	protected GraphDatabaseService getGraphService(){
		if (this.graphService==null){
			this.graphService = Neo4jDatabaseConnector.getGraphDatabaseService();
		}
		return this.graphService;
	}
	protected abstract boolean link(String idFrom, String idTo);
}
