package es.inf.uc3m.kr.smartgit.graph.loader;

import java.io.File;

import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.EdgeDefault;
import org.gephi.io.importer.api.ImportController;
import org.openide.util.Lookup;

import es.inf.uc3m.kr.smartgit.exceptions.GephiLoaderException;

public class GephiFileResourceLoaderImpl implements GraphLoader{
	ImportController importController = Lookup.getDefault().lookup(ImportController.class);
	Container container = null;
	@Override
	public Container load(String from) {
		try {
			File file = new File(Thread.currentThread().getContextClassLoader().getResource(from).toURI());
			this.container = importController.importFile(file);
			this.container.getLoader().setEdgeDefault(EdgeDefault.DIRECTED);   //Force DIRECTED
		} catch (Exception e) {
			throw new GephiLoaderException(e);
		}
		return this.container;
	}
	
	@Override
	public Container getContainer() {
		return this.container;
	}
}
