package es.inf.uc3m.kr.smartgit;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Api Quickstart sample code. Runs queries using client_secrets.json authentication.
 */
public class ApiQuickstart {

  // Enter your Google Developer Project number or string id.
  private static final String PROJECT_ID = "tribal-primacy-676";

  // Use a Google APIs Client standard client_secrets.json OAuth 2.0 parameters file.
  private static final String CLIENTSECRETS_LOCATION = "/client_secrets.json";

  // Objects for handling HTTP transport and JSON formatting of API calls.
  private static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  private static final JsonFactory JSON_FACTORY = new JacksonFactory();

  public static void main(String[] args) throws IOException {
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(new JacksonFactory(),
        new InputStreamReader(ApiQuickstart.class.getResourceAsStream(CLIENTSECRETS_LOCATION)));

    Credential credential = getCredentials(clientSecrets, new Scanner(System.in));
    Bigquery bigquery = new Bigquery(HTTP_TRANSPORT, JSON_FACTORY, credential);
    String query = "SELECT TOP( title, 10) as title, COUNT(*) as revision_count "
        + "FROM [publicdata:samples.wikipedia] WHERE wp_namespace = 0;";
    runQueryRpcAndPrint(bigquery, PROJECT_ID, query, System.out);
  }

  static Credential getCredentials(GoogleClientSecrets clientSecrets, Scanner scanner)
      throws IOException {
    String authorizeUrl = new GoogleAuthorizationCodeRequestUrl(
        clientSecrets, clientSecrets.getInstalled().getRedirectUris().get(0),
        Collections.singleton(BigqueryScopes.BIGQUERY)).build();
    System.out.println(
        "Paste this URL into a web browser to authorize BigQuery Access:\n" + authorizeUrl);
    System.out.println("... and paste the code you received here: ");
    String authorizationCode = scanner.nextLine();

    // Exchange the auth code for an access token.
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, Arrays.asList(BigqueryScopes.BIGQUERY))
        .build();
    GoogleTokenResponse response = flow.newTokenRequest(authorizationCode)
        .setRedirectUri(clientSecrets.getInstalled().getRedirectUris().get(0)).execute();
    return flow.createAndStoreCredential(response, null);
  }



  /**
   * Runs a synchronous BigQuery query and displays the result.
   *
   * @param bigquery An authorized BigQuery client
   * @param projectId The current project id
   * @param query A String containing a BigQuery SQL statement
   * @param out A PrintStream for output, normally System.out
   */
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

}