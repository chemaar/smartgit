package es.inf.uc3m.kr.smartgit.dao.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.to.LinkTO;
import es.inf.uc3m.kr.smartgit.dao.neo4j.LinkCreator;

public abstract class GithubDumperEntityDAOAdapter implements GithubDumperEntityDAO {

	private DataSerializer serializer;
	private LinkCreator linkCreator;
	private List<LinkTO> links = new LinkedList<LinkTO>();
	
	@Override
	public void serialize(Map<String, Object> params) throws Exception {
		System.out.println("Getting particular description");
		List<Map<Enum, String>> csvData = getDescription(params);
		System.out.println("Serialize normal "+this.getClass().getCanonicalName());
		this.serializer.serialize(csvData,this.getFields(),this.getLinks(),this.linkCreator);
		System.out.println("End Serialize normal"+this.getClass().getCanonicalName());
		
	}

	public DataSerializer getSerializer() {
		return serializer;
	}

	public void setSerializer(DataSerializer serializer) {
		this.serializer = serializer;
	}

	public List<LinkTO> getLinks() {
		return links;
	}

	public void setLinks(List<LinkTO> links) {
		this.links = links;
	}

	public LinkCreator getLinkCreator() {
		return linkCreator;
	}

	public void setLinkCreator(LinkCreator linkCreator) {
		this.linkCreator = linkCreator;
	}
	
	

	
}
