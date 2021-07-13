package com.contentLoad;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.soap.partner.Error;

public class PartnerSamples {
	PartnerConnection partnerConnection = null;
    public boolean login() {
        boolean success = false;
        String username = "admin@veck.be.dev";
        String password = "@bsi567891Y0eF3l8qek7I1kLn0Yellv3C";
        String authEndPoint = "https://test.salesforce.com/services/Soap/u/52.0";
 
        try {
          ConnectorConfig config = new ConnectorConfig();
          config.setUsername(username);
          config.setPassword(password);
          
          config.setAuthEndpoint(authEndPoint);
          config.setTraceFile("traceLogs.txt");
          config.setTraceMessage(true);
          config.setPrettyPrintXml(true);
 
          partnerConnection = new PartnerConnection(config);     
          System.out.println(partnerConnection.getSessionHeader().getSessionId());
 
          success = true;
        } catch (ConnectionException ce) {
          ce.printStackTrace();
        } catch (FileNotFoundException fnfe) {
          fnfe.printStackTrace();
        }
 
        return success;
      }
    
    public String getSessionId() {
        boolean success = false;
        String username = "admin@veck.be.dev";
        String password = "@bsi567891Y0eF3l8qek7I1kLn0Yellv3C";
        String authEndPoint = "https://test.salesforce.com/services/Soap/u/52.0";
        String sessionId = null;
 
        try {
          ConnectorConfig config = new ConnectorConfig();
          config.setUsername(username);
          config.setPassword(password);
          
          config.setAuthEndpoint(authEndPoint);
          config.setTraceFile("traceLogs.txt");
          config.setTraceMessage(true);
          config.setPrettyPrintXml(true);
 
          partnerConnection = new PartnerConnection(config);     
          sessionId = partnerConnection.getSessionHeader().getSessionId();
          
 
          success = true;
        } catch (ConnectionException ce) {
          ce.printStackTrace();
        } catch (FileNotFoundException fnfe) {
          fnfe.printStackTrace();
        }
 
        return sessionId;
      }
    
    public String createSample(List<SObject> lSobject) {
        String result = null;
        try {
            // Create a new sObject of type Contact
           // and fill out its fields.
        
            // Add this sObject to an array 
            //SObject[] contacts = new SObject[1];
            //contacts[0] = contact;
            //System.out.println(contact);
            // Make a create call and pass it the array of sObjects
        	SaveResult[] results = null;
        	if(!lSobject.isEmpty()){
        	   for(SObject contactVersion:lSobject){
        		   SObject[] conVersions = new SObject[1];
        		   conVersions[0]= contactVersion;
        		   results = partnerConnection.create(conVersions);
        		   
        
            // Iterate through the results list
            // and write the ID of the new sObject
            // or the errors if the object creation failed.
            // In this case, we only have one result
            // since we created one contact.
		            for (int j = 0; j < results.length; j++) {
		                if (results[j].isSuccess()) {
		                    result = results[j].getId();
		                    System.out.println(
		                        "\nA content version was created with an ID of: " + result
		                    );
		                 } else {
		                    // There were errors during the create call,
		                    // go through the errors array and write
		                    // them to the console
		                    for (int i = 0; i < results[j].getErrors().length; i++) {
		                        Error err = results[j].getErrors()[i];
		                        System.out.println("Errors were found on item " + j);
		                        System.out.println("Error code: " + 
		                            err.getStatusCode().toString());
		                        System.out.println("Error message: " + err.getMessage());
		                    }
		                 }
		            }
        	   }
        	}
        } catch (ConnectionException ce) {
        	System.out.println(ce.getMessage());
            ce.printStackTrace();
        }
        return result;
    }
    
    public void createRest(List<StringBuilder> lJsonString,String sessionId) throws IOException {
    	for(StringBuilder sbJson:lJsonString){
    		String result = "";
	    	HttpPost post = new HttpPost("https://veck--dev.my.salesforce.com/services/data/v44.0/sobjects/ContentVersion");
	    	post.addHeader("Authorization","OAuth " + sessionId); 
	    	post.addHeader("Content-Type","application/json");
	    	System.out.println(sbJson.toString() );
	    	System.out.println(sessionId );
	    	// send a JSON data
	        post.setEntity(new StringEntity(sbJson.toString()));
	        try (CloseableHttpClient httpClient = HttpClients.createDefault();
	                CloseableHttpResponse response = httpClient.execute(post)) {
	             result = EntityUtils.toString(response.getEntity());
	             System.out.println(result);
	             
	        }
    	}
    }
    
}
