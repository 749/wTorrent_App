package com.example.wtorrent_app.wtorrent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AddTorrentResult {
	private boolean success = false;
	private String[] messages = new String[0];

	public AddTorrentResult(JSONObject res) {
		if (res == null)
			return;
		try {
			this.success = res.getBoolean("success");
			JSONArray msgs = res.getJSONArray("message");
			messages = new String[msgs.length()];
			for (int i = 0; i < msgs.length(); i++) {
				messages[i] = msgs.getString(i);
			}
		} catch (JSONException e) {
			this.success = false;
			this.messages = new String[] { "Received Data corrupt." };
		}
	}

	public boolean isSuccessful() {
		return success;
	}

	public String[] getMessages() {
		return messages;
	}
}
