package es.inf.uc3m.kr.smartgit;

import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.jackson.JacksonFactory;

public class ProjectConfig {

	protected static Logger logger = Logger.getLogger(ProjectConfig.class);
	// Change this to your current project ID
	protected static final String PROJECT_NUMBER = "tribal-primacy-676";
	protected static final String PROJECT_ID = "tribal-primacy-676";

	// Load Client ID/secret from client_secrets.json file.
	protected static final String CLIENTSECRETS_LOCATION = "/client_secrets.json";
	protected static GoogleClientSecrets clientSecrets = loadClientSecrets();

	protected static final String TOKEN_PROPERTIES = "token.properties";
	public static final String REFRESH_TOKEN = "refreshtoken";
	protected static final String REDIRECT_URI = "urn:ietf:wg:oauth:2.0:oob";


	/**
	 *  Helper to load client ID/Secret from file.
	 */
	private static GoogleClientSecrets loadClientSecrets() {
		try {

			GoogleClientSecrets clientSecrets =
					GoogleClientSecrets.load(new JacksonFactory(),
							new InputStreamReader(BigQueryInstalledAuthDemo.class.getResourceAsStream(CLIENTSECRETS_LOCATION)));
			return clientSecrets;
		} catch (Exception e)  {
			logger.error("Could not load clientsecrets.json");
			e.printStackTrace();
		}
		return clientSecrets;
	}

}
