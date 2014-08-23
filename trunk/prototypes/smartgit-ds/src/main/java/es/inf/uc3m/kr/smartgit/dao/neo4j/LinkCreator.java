package es.inf.uc3m.kr.smartgit.dao.neo4j;

import java.util.List;

import es.inf.uc3m.kr.smartgit.to.LinkTO;

public interface LinkCreator {

	public boolean createLinks(List<LinkTO> links);
}
