package jenkinsapi2;

import java.util.Scanner;

import org.apache.http.auth.*;
import org.apache.http.client.*;
import org.apache.http.client.methods.*;
import org.apache.http.impl.client.*;

import com.google.gson.Gson;

public class JenkinsMonitor {

    public static void main(String[] args) throws Exception {

        String protocol = "http";
        String host = "192.168.1.3";
        int port = 8080;
        String usernName = "shubham";
        String password = ")yz$trazG5";

        DefaultHttpClient httpclient = new DefaultHttpClient();
        httpclient.getCredentialsProvider().setCredentials(
                new AuthScope(host, port), 
                new UsernamePasswordCredentials(usernName, password));

        String jenkinsUrl = protocol + "://" + host + ":" + port;

        try {
            // get the crumb from Jenkins
            // do this only once per HTTP session
            // keep the crumb for every coming request
        	 System.out.println("Connecting to...."+jenkinsUrl);
        	 Thread.sleep(2000);
        	 System.out.println("Connecting to...."+jenkinsUrl);
        	 Thread.sleep(1000);
        	 System.out.println("Connecction established");
        	 System.out.println();
            System.out.println("... issue crumb");
            HttpGet httpGet = new HttpGet(jenkinsUrl + "/crumbIssuer/api/json");
            String crumbResponse= toString(httpclient, httpGet);
            CrumbJson crumbJson = new Gson()
                .fromJson(crumbResponse, CrumbJson.class);

            // add the issued crumb to each request header
            // the header field name is also contained in the json response
            System.out.println("... issue rss of latest builds");
            HttpPost httpost = new HttpPost(jenkinsUrl + "/rssLatest");
            httpost.addHeader(crumbJson.crumbRequestField, crumbJson.crumb);
            toString(httpclient, httpost);
            
            System.out.println();
           
            try {
            	 Scanner input = new Scanner(System.in);
            	 System.out.println("Enter the Job to build in Jenkins..");
            	 String job=input.next();
            	 String buildurl= String.format("/job/%s/build", job);
            	 
            	 
            HttpPost httpost2 = new HttpPost(jenkinsUrl + buildurl);
            httpost2.addHeader(crumbJson.crumbRequestField, crumbJson.crumb);
            toString(httpclient, httpost2);
            System.out.println("....starting to build");
            }
            catch(Exception e)
            {
            	System.out.print("Sorry something went Wrong: PLease check your job Name");
            }
            
        } finally {
            httpclient.getConnectionManager().shutdown();
        }

    }

    // helper construct to deserialize crumb json into 
    public static class CrumbJson {
        public String crumb;
        public String crumbRequestField;
    }

    private static String toString(DefaultHttpClient client, 
        HttpRequestBase request) throws Exception {
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = client.execute(request, responseHandler);
        System.out.println(responseBody + "\n");
        return responseBody;
    }

}