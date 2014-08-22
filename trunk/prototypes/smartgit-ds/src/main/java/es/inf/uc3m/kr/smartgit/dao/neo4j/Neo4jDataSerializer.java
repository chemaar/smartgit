package es.inf.uc3m.kr.smartgit.dao.neo4j;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;

import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class Neo4jDataSerializer implements DataSerializer {

	private GraphDatabaseService graphService;
	private RelTypes relType;
	private boolean isRoot = Boolean.FALSE;

	public Neo4jDataSerializer(GraphDatabaseService graphService, RelTypes relType, boolean isRoot){
		this.graphService = graphService;
		this.relType = relType;
		this.isRoot = isRoot;
	}
	
	@Override
	public void serialize(List<Map<Enum, String>> csvData, Enum[] fields) throws IOException {
		this.graphService = Neo4jDatabaseConnector.getGraphDatabaseService();
		if(isRoot){
			Neo4jSerializeUtils.genericRootSerialize(this.graphService, fields, 
					this.relType, csvData);
		}else{
			Neo4jSerializeUtils.genericSerialize(this.graphService, fields,
					this.relType, csvData);
		}
		
		Neo4jDatabaseConnector.returnGraphDatabaseService(this.graphService);

	}

}
