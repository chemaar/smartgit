package es.inf.uc3m.kr.smartgit.graph.loader;

import org.gephi.io.importer.api.Container;

public interface GraphLoader {

	public Container load(String from);
	public Container getContainer();
}
