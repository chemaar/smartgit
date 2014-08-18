package es.inf.uc3m.kr.smartgit;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.auth.oauth2.TokenResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.Data;
import com.google.api.services.bigquery.Bigquery;
import com.google.api.services.bigquery.BigqueryScopes;
import com.google.api.services.bigquery.model.GetQueryResultsResponse;
import com.google.api.services.bigquery.model.QueryRequest;
import com.google.api.services.bigquery.model.QueryResponse;
import com.google.api.services.bigquery.model.TableCell;
import com.google.api.services.bigquery.model.TableRow;

import es.inf.uc3m.kr.smartgit.utils.ApplicationContextLocator;

public class QueryExecutor {

	protected static Logger logger = Logger.getLogger(QueryExecutor.class);

	QueryLoaderDAO queryDAO = (QueryLoaderDAO) 
			ApplicationContextLocator.getApplicationContext().getBean(QueryLoaderDAO.class.getSimpleName());

	// Objects for handling HTTP transport and JSON formatting of API calls
	private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
	private static final JsonFactory JSON_FACTORY = new JacksonFactory();

	private static GoogleAuthorizationCodeFlow flow = null;

	// BigQuery Client
	Bigquery bigquery;

	public QueryExecutor(){

	}

	protected void execute() throws IOException{
		this.bigquery = (this.bigquery==null?createBigQueryClient():this.bigquery);
		String query = queryDAO.getQueries().getProperty("testQuery");
		runQueryRpcAndPrint(bigquery, ProjectConfig.PROJECT_ID, query, System.out);
	}

	//FIXME: Extract methods
	static void runQueryRpcAndPrint(
			Bigquery bigquery, String projectId, String query, PrintStream out) throws IOException {
		QueryRequest queryRequest = new QueryRequest().setQuery(query);
		QueryResponse queryResponse = bigquery.jobs().query(projectId, queryRequest).execute();
		if (queryResponse.getJobComplete()) {
			printRows(queryResponse.getRows(), out);
			if (null == queryResponse.getPageToken()) {
				return;
			}
		}
		// This loop polls until results are present, then loops over result pages.
		String pageToken = null;
		while (true) {
			GetQueryResultsResponse queryResults = bigquery.jobs()
					.getQueryResults(projectId, queryResponse.getJobReference().getJobId())
					.setPageToken(pageToken).execute();
			if (queryResults.getJobComplete()) {
				printRows(queryResults.getRows(), out);
				pageToken = queryResults.getPageToken();
				if (null == pageToken) {
					return;
				}
			}
		}
	}


	private static void printRows(List<TableRow> rows, PrintStream out) {
		if (rows != null) {
			for (TableRow row : rows) {
				for (TableCell cell : row.getF()) {
					// Data.isNull() is the recommended way to check for the 'null object' in TableCell.
					out.printf("%s, ", Data.isNull(cell.getV()) ? "null" : cell.getV().toString());
				}
				out.println();
			}
		}
	}


	private Bigquery createBigQueryClient() throws IOException{
		// Attempt to Load existing Refresh Token
		String storedRefreshToken = loadRefreshToken();
		// Check to see if the an existing refresh token was loaded.
		// If so, create a credential and call refreshToken() to get a new
		// access token.
		Credential credential = null;
		if (storedRefreshToken != null) {
			// Request a new Access token using the refresh token.
			credential = createCredentialWithRefreshToken(
					HTTP_TRANSPORT, JSON_FACTORY, new TokenResponse().setRefreshToken(storedRefreshToken));
			credential.refreshToken();
			// If there is no refresh token (or token.properties file), start the OAuth
			// authorization flow.
		} else {
			String authorizeUrl = new GoogleAuthorizationCodeRequestUrl(
					ProjectConfig.clientSecrets,
					ProjectConfig.REDIRECT_URI,
					Collections.singleton(BigqueryScopes.BIGQUERY)).setState("").build();
			System.out.println("Paste this URL into a web browser to authorize BigQuery Access:\n" + authorizeUrl);
			System.out.println("... and type the code you received here: ");
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			String authorizationCode = in.readLine();
			// Exchange the auth code for an access token and refesh token
			credential = exchangeCode(authorizationCode);
			// Store the refresh token for future use.
			storeRefreshToken(credential.getRefreshToken());

		}

		return buildService(credential);
	}


	/**
	 *  Builds an authorized BigQuery API client.
	 */
	private static Bigquery buildService(Credential credential) {
		return new Bigquery.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential).build();
	}

	/**
	 *  Helper to load refresh token from the token.properties file.
	 */
	private static String loadRefreshToken(){
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(ProjectConfig.TOKEN_PROPERTIES));
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
		return (String) properties.get(ProjectConfig.REFRESH_TOKEN);
	}

	/**
	 * Build an authorization flow and store it as a static class attribute.
	 */
	static GoogleAuthorizationCodeFlow getFlow() {
		if (flow == null) {
			flow = new GoogleAuthorizationCodeFlow.Builder(HTTP_TRANSPORT,
					JSON_FACTORY,
					ProjectConfig.clientSecrets,
					Collections.singleton(BigqueryScopes.BIGQUERY))
			.setAccessType("offline").setApprovalPrompt("force").build();
		}
		return flow;
	}


	/**
	 * Exchange the authorization code for OAuth 2.0 credentials.
	 */
	static Credential exchangeCode(String authorizationCode) throws IOException  {
		GoogleAuthorizationCodeFlow flow = getFlow();
		GoogleTokenResponse response =
				flow.newTokenRequest(authorizationCode).setRedirectUri(ProjectConfig.REDIRECT_URI).execute();
		return flow.createAndStoreCredential(response, null);
	}


	/**
	 * No need to go through OAuth dance, get an access token using the
	 * existing refresh token.
	 */
	public static GoogleCredential createCredentialWithRefreshToken(HttpTransport transport,
			JsonFactory jsonFactory, TokenResponse tokenResponse) {
		return new GoogleCredential.Builder().setTransport(transport)
				.setJsonFactory(jsonFactory)
				.setClientSecrets(ProjectConfig.clientSecrets)
				.build()
				.setFromTokenResponse(tokenResponse);
	}



	/**
	 *  Helper to store a new refresh token in token.properties file.
	 */
	private static void storeRefreshToken(String refresh_token) {
		Properties properties = new Properties();
		properties.setProperty(ProjectConfig.REFRESH_TOKEN, refresh_token);		
		try {
			properties.store(new FileOutputStream(ProjectConfig.TOKEN_PROPERTIES), null);
		} catch (FileNotFoundException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}


}
