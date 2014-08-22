package es.inf.uc3m.kr.smartgit.dao.impl;

import java.util.List;
import java.util.Map;

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;

public abstract class GithubDumperEntityDAOAdapter implements GithubDumperEntityDAO {

	private DataSerializer serializer;
	
	@Override
	public void serialize(Map<String, Object> params) throws Exception {
		List<Map<Enum, String>> csvData = getDescription(params);
		this.serializer.serialize(csvData,this.getFields());
		//DumperSerializer.write(getFileName(),csvData,this.getFields());
		
	}

	public DataSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(DataSerializer serializer) {
		this.serializer = serializer;
	}
	
	

	
}
