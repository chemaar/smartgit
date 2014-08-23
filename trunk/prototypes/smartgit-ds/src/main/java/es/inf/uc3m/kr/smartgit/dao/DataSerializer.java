package es.inf.uc3m.kr.smartgit.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import es.inf.uc3m.kr.smartgit.dao.neo4j.LinkCreator;
import es.inf.uc3m.kr.smartgit.to.LinkTO;

public interface DataSerializer {

	public void serialize(List<Map<Enum, String>> csvData, Enum[] fields) throws IOException;
	void serialize(List<Map<Enum, String>> csvData, Enum[] fields,List<LinkTO> links, LinkCreator linkCreator) throws IOException;

}
