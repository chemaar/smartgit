package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

import es.inf.uc3m.kr.smartgit.GighubConfigProperties;

public class UserExtractor {
	
	protected static Logger logger = Logger.getLogger(UserExtractor.class);
	public static void main(String []args) throws FileNotFoundException{
		//Until 5 000 0000=135*x->37K tries~~
		int TRIES = 5000; //Restriction of the Github API: 5000 per hour
		int since = 0; //Read from file to automate: 436403
		int MAX__PER_FILE = 10000;
		ClientConfig config = new DefaultClientConfig();
		Client client = Client.create(config);
		PrintWriter pw = new PrintWriter(new File("dumps/"+since+"-users-login-id.properties"));
		int written = 0;
		int total = 0;
		for(int tries = 0; tries<TRIES; tries++){
			logger.debug("SINCE: "+since+" written: "+written+" tries: "+tries);
			
		
			if(written>MAX__PER_FILE){
				total += written;
				pw.close();
				pw = null;
				pw = new PrintWriter(new File("dumps/"+total+"-users-login-id.properties"));
				written = 0;
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
				written++;
			}
			since = lastId;
		}

	}

}
