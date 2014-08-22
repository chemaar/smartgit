package es.inf.uc3m.kr.smartgit.dao;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DataSerializer {

	public void serialize(List<Map<Enum, String>> csvData, Enum[] fields) throws IOException;

}
