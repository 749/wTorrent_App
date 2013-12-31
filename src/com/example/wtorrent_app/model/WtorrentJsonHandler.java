package com.example.wtorrent_app.model;

import org.json.JSONException;
import org.json.JSONObject;

public interface WtorrentJsonHandler {
	public void readJSONData(JSONObject data) throws JSONException;
}
