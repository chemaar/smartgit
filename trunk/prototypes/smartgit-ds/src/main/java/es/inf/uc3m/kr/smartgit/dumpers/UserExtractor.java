package es.inf.uc3m.kr.smartgit.dumpers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class UserExtractor {
	
	public static void main(String []args) throws FileNotFoundException{
		PrintWriter pw = new PrintWriter(new File("users-login-id.properties"));
		//Until 5 000 0000
		int since = 0;
		ClientConfig config = new DefaultClientConfig();
		
		for(int tries = 0; tries<1; tries++){
			Client client = Client.create(config);
			String uri = "https://api.github.com/users?since="+since;
			//System.out.println("Try "+tries+" with URI "+uri);	
			WebResource service = 
					client.resource(UriBuilder.fromUri(uri).build());
			String json = service.accept(MediaType.APPLICATION_JSON).get(String.class);
			JSONArray list = new JSONArray(json);
			int lastId = since;
			for (int i = 0; i < list.length(); i++){
				JSONObject user = (JSONObject)list.get(i);
				pw.println(user.get("login")+"="+user.get("id"));
				lastId=(int) user.get("id");
			}
			since = lastId;
		}
		pw.close();

	}

}
