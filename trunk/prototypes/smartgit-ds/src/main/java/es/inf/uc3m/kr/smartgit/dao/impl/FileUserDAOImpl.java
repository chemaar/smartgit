package es.inf.uc3m.kr.smartgit.dao.impl;

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
import es.inf.uc3m.kr.smartgit.dumpers.UserFields;

public class FileUserDAOImpl implements GithubDumperEntityDAO {
	protected static Logger logger = Logger.getLogger(FileUserDAOImpl.class);
	
	private UserService service;
	private String filename;

	public FileUserDAOImpl(UserService service, String filename){
		this.service = service;
		this.filename = filename;
	}
	
	
	public void serialize(Map<String, Object> params) throws Exception{
		List<Map<Enum, String>> csvData = getDescription(params);
		DumperSerializer.write(this.filename,csvData,this.getFields());
	}


	protected List<Map<Enum, String>> getDescription(Map<String, Object> params)
			throws Exception {
		List<Map<Enum,String>> csvData = new LinkedList<>();
		try{
			UserService userService = (UserService) getService();
			List<String> allUserLogins = (List<String>) params.get(ALL_USER_LOGIN_PARAM);
			for(String login:allUserLogins){				
				User user = userService.getUser(login);
				if (user != null){
					csvData.add(describe(user));
				}
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
		values.put(UserFields.Collaborators,""+user.getCollaborators()); 
		values.put(UserFields.Company,user.getCompany()); 
		values.put(UserFields.Disk_Usage,""+user.getDiskUsage()); 
		values.put(UserFields.Email,user.getEmail()); 
		values.put(UserFields.Followers,""+user.getFollowers()); 
		values.put(UserFields.Following,""+user.getFollowing()); 
		values.put(UserFields.Gravatar_ID,user.getGravatarId()); 
		values.put(UserFields.Public_Repos,""+user.getPublicRepos()); 
		values.put(UserFields.Private_Repos,""+user.getTotalPrivateRepos()); 
		return values;
	}
	

	public Enum[] getFields() {
		return UserFields.values();
	}

	@Override
	public GitHubService getService() {
		if(this.service == null){
			this.service = new UserService(GithubConnectionHelper.createConnection());
		}
		return this.service;
	}
	
	
}
