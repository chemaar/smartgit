package es.inf.uc3m.kr.smartgit.graph;

import java.util.LinkedList;
import java.util.List;

import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeModel;
import org.gephi.filters.api.FilterController;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.io.importer.api.ImportController;
import org.gephi.io.processor.plugin.DefaultProcessor;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.PreviewModel;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.ranking.api.RankingController;
import org.gephi.statistics.plugin.GraphDistance;
import org.openide.util.Lookup;

import es.inf.uc3m.kr.smartgit.graph.loader.GraphLoader;
import es.inf.uc3m.kr.smartgit.graph.to.MetricTO;

public class GraphMetricsService {

	GraphLoader loader;
	ProjectController pc;
	Workspace workspace;
	ImportController importController = Lookup.getDefault().lookup(ImportController.class);
	GraphModel graphModel;
	private AttributeModel attributeModel;
	

	
	public GraphMetricsService(GraphLoader loader){
		this.loader = loader;
		this.configureProject();
	}
	
	private void configureProject(){
		this.pc = Lookup.getDefault().lookup(ProjectController.class);
		this.pc.newProject();
		this.workspace = this.pc.getCurrentWorkspace();
		this.attributeModel = Lookup.getDefault().lookup(AttributeController.class).getModel();
		this.graphModel = Lookup.getDefault().lookup(GraphController.class).getModel();
//		PreviewModel model = Lookup.getDefault().lookup(PreviewController.class).getModel();
//		ImportController importController = Lookup.getDefault().lookup(ImportController.class);
//		FilterController filterController = Lookup.getDefault().lookup(FilterController.class);
//		RankingController rankingController = Lookup.getDefault().lookup(RankingController.class);
		//Append imported data to GraphAPI
		this.importController.process(this.loader.getContainer(), new DefaultProcessor(), workspace);
		

	}
	
	
	public List<MetricTO> calculateMetrics(){
		List<MetricTO> metrics = new LinkedList<MetricTO>();
		DirectedGraph graph = this.graphModel.getDirectedGraph(); //FIXME
		MetricTO nNodes = new MetricTO("Nº of nodes", "", "", graph.getNodeCount());
		MetricTO nEdges = new MetricTO("Nº of edges", "", "", graph.getEdgeCount());
		metrics.add(nNodes);
		metrics.add(nEdges);
		GraphDistance distance = new GraphDistance();
		distance.setDirected(true);
		distance.execute(this.graphModel, this.attributeModel );
		AttributeColumn centralityColumn = attributeModel.getNodeTable().getColumn(GraphDistance.BETWEENNESS);
		
//		Ranking centralityRanking = rankingController.getModel().getRanking(Ranking.NODE_ELEMENT, centralityColumn.getId());

		return metrics;
	}
}
