package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Download;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.service.DownloadService;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.RepositoryService;

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;

public class DumpDownloads implements GitHubDumper {
	protected static Logger logger = Logger.getLogger(DumpDownloads.class);
	DownloadService service;

	public DumpDownloads(){
	}


	
	public List<Map<Enum,String>> createDump(Map<String, Object> params) throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		try{
			IRepositoryIdProvider repo = (IRepositoryIdProvider) params.get(REPO_CONSTANT_PARAM);
			List<Download> downloads = ((DownloadService) getService()).getDownloads(repo);
			long repoID = ((Repository)repo).getId();
			for(Download download: downloads){
				//In case of needing memory, directly write here to a file...
				csvData.add(describe(download,repoID));
			}
		}catch(Exception e){
			logger.error(e);
			throw e;
		}
		
		return csvData;
	}

	private Map<Enum,String> describe(Download download, long id) {
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(DownloadFields.Type, DownloadFields.Download.name());
		values.put(DownloadFields.ID_Repo,""+id); 
		values.put(DownloadFields.ID,""+download.getId());
		values.put(DownloadFields.Name,download.getName());
		values.put(DownloadFields.Description,download.getDescription());
		values.put(DownloadFields.URL,download.getUrl());
		values.put(DownloadFields.Content_Type,download.getContentType());
		values.put(DownloadFields.Count,""+download.getDownloadCount());
		values.put(DownloadFields.HTML_URL,download.getHtmlUrl());
		values.put(DownloadFields.Size,""+download.getSize());
			return values;
	}

	
	@Override
	public GitHubService getService() {
		if(this.service == null){
			this.service = new DownloadService(GithubConnectionHelper.createConnection());
		}
		return this.service;
	}

	@Override
	public Enum[] getFields() {
		return DownloadFields.values();
	}

	
	public static void main(String []args) throws IOException{
		String DUMP_FILE="downloads-dump";
		DumpRepository dumpRepository = new DumpRepository();
		RepositoryService repositoryService = (RepositoryService) dumpRepository.getService();
		GitHubDumper dumper = new DumpDownloads();
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
