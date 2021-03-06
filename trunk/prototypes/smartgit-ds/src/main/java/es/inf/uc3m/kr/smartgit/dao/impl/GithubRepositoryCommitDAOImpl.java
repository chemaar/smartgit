package es.inf.uc3m.kr.smartgit.dao.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.Commit;
import org.eclipse.egit.github.core.CommitUser;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.MilestoneService;

import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.DataSerializer;
import es.inf.uc3m.kr.smartgit.to.LinkTO;
import es.inf.uc3m.kr.smartgit.dao.fields.CommitFields;
import es.inf.uc3m.kr.smartgit.dao.neo4j.LinkCreator;
import es.inf.uc3m.kr.smartgit.dao.neo4j.Neo4jDatabaseConnector.RelTypes;

public class GithubRepositoryCommitDAOImpl extends GithubDumperEntityDAOAdapter  {

	protected static Logger logger = Logger.getLogger(GithubRepositoryCommitDAOImpl.class);

	private CommitService service;

	protected static int MAX_COMMITS = 100;


	public GithubRepositoryCommitDAOImpl(CommitService service, DataSerializer serializer, LinkCreator linkCreator){
		this.service = service;
		setSerializer(serializer);
		setLinkCreator(linkCreator);
	}


	public GithubRepositoryCommitDAOImpl(CommitService service, DataSerializer serializer){
		this.service = service;
		setSerializer(serializer);
	}

	public List<Map<Enum,String>> getDescription(Map<String, Object> params) throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		try{
			IRepositoryIdProvider repo = (IRepositoryIdProvider) params.get(REPO_CONSTANT_PARAM);
			List<RepositoryCommit> commits = ((CommitService) getService()).getCommits(repo);
			long repoID = ((Repository)repo).getId();
			logger.debug("The repository with id "+repoID+" has "+commits.size()+" commits.");
			int ncommits = 0;
			if(commits != null){
				RepositoryCommit commit;
				for (int i = 0; ncommits<MAX_COMMITS && i<commits.size();i++){
					commit = commits.get(i);
					//In case of needing memory, directly write here to a file...
					csvData.add(describe(commit,repoID));
					LinkTO link = new LinkTO();
					link.idFrom = String.valueOf(repoID);
					link.idTo = commit.getSha();
					link.relation = RelTypes.HAS_COMMIT;
					getLinks().add(link);
					ncommits++;
				}
				commits.clear();
				commits = null;
			}
			
		}catch(Exception e){
			logger.error(e);
			throw e;
		}

		return csvData;
	}

	private Map<Enum,String> describe(RepositoryCommit commit, long id) {
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(CommitFields.Type, CommitFields.Commit.name());
		values.put(CommitFields.ID_Repo,""+id); 
		values.put(CommitFields.SHA,commit.getSha());
		values.put(CommitFields.Login,(commit.getCommitter()!=null?commit.getCommitter().getLogin():null));
		values.put(CommitFields.URL,commit.getUrl());
		values.put(CommitFields.Files,""+(commit.getFiles()!=null?commit.getFiles().size():null));
		Commit commitValue = commit.getCommit();
		if(commitValue !=null){
			values.put(CommitFields.SHA_COMMIT,commitValue.getSha());
			CommitUser author = commitValue.getAuthor();
			if(author!=null){
				values.put(CommitFields.Email,author.getEmail());
				values.put(CommitFields.Name,author.getName());
				values.put(CommitFields.Date,(author.getDate()!=null?author.getDate().toString():null));
			}else{
				values.put(CommitFields.Email,null);
				values.put(CommitFields.Name,null);
				values.put(CommitFields.Date,null);
			}
			values.put(CommitFields.N_Comments,""+commitValue.getCommentCount());
			values.put(CommitFields.Message,commitValue.getMessage());
			values.put(CommitFields.URL_Commit,commitValue.getUrl());
		}else{
			//Is it necessary?

		}



		return values;
	}


	@Override
	public GitHubService getService() {
		if(this.service == null){
			this.service = new CommitService(GithubConnectionHelper.createConnection());
		}
		return this.service;
	}

	@Override
	public Enum[] getFields() {
		return CommitFields.values();
	}


}
