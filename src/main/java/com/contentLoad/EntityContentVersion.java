package com.contentLoad;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
public class EntityContentVersion {
	private String Title;
	private String PathOnClient;
	
	
	public EntityContentVersion(String title, String pathOnClient) {
		super();
		Title = title;
		PathOnClient = pathOnClient;
	}
	public EntityContentVersion(){}
	public String prepareJSON()
	{
		GsonBuilder builder = new GsonBuilder();
		Gson gs = builder.create();
		return gs.toJson(this);
	}
	
	public String getTitle() {
		return Title;
	}

	public void setTitle(String title) {
		Title = title;
	}

	public String getPathOnClient() {
		return PathOnClient;
	}

	public void setPathOnClient(String pathOnClient) {
		PathOnClient = pathOnClient;
	}

}
