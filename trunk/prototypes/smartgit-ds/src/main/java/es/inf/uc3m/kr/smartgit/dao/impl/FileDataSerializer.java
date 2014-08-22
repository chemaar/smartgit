package es.inf.uc3m.kr.smartgit.dao.impl;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.dao.LinkTO;
import es.inf.uc3m.kr.smartgit.dao.neo4j.LinkCreator;

public class FileDataSerializer implements DataSerializer {

	private String filename;

	public FileDataSerializer(String filename){
		this.filename = filename;
	}
	
	@Override
	public void serialize(List<Map<Enum, String>> csvData, Enum[] fields) throws IOException {
		DumperSerializer.write(filename,csvData,fields);
		
	}

	@Override
	public void serialize(List<Map<Enum, String>> csvData, Enum[] fields,
			List<LinkTO> links, LinkCreator linkCreator) throws IOException {
		
		
	}



}
