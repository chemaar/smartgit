package es.inf.uc3m.kr.smartgit.dao;

import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;


public class LinkTO {

	public String idFrom;
	public String idTo;
	public long from;
	public long to;
	public RelTypes relation;
	@Override
	public String toString() {
		return "LinkTO [idFrom=" + idFrom + ", idTo=" + idTo + ", from=" + from
				+ ", to=" + to + ", relation=" + relation + "]";
	}
	
	
}
