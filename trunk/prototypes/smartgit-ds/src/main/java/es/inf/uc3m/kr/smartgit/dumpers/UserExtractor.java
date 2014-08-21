package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import es.inf.uc3m.kr.smartgit.GighubConfigProperties;

public class UserExtractor {
	
	public static void main(String []args) throws FileNotFoundException{
		//Until 5 000 0000=135*x 37K~~
		int TRIES = 10000;
		int since = 0;
		int MAX_FILE = 135*3;
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		PrintWriter pw = new PrintWriter(new File("dumps/"+since+"-users-login-id.properties"));
		
		for(int tries = 0; tries<TRIES; tries++){
			System.out.println("SINCE: "+since);
			if(since>MAX_FILE){
				pw.close();
				pw = null;
				pw = new PrintWriter(new File("dumps/"+since+"-users-login-id.properties"));
			}
			String uri = "https://api.github.com/users?since="+since;
			//System.out.println("Try "+tries+" with URI "+uri);	
			WebResource  service = client.resource(UriBuilder.fromUri(uri).build());
			String json = service.
					header("Authorization", "token "+ GighubConfigProperties.getString("GithubRepositoryDAO.TOKEN")).
					accept(MediaType.APPLICATION_JSON).
					get(String.class);
		     
			JSONArray list = new JSONArray(json);
			int lastId = since;
			for (int i = 0; i < list.length(); i++){
				JSONObject user = (JSONObject)list.get(i);
				pw.println(user.get("login")+"="+user.get("id"));
				lastId=(int) user.get("id");
			}
			since = lastId;
		}

	}

}
