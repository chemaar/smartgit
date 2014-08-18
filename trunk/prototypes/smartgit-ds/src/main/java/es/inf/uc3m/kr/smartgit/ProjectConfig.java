package es.inf.uc3m.kr.smartgit;

import java.io.InputStreamReader;

import org.apache.log4j.Logger;

import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.json.jackson.JacksonFactory;

public class ProjectConfig {

	protected static Logger logger = Logger.getLogger(ProjectConfig.class);
	// Change this to your current project ID
	protected static final String PROJECT_NUMBER = ConfigVariables.getString("ProjectConfig.PROJECT_NUMBER"); 
	protected static final String PROJECT_ID = ConfigVariables.getString("ProjectConfig.PROJECT_ID"); 
	// Load Client ID/secret from client_secrets.json file.
	protected static final String CLIENTSECRETS_LOCATION = ConfigVariables.getString("ProjectConfig.CLIENTSECRETS_LOCATION"); 
	protected static GoogleClientSecrets clientSecrets = loadClientSecrets();
	protected static final String TOKEN_PROPERTIES = ConfigVariables.getString("ProjectConfig.TOKEN_PROPERTIES");
	public static final String REFRESH_TOKEN = ConfigVariables.getString("ProjectConfig.REFRESH_TOKEN"); 
	protected static final String REDIRECT_URI = ConfigVariables.getString("ProjectConfig.REDIRECT_URI");


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
			logger.error("Could not load clientsecrets.json"); //$NON-NLS-1$
			e.printStackTrace();
		}
		return clientSecrets;
	}

}
