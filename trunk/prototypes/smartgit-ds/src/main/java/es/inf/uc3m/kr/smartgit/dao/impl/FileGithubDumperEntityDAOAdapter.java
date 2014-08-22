package es.inf.uc3m.kr.smartgit.dao.impl;

import java.util.List;
import java.util.Map;

import es.inf.uc3m.kr.smartgit.DumperSerializer;

public abstract class FileGithubDumperEntityDAOAdapter implements GithubDumperEntityDAO {

	private String filename;
	
	public void serialize(Map<String, Object> params) throws Exception{
		List<Map<Enum, String>> csvData = getDescription(params);
		DumperSerializer.write(getFileName(),csvData,this.getFields());
	}
	
	public String getFileName(){
		return this.filename;
	}
	
	public void setFileName(String filename){
		this.filename = filename;
	}

	
}
