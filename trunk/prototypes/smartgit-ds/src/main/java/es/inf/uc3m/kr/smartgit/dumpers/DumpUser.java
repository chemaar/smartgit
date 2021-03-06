package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.GitHubService;
import org.eclipse.egit.github.core.service.UserService;

import es.inf.uc3m.kr.smartgit.DumperSerializer;
import es.inf.uc3m.kr.smartgit.GithubConnectionHelper;
import es.inf.uc3m.kr.smartgit.dao.fields.UserFields;

public class DumpUser implements GitHubDumper {
	protected static Logger logger = Logger.getLogger(DumpUser.class);

	UserService service;


	public DumpUser(){
	}

	public List<Map<Enum,String>> createDump() throws IOException{
		//FIXME: Implement
		List<Map<Enum,String>> csvData = new LinkedList<>();
		return csvData;
	}
	
	public List<Map<Enum,String>> createDump(Map<String, Object> params) throws IOException{
		List<Map<Enum,String>> csvData = new LinkedList<>();
		try{
			User user = ((UserService) getService()).getUser((String) params.get(USER_LOGIN_PARAM));
			if (user != null){
				csvData.add(describe(user));
			}
		}catch(Exception e){
			logger.error(e);
			throw e;
		}
		
		return csvData;
	}

	protected Map<Enum,String> describe(User user) {
		Map<Enum,String> values = new HashMap<Enum,String>();
		values.put(UserFields.Type, UserFields.User.name());
		values.put(UserFields.ID,""+user.getId()); 
		values.put(UserFields.Login,user.getLogin()); 
		values.put(UserFields.Name,user.getName()); 
		values.put(UserFields.Created,(user.getCreatedAt()!=null)?user.getCreatedAt().toString():null); 
		values.put(UserFields.Location,user.getLocation()); 
		values.put(UserFields.Avatar,user.getAvatarUrl()); 
		values.put(UserFields.Blog,user.getBlog()); 
		values.put(UserFields.Collaborators,String.valueOf(user.getCollaborators())); 
		values.put(UserFields.Company,user.getCompany()); 
		values.put(UserFields.Disk_Usage,String.valueOf(user.getDiskUsage())); 
		values.put(UserFields.Email,user.getEmail()); 
		values.put(UserFields.Followers,String.valueOf(user.getFollowers())); 
		values.put(UserFields.Following,String.valueOf(user.getFollowing())); 
		values.put(UserFields.Gravatar_ID,user.getGravatarId()); 
		values.put(UserFields.Public_Repos,String.valueOf(user.getPublicRepos())); 
		values.put(UserFields.Private_Repos,String.valueOf(user.getTotalPrivateRepos()));
		values.put(UserFields.Owned_Private_Repos,String.valueOf(user.getOwnedPrivateRepos())); 
		user.getOwnedPrivateRepos();
		if(user.getPlan() != null){
			values.put(UserFields.Plan_Collaborators, String.valueOf(user.getPlan().getCollaborators()));
			values.put(UserFields.Plan_Name, user.getPlan().getName());
			values.put(UserFields.Plan_Space, String.valueOf(user.getPlan().getSpace()));
		}
		
		return values;
	}

	
	@Override
	public GitHubService getService() {
		if(this.service == null){
			this.service = new UserService(GithubConnectionHelper.createConnection());
		}
		return this.service;
	}

	@Override
	public Enum[] getFields() {
		return UserFields.values();
	}


	
	public static void main(String []args) throws IOException{
		String DUMP_FILE="user-dump.txt";
		String login = "chemaar";
		logger.info("Creating dump for user with login: "+login);
		GitHubDumper dumper = new DumpUser();
		Map<String, Object> params = new HashMap<String,Object>();
		params.put(USER_LOGIN_PARAM,login);
		DumperSerializer.serialize(dumper, DUMP_FILE,params );

	}
}
