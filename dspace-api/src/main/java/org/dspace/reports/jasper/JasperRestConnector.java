/**
 * The contents of this file are subject to the license and copyright
 * detailed in the LICENSE and NOTICE files at the root of the source
 * tree and available online at
 *
 * http://www.dspace.org/license/
 */
package org.dspace.reports.jasper;
import java.io.InputStream;
import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

/**
 * This class deals with logic management to connect to the Jasper external service
 *
 * @author Mykhaylo Boychuk (mykhaylo.boychuk at 4science.it)
 */
public class JasperRestConnector {

    private static Logger log = LogManager.getLogger(JasperRestConnector.class);

    private String userName;

    private String password;

    private String url;

    private HttpClient httpClient;

    @PostConstruct
    private void setup() {
        CredentialsProvider provider = new BasicCredentialsProvider();
        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(userName, password);
        provider.setCredentials(AuthScope.ANY, credentials);

        this.httpClient = HttpClientBuilder.create()
            .setConnectionManager(new PoolingHttpClientConnectionManager())
            .setDefaultCredentialsProvider(provider)
            .build();
    }

    public InputStream sendPostRequest() {
        return new GetStatusCallable().sendRequestToJasper();
    }

    public InputStream sendGetRequest(String path) {
        return new GetReportsCallable(path).call();
    }

    private class GetReportsCallable {

        private String path;

        private GetReportsCallable(String path) {
            this.path = path;
        }

        public InputStream call() {
            StringBuilder stringUrl = new StringBuilder(url);
            if (StringUtils.isNotBlank(path)) {
                stringUrl.append(path);
            }
            try {
                HttpGet httpGet = new HttpGet(stringUrl.toString());
                httpGet.setHeader("Accept", "*/*");
                httpGet.setHeader("Accept-Encoding", "gzip, deflate, br");
                httpGet.setHeader("Connection", "keep-alive");
                httpGet.setHeader("Content-Type", "application/json");

                HttpResponse response = httpClient.execute(httpGet);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    log.error("Error connecting to server! The Server answered with: " + statusCode);
                    throw new RuntimeException();
                }
                return response.getEntity().getContent();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    private class GetStatusCallable {

        private InputStream sendRequestToJasper() {
            try {
                HttpPost httpPost = new HttpPost(url);
                httpPost.setHeader("Accept", "*/*");
                httpPost.setHeader("Accept-Encoding", "gzip, deflate, br");
                httpPost.setHeader("Connection", "keep-alive");
                httpPost.setHeader("Content-Type", "application/json");

                JSONObject jsonBody = new JSONObject();
                jsonBody.put("reportUnitUri", "/Reports/Collection");
                jsonBody.put("outputFormat", "pdf");
                jsonBody.put("freshData", true);

                StringEntity stringEntity = new StringEntity(jsonBody.toString());

                httpPost.getRequestLine();
                httpPost.setEntity(stringEntity);
                HttpResponse response = httpClient.execute(httpPost);
                int statusCode = response.getStatusLine().getStatusCode();
                if (statusCode != HttpStatus.SC_OK) {
                    log.error("Error connecting to server! The Server answered with: " + statusCode);
                    throw new RuntimeException();
                }
                return response.getEntity().getContent();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                throw new RuntimeException(e);
            }
        }
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

}