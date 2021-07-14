package com.contentLoad;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;

import com.opencsv.CSVReader;
import com.sforce.soap.partner.sobject.SObject;

public class FileReading {
	// Java code to illustrate reading a
	// CSV file line by line
	public  String readDataLineByLine(String file)
	{
		StringBuilder sb = new StringBuilder("Title,VersionData,PathOnClient,FirstPublishLocationId");
		try {

			// Create an object of filereader
			// class with CSV file as a parameter.
			FileReader filereader = new FileReader(file);

			// create csvReader object passing
			// file reader as a parameter
			CSVReader csvReader = new CSVReader(filereader);
			String[] nextRecord;
			
			//sb.append("\n");
			// we are going to read data line by line
			int row = 0;
			while ((nextRecord = csvReader.readNext()) != null) {
				int col = 0;
				for (String cell : nextRecord) {
					if(row != 0 && col != 0 && col != 4 && col != 5 && col != 7){
						if(col == 2){
							//sb.append(encodeBase64(cell));
							sb.append(encodeBase64(cell));
							sb.append(",");
						}else if(col == 6){
							sb.append(cell);
						}else{
							sb.append(cell);
							sb.append(",");
						}
						//System.out.print(cell + ",");
					}
					col++;
				}
				row ++;
				sb.append("\r\n");
			}
			System.out.print(sb.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	public List<SObject> readDataLineByLine2(String file,String sObjectType)
	{
		
		List<SObject> ls = new ArrayList<SObject>();
		try {

			// Create an object of filereader
			// class with CSV file as a parameter.
			FileReader filereader = new FileReader(file);
			// create csvReader object passing
			// file reader as a parameter
			CSVReader csvReader = new CSVReader(filereader);
			String[] nextRecord;
			//SObject[] contacts = new SObject[1];
			
			//sb.append("\n");
			// we are going to read data line by line
			int row = 0;
			while ((nextRecord = csvReader.readNext()) != null) {
				int col = 0;
				SObject contentVersion = new SObject();
				contentVersion.setType(sObjectType);
				for (String cell : nextRecord) {
					if(row != 0 && col != 0 && col != 4 && col != 5 && col != 7){
						if(col == 1){
							contentVersion.setField("Title",cell);
							System.out.println(col);
						}else if(col == 2){
							//byte[] myArray = cell.getBytes();
							//Blob blob = new SerialBlob(myArray );
							contentVersion.setField("VersionData",convertFileContentToBlob(cell));
						}else if(col == 3){
							contentVersion.setField("PathOnClient",cell);
						}else if(col == 6){
							contentVersion.setField("FirstPublishLocationId",cell);
						}
						//System.out.print(cell + ",");
					}
					col++;
				}
				if(row != 0){
					ls.add(contentVersion);
				}
				row ++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(contentVersion);
		//SObject[] array = new SObject[ls.size()];
		//ls.toArray(array);
		return ls;
	}
	
	public List<StringBuilder> readDataLineByLineRest(String file,String sObjectType)
	{
		
		List<StringBuilder> ls = new ArrayList<StringBuilder>();
		//String jsonInputString = "{\"Title\": \"TITLE\", \"PathOnClient\": \"PATHONCLIENT\",\"VersionData\" :\"VERSIONDATA\",\"FirstPublishLocationId\":\"ID\",\"ContentLocation\":\"S\"}";
		//String jsonInputString = "{\"name\": \"Upendra\", \"job\": \"Programmer\"}";
		try {

			// Create an object of filereader
			// class with CSV file as a parameter.
			FileReader filereader = new FileReader(file);
			// create csvReader object passing
			// file reader as a parameter
			CSVReader csvReader = new CSVReader(filereader);
			String[] nextRecord;
			//SObject[] contacts = new SObject[1];
			
			
			//sb.append("\n");
			// we are going to read data line by line
			int row = 0;
			while ((nextRecord = csvReader.readNext()) != null) {
				int col = 0;
				SObject contentVersion = new SObject();
				contentVersion.setType(sObjectType);
				String title = null;
				String versionData = null;
				String pathOnClient = null;
				String firstPublishLocationId=null;
				StringBuilder json = new StringBuilder();
				json.append("{");
				for (String cell : nextRecord) {
					if(row != 0 && col != 0 && col != 4 && col != 5 && col != 7){
						if(col == 1){
							//title = cell;
							json.append("\"Title\":\""+cell+"\",");
						}else if(col == 2){
							//byte[] myArray = cell.getBytes();
							//Blob blob = new SerialBlob(myArray );
							//versionData = encodeBase64(cell);
							json.append("\"VersionData\":\""+encodeBase64(cell)+"\",");
							//contentVersion.setField("VersionData",convertFileContentToBlob(cell));
						}else if(col == 3){
							//pathOnClient = cell;
							json.append("\"PathOnClient\":\""+cell+"\",");
							//contentVersion.setField("PathOnClient",cell);
						}else if(col == 6){
							json.append("\"FirstPublishLocationId\":\""+cell+"\"");
							//firstPublishLocationId = cell;
						}
						//System.out.print(cell + ",");
					}
					col++;
				}
				if(row != 0){
					json.append("}");
					//String json = jsonInputString.replace("TITLE", title).replace("PATHONCLIENT", pathOnClient).replace("ID", firstPublishLocationId).replace("VERSIONDATA", versionData);
					ls.add(json);
				}
				row ++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(contentVersion);
		//SObject[] array = new SObject[ls.size()];
		//ls.toArray(array);
		return ls;
	}
	
	public List<EntityContentVersion> readDataLineByRest(String file)
	{
		List<EntityContentVersion> ls = new ArrayList<EntityContentVersion>();
		try {
			FileReader filereader = new FileReader(file);
			CSVReader csvReader = new CSVReader(filereader);
			String[] nextRecord;
			int row = 0;
			while ((nextRecord = csvReader.readNext()) != null) {
				int col = 0;
				EntityContentVersion entity = new EntityContentVersion();
				for (String cell : nextRecord) {
					if(row != 0 && col != 0 && col != 4 && col != 5 && col != 7){
						if(col == 1){
							entity.setTitle(cell);
						}else if(col == 2){
						}else if(col == 3){
							entity.setPathOnClient(cell);
							//contentVersion.setField("PathOnClient",cell);
						}else if(col == 6){
						}
					}
					col++;
				}
				if(row != 0){
					ls.add(entity);
				}
				row ++;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return ls;
	}
	public String encodeBase64(String filePath) throws IOException{
		byte[] fileContent = FileUtils.readFileToByteArray(new File(filePath));
		String encodedString = Base64.getEncoder().encodeToString(fileContent);
		return encodedString;
	}
	
	public  byte[] convertFileContentToBlob(String filePath) throws IOException {
		   byte[] fileContent = null;
		   try {
			fileContent = FileUtils.readFileToByteArray(new File(filePath));
		   } catch (IOException e) {
			throw new IOException("Unable to convert file to byte array. " +
		              e.getMessage());
		   }
		   return fileContent;
	}
	
}
