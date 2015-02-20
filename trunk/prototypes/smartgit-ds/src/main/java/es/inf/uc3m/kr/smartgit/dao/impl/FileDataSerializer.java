package es.inf.uc3m.kr.smartgit.dao.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import main.FileMainAggregatedFullUserDescribe;

import org.apache.log4j.Logger;

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.dao.fields.LinkFields;
import es.inf.uc3m.kr.smartgit.dao.neo4j.LinkCreator;
import es.inf.uc3m.kr.smartgit.to.LinkTO;

public class FileDataSerializer implements DataSerializer {

	protected static Logger logger = Logger.getLogger(FileDataSerializer.class);
	private String filename;
	private String linksFileName;

	public FileDataSerializer(String filename, String linksFileName){
		this.filename = filename;
		this.linksFileName = linksFileName;
	}
	
	@Override
	public void serialize(List<Map<Enum, String>> csvData, Enum[] fields) throws IOException {
		DumperSerializer.write(filename,csvData,fields);
		
	}

	@Override
	public void serialize(List<Map<Enum, String>> csvData, Enum[] fields,
			List<LinkTO> links, LinkCreator linkCreator) throws IOException {
		DumperSerializer.write(filename,csvData,fields);
		logger.debug("Creating links...");
		List<Map<Enum,String>> linksMaps = new LinkedList<Map<Enum,String>>();
		for(LinkTO link: links){
			Map<Enum,String> values = new HashMap<Enum,String>();
			values.put(LinkFields.ID_FROM, String.valueOf(link.from));
			values.put(LinkFields.ID_TO,  String.valueOf(link.to));
			values.put(LinkFields.FROM_STR, link.idFrom);
			values.put(LinkFields.TO_STR, link.idTo);
			values.put(LinkFields.RELATION, link.relation.name());
			linksMaps.add(values);
		}
		
		DumperSerializer.write(linksFileName,linksMaps,LinkFields.values());
		
	}



}
