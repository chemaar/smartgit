package es.inf.uc3m.kr.smartgit.dao.neo4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class Neo4jSerializeUtils {

	protected static Logger logger = Logger.getLogger(Neo4jSerializeUtils.class);
	public static void genericSerialize(GraphDatabaseService graphDb, Enum[] fields, RelTypes type, List<Map<Enum, String>> propertyValues) throws IOException{
		try ( Transaction tx = graphDb.beginTx() ){
			logger.debug("Creating nodes of type "+type);
			for(Map<Enum, String> repoData:propertyValues){
				Node currentNode = graphDb.createNode();
				currentNode.addLabel(DynamicLabel.label(type.name()));
				for(int i = 0; i<fields.length;i++){
					Enum property = fields[i];
					String value = repoData.get(fields[i]);
					currentNode.setProperty( property.name(), (value==null?"N/A":value) );
					value = null;
				}
				currentNode = null;
			}
			tx.success();
			tx.close();
		}
	}
	
	public static void genericRootSerialize(GraphDatabaseService graphDb, Enum[] fields , RelTypes type, List<Map<Enum, String>> propertyValues) throws IOException{
		try ( Transaction tx = graphDb.beginTx() ){
			Node github =  graphDb.getNodeById( Neo4jDatabaseConnector.getInitialNode() );
			for(Map<Enum, String> repoData:propertyValues){
				Node currentNode = graphDb.createNode();
				currentNode.addLabel(DynamicLabel.label(type.name()));
				for(int i = 0; i<fields.length;i++){
					Enum property = fields[i];
					String value = repoData.get(fields[i]);
					currentNode.setProperty( property.name(), (value==null?"N/A":value) );
					value = null;
				}
				github.createRelationshipTo(currentNode,type);
				currentNode = null;
			}
			tx.success();
			tx.close();
		}
	}
	
}
