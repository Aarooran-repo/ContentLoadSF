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
    	String sessionId = samples.getSessionId();
        if (sessionId != null) {
        	samples.createRest(new FileReading().readDataLineByLineRest("C:\\Boomi AtomSphere\\Atom - Aaroorank_Lap.cms.VECK\\VECK\\ContentVersion\\LargeFiles_FileList1.csv","ContentVersion"),sessionId);
        	//samples.createSample( new FileReading().readDataLineByLine2("C:\\Boomi AtomSphere\\Atom - Aaroorank_Lap.cms.VECK\\VECK\\ContentVersion\\LargeFiles_FileList1.csv","ContentVersion"));
        }
    	//System.out.println( new FileReading().readDataLineByLine2("C:\\Boomi AtomSphere\\Atom - Aaroorank_Lap.cms.VECK\\VECK\\ContentVersion\\LargeFiles_FileList1.csv","ContentVersion"));
    }
}
