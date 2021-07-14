package com.contentLoad;

import java.io.File;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

public class PostRequestBuilder {
	private String filetoUpload;
	private EntityContentVersion docDetails;
	private String boundary;
	private String fileType;
	/**
	 * @param filetoUpload
	 * @param docDetails
	 */
	public PostRequestBuilder(String filetoUpload, EntityContentVersion docDetails) {
		super();
		System.out.println("test : "+filetoUpload);
		this.filetoUpload = filetoUpload;
		this.docDetails = docDetails;
		this.boundary = "boundary" + System.currentTimeMillis();
		this.fileType = FileUtil.getFileMimeTypeManual(new File(this.filetoUpload));
	}
	
	/*
	 * Function		: buildRequestBody
	 * Description	: This prepares the Body for the HttpPost 
	 * @returns		: HttpEntity
	 */
	private HttpEntity buildRequestBody()
	{
		MultipartEntityBuilder builder = MultipartEntityBuilder.create();
		//Set the Boundary String 
		builder.setBoundary(boundary);
		//Set the Mode - This will put the Content-Disposition and Content-Type headers in the request Body
	    builder.setMode(HttpMultipartMode.STRICT);
	    //Add the Non-Binary Part i.e. Details of the Document being uploaded 
	    builder.addTextBody("entity_document",docDetails.prepareJSON(),ContentType.APPLICATION_JSON);
	    //Add the Binary Part i.e. File Contents 
	    File inpFile = new File(filetoUpload);
	    FileBody fb;
	    fb = new FileBody(inpFile,ContentType.create(fileType));
		builder.addPart("VersionData", fb);
		return builder.build();
		
	}
	
	/*
	 * Function		: preparePostRequest
	 * Description	: This prepares the HttpPost Request 
	 * @returns		: HttpPost
	 */
	public HttpPost preparePostRequest(String sessionId)
	{
		HttpPost hpost = new HttpPost("https://ap16.salesforce.com/services/data/v50.0/sobjects/ContentVersion");
		// Set the Headers
		// Set Authorization
		//hpost.addHeader("Authorization", "Bearer " + Configs.getAuthToken());
		hpost.addHeader("Authorization","OAuth " + sessionId); 
		// Set the Content Type
		hpost.addHeader("Content-Type","multipart/form-data;boundary=" + boundary);
		//Set the Request Body
		hpost.setEntity(buildRequestBody());
		return hpost;
		
	}
}
