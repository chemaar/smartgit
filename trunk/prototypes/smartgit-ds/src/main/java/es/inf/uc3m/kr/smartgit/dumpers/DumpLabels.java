package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Label;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.LabelService;
import org.eclipse.egit.github.core.service.RepositoryService;

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;

public class DumpLabels implements GitHubDumper {
	protected static Logger logger = Logger.getLogger(DumpLabels.class);
	LabelService service;

	public DumpLabels(){
	}



	public List<Map<Enum,String>> createDump(Map<String, Object> params) throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		try{
			IRepositoryIdProvider repo = (IRepositoryIdProvider) params.get(REPO_CONSTANT_PARAM);
			List<Label> labels = ((LabelService) getService()).getLabels(repo);
			long repoID = ((Repository)repo).getId();
			for(Label label: labels){
				//In case of needing memory, directly write here to a file...
				csvData.add(describe(label,repoID));
			}
		}catch(Exception e){
			logger.error(e);
			throw e;
		}

		return csvData;
	}

	private Map<Enum,String> describe(Label label, long id) {
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(LabelFields.Type, LabelFields.Label.name());
		values.put(LabelFields.ID_Repo,""+id); 
		values.put(LabelFields.Name,label.getName());
		values.put(LabelFields.Color,label.getColor());
		values.put(LabelFields.URL,label.getUrl());
		return values;
	}


	@Override
	public GitHubService getService() {
		if(this.service == null){
			this.service = new LabelService(GithubConnectionHelper.createConnection());
		}
		return this.service;
	}

	@Override
	public Enum[] getFields() {
		return LabelFields.values();
	}


	public static void main(String []args) throws IOException{
		String DUMP_FILE="labels-dump";
		DumpRepository dumpRepository = new DumpRepository();
		RepositoryService repositoryService = (RepositoryService) dumpRepository.getService();
		GitHubDumper dumper = new DumpLabels();
		Map<String, Object> params = new HashMap<String,Object>();
		for(Repository repo:repositoryService.getRepositories()){
			logger.info("Processing repository with ID= "+repo.getId());
			params.put(REPO_CONSTANT_PARAM,repo);
			DumperSerializer.serialize(dumper, DUMP_FILE+"-"+repo.getId()+".txt",params);
			params.clear();
		}


	}

	@Override
	public List<Map<Enum, String>> createDump() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
