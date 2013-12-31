package com.example.wtorrent_app;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;

public abstract class WtorrentAsyncTask extends AsyncTask<String, Void, JSONObject> {
	@Override
	protected JSONObject doInBackground(String... urls) {
		try {
            return Wtorrent.downloadUrl(urls[0]);
        } catch (IOException e) {
            return null;
        } catch (JSONException e) {
			return null;
		}
	}
	
	protected void onPostExecute(JSONObject result) {
        try {
			if(result.getJSONObject("json").getInt("version") != WtorrentApplication.JSON_VERSION){
				
			}
				
		} catch (JSONException e) {
			//failed probably wrong url
		}
    }
}
