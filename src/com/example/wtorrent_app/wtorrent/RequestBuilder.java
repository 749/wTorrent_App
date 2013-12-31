package com.example.wtorrent_app.wtorrent;

import org.apache.http.client.methods.HttpRequestBase;

public abstract class RequestBuilder {
	private String baseUrl;
	public String getBaseURL(){
		return baseUrl;
	}
	public void setBaseURL(String url){
		this.baseUrl = url;
	}
	public abstract HttpRequestBase generateRequest();
}
