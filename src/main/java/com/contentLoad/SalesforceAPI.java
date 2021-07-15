package com.contentLoad;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.sforce.async.AsyncApiException;
import com.sforce.async.BatchInfo;
import com.sforce.async.BatchStateEnum;
import com.sforce.async.BulkConnection;
import com.sforce.async.CSVReader;
import com.sforce.async.ConcurrencyMode;
import com.sforce.async.ContentType;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;
import com.sforce.async.OperationEnum;
import com.sforce.soap.partner.Connector;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class SalesforceAPI {
	ConnectorConfig partnerConfig;
	PartnerConnection partnerConnection;
	BulkConnection bulkConnection;


	
	/**
     * Creates a Bulk API job and uploads batches for a CSV file.
     */
    public void runSample(String sobjectType, String sampleFileName)
            throws AsyncApiException, ConnectionException, IOException {
        BulkConnection connection = getBulkConnection();
        JobInfo job = createJob(sobjectType, connection);
        List<BatchInfo> batchInfoList = createBatchesFromCSVFile(connection, job,sobjectType,sampleFileName);
        closeJob(connection, job.getId());
        awaitCompletion(connection, job, batchInfoList);
        checkResults(connection, job, batchInfoList);
    }
    /**
     * Gets the results of the operation and checks for errors.
     */
    private void checkResults(BulkConnection connection, JobInfo job,
              List<BatchInfo> batchInfoList)
            throws AsyncApiException, IOException {
        // batchInfoList was populated when batches were created and submitted
        for (BatchInfo b : batchInfoList) {
            CSVReader rdr =
              new CSVReader(connection.getBatchResultStream(job.getId(), b.getId()));
            List<String> resultHeader = rdr.nextRecord();
            int resultCols = resultHeader.size();
 
            List<String> row;
            while ((row = rdr.nextRecord()) != null) {
                Map<String, String> resultInfo = new HashMap<String, String>();
                for (int i = 0; i < resultCols; i++) {
                    resultInfo.put(resultHeader.get(i), row.get(i));
                }
                boolean success = Boolean.valueOf(resultInfo.get("Success"));
                boolean created = Boolean.valueOf(resultInfo.get("Created"));
                String id = resultInfo.get("Id");
                String error = resultInfo.get("Error");
                if (success && created) {
                    System.out.println("Created row with id " + id);
                } else if (!success) {
                    System.out.println("Failed with error: " + error);
                }
            }
        }
    }
    

    private void closeJob(BulkConnection connection, String jobId)
          throws AsyncApiException {
        JobInfo job = new JobInfo();
        job.setId(jobId);
        job.setState(JobStateEnum.Closed);
        connection.updateJob(job);
    }
    
    
    /**
     * Wait for a job to complete by polling the Bulk API.
     * 
     * @param connection
     *            BulkConnection used to check results.
     * @param job
     *            The job awaiting completion.
     * @param batchInfoList
     *            List of batches for this job.
     * @throws AsyncApiException
     */
    private void awaitCompletion(BulkConnection connection, JobInfo job,
          List<BatchInfo> batchInfoList)
            throws AsyncApiException {
        long sleepTime = 0L;
        Set<String> incomplete = new HashSet<String>();
        for (BatchInfo bi : batchInfoList) {
            incomplete.add(bi.getId());
        }
        while (!incomplete.isEmpty()) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {}
            System.out.println("Awaiting results..." + incomplete.size());
            sleepTime = 10000L;
            BatchInfo[] statusList =
              connection.getBatchInfoList(job.getId()).getBatchInfo();
            for (BatchInfo b : statusList) {
                if (b.getState() == BatchStateEnum.Completed
                  || b.getState() == BatchStateEnum.Failed) {
                    if (incomplete.remove(b.getId())) {
                        System.out.println("BATCH STATUS:\n" + b);
                    }
                }
            }
        }
    }
    /**
     * Create the BulkConnection used to call Bulk API operations.
     */
    private BulkConnection getBulkConnection()
          throws ConnectionException, AsyncApiException {
        ConnectorConfig partnerConfig = new ConnectorConfig();
        partnerConfig.setUsername("");
        partnerConfig.setPassword("");
        partnerConfig.setAuthEndpoint("https://test.salesforce.com/services/Soap/u/52.0");
        // Creating the connection automatically handles login and stores
        // the session in partnerConfig
        new PartnerConnection(partnerConfig);
        // When PartnerConnection is instantiated, a login is implicitly
        // executed and, if successful,
        // a valid session is stored in the ConnectorConfig instance.
        // Use this key to initialize a BulkConnection:
        ConnectorConfig config = new ConnectorConfig();
        config.setSessionId(partnerConfig.getSessionId());
        // The endpoint for the Bulk API service is the same as for the normal
        // SOAP uri until the /Soap/ part. From here it's '/async/versionNumber'
        String soapEndpoint = partnerConfig.getServiceEndpoint();
        String apiVersion = "52.0";
        String restEndpoint = soapEndpoint.substring(0, soapEndpoint.indexOf("Soap/"))
            + "async/" + apiVersion;
        config.setRestEndpoint(restEndpoint);
        // This should only be false when doing debugging.
        config.setCompression(true);
        // Set this to true to see HTTP requests and responses on stdout
        config.setTraceMessage(false);
        BulkConnection connection = new BulkConnection(config);
        return connection;
    }
	private JobInfo createJob(String sobjectType, BulkConnection connection)
	          throws AsyncApiException {
	        JobInfo job = new JobInfo();
	        job.setObject(sobjectType);
	        job.setOperation(OperationEnum.insert);
	        job.setContentType(ContentType.CSV);
	        job = connection.createJob(job);
	        System.out.println(job);
	        return job;
	}
	public List<BatchInfo> createBatchesFromCSVFile(BulkConnection bulkConnection,JobInfo jobInfo,String sobjectType, String csvFileName) throws IOException,
			AsyncApiException, ConnectionException {
		//bulkConnection = getBulkConnection();
		//JobInfo jobInfo = createJob(sobjectType,bulkConnection);
		List<BatchInfo> batchInfos = new ArrayList<BatchInfo>();
		BufferedReader rdr = new BufferedReader(new InputStreamReader(
				new ByteArrayInputStream(csvFileName.getBytes(StandardCharsets.UTF_8))));
		// read the CSV header row
		byte[] headerBytes = (rdr.readLine() + "\n").getBytes("UTF-8");
		int headerBytesLength = headerBytes.length;
		File tmpFile = File.createTempFile("bulkAPIInsert", ".csv");

		// Split the CSV file into multiple batches
		try {
			FileOutputStream tmpOut = new FileOutputStream(tmpFile);
			int maxBytesPerBatch = 10000000; // 10 million bytes per batch
			int maxRowsPerBatch = 10000; // 10 thousand rows per batch
			int currentBytes = 0;
			int currentLines = 0;
			String nextLine;
			while ((nextLine = rdr.readLine()) != null) {
				byte[] bytes = (nextLine + "\n").getBytes("UTF-8");
				// Create a new batch when our batch size limit is reached
				if (currentBytes + bytes.length > maxBytesPerBatch
						|| currentLines > maxRowsPerBatch) {
					createBatch(tmpOut, tmpFile, batchInfos, bulkConnection,
							jobInfo);
					currentBytes = 0;
					currentLines = 0;
				}
				if (currentBytes == 0) {
					tmpOut = new FileOutputStream(tmpFile);
					tmpOut.write(headerBytes);
					currentBytes = headerBytesLength;
					currentLines = 1;
				}
				tmpOut.write(bytes);
				currentBytes += bytes.length;
				currentLines++;
			}
			// Finished processing all rows
			// Create a final batch for any remaining data
			if (currentLines > 1) {
				createBatch(tmpOut, tmpFile, batchInfos, bulkConnection, jobInfo);
			}
		} finally {
			tmpFile.delete();
		}
		return batchInfos;
	}

	private void createBatch(FileOutputStream tmpOut, File tmpFile,
			List<BatchInfo> batchInfos, BulkConnection connection,
			JobInfo jobInfo) throws IOException, AsyncApiException {
		tmpOut.flush();
		tmpOut.close();
		FileInputStream tmpInputStream = new FileInputStream(tmpFile);
		try {
			BatchInfo batchInfo = connection.createBatchFromStream(jobInfo,
					tmpInputStream);
			System.out.println(batchInfo);
			batchInfos.add(batchInfo);

		} finally {
			tmpInputStream.close();
		}
	}


	private BulkConnection createBulkConnection() throws ConnectionException,
			AsyncApiException {

		String soapEndpoint = partnerConfig.getServiceEndpoint();
		ConnectorConfig bulkConfig = new ConnectorConfig();
		int iIndex = soapEndpoint.indexOf("Soap/u/");
		String apiVersion = soapEndpoint.substring((iIndex + 7), (iIndex + 11));
		String restEndpoint = soapEndpoint.substring(0,
				soapEndpoint.indexOf("Soap/"))
				+ "async/" + apiVersion;
		bulkConfig.setRestEndpoint(restEndpoint);
		// This should only be false when doing debugging.
		bulkConfig.setCompression(true);

		bulkConfig.setSessionId(partnerConfig.getSessionId());
		// Set this to true to see HTTP requests and responses on stdout
		bulkConfig.setTraceMessage(false);
		BulkConnection connection = new BulkConnection(bulkConfig);
		return connection;
	}

}
