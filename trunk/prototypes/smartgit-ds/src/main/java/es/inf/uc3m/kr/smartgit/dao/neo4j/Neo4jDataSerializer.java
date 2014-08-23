package es.inf.uc3m.kr.smartgit.dao.neo4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neo4j.cypher.javacompat.ExecutionEngine;
import org.neo4j.cypher.javacompat.ExecutionResult;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.helpers.collection.IteratorUtil;

import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.to.LinkTO;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class Neo4jDataSerializer implements DataSerializer {

	protected static Logger logger = Logger.getLogger(Neo4jDataSerializer.class);
	private GraphDatabaseService graphService;
	private RelTypes relType;
	private boolean linkToRoot = Boolean.FALSE;

	public Neo4jDataSerializer(RelTypes relType, boolean linkToRoot){
		this.relType = relType;
		this.linkToRoot = linkToRoot;
	}

	@Override
	public void serialize(List<Map<Enum, String>> csvData, Enum[] fields) throws IOException {
		this.graphService = Neo4jDatabaseConnector.getGraphDatabaseService();
		internalSerialize(csvData, fields);
		Neo4jDatabaseConnector.returnGraphDatabaseService(this.graphService);

	}

	private void internalSerialize(List<Map<Enum, String>> csvData,
			Enum[] fields) throws IOException {
		if(linkToRoot){
			Neo4jSerializeUtils.genericRootSerialize(this.graphService, fields, 
					this.relType, csvData);
		}else{
			Neo4jSerializeUtils.genericSerialize(this.graphService, fields,
					this.relType, csvData);
		}
	}

	@Override
	public void serialize(List<Map<Enum, String>> csvData, Enum[] fields, List<LinkTO> links, LinkCreator linkCreator) throws IOException {
		this.graphService = Neo4jDatabaseConnector.getGraphDatabaseService();
		logger.debug("Serialize normal "+this.getClass().getCanonicalName());
		internalSerialize(csvData, fields);
		logger.debug("Serialize links "+this.getClass().getCanonicalName());
		if(links!=null && links.size()!=0 && linkCreator!=null){
			System.out.println("Starting links "+links.size());
			linkCreator.createLinks(links);
			System.out.println("Ending links");
		}
		logger.debug("End serialize normal "+this.getClass().getCanonicalName());
		//Neo4jDatabaseConnector.returnGraphDatabaseService(this.graphService);
	}




}
