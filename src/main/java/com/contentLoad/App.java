package com.contentLoad;

import java.io.IOException;

import com.sforce.async.AsyncApiException;
import com.sforce.ws.ConnectionException;

/**
 * Content upload
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, ConnectionException, AsyncApiException
    {
        //new SalesforceAPI().runSample("ContentVersion",new FileReading().readDataLineByLine("C:\\Boomi AtomSphere\\Atom - Aaroorank_Lap.cms.VECK\\VECK\\ContentVersion\\LargeFiles_FileList1.csv"));
    	PartnerSamples samples = new PartnerSamples();
    	//samples.login();
    	String sessionId = samples.getSessionId();
    	//System.out.println(sessionId);
        if (sessionId != null) {
        	samples.callRestService(new FileReading().readDataLineByRest("C:\\Boomi AtomSphere\\Atom - Aaroorank_Lap.cms.VECK\\VECK\\ContentVersion\\LargeFiles_FileList1.csv"),sessionId);
        	//samples.createRest(new FileReading().readDataLineByLineRest("C:\\Boomi AtomSphere\\Atom - Aaroorank_Lap.cms.VECK\\VECK\\ContentVersion\\LargeFiles_FileList1.csv","ContentVersion"),sessionId);
        	//samples.createSample( new FileReading().readDataLineByLine2("C:\\Boomi AtomSphere\\Atom - Aaroorank_Lap.cms.VECK\\VECK\\ContentVersion\\LargeFiles_FileList1.csv","ContentVersion"));
        }
    	//System.out.println( new FileReading().readDataLineByLine2("C:\\Boomi AtomSphere\\Atom - Aaroorank_Lap.cms.VECK\\VECK\\ContentVersion\\LargeFiles_FileList1.csv","ContentVersion"));
    	//System.out.println(new FileReading().encodeBase64("C:\\Users\\aarooran.CMS\\Desktop\\binary.txt").toString());
    }
}
