package com.example.wtorrent_app;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.wtorrent_app.model.Torrent;
import com.example.wtorrent_app.model.TorrentAdapter;
import com.example.wtorrent_app.wtorrent.AddTorrent;
import com.example.wtorrent_app.wtorrent.AddTorrentResult;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.util.Log;


public class Wtorrent {
	private static final String DEBUG_TAG = "wTorrent";
	private static final int JSON_VERSION = 1;
	
	
	/* SETTINGS NAMES */
	public static final String SETTINGS_URL = "server_url";
	public static final String SETTINGS_USER = "server_user";
	public static final String SETTINGS_PASSWORD = "server_passwd";
	
	
	
	/**
	 * The base url string to add new torrent files
	 */
	public static final String URL_ADD_TORRENT = "?cls=AddT";
	public static final String URL_LIST_TORRENT = "?cls=ListT&detail=android";
	public static final String URL_CONNECT_TORRENT = "?cls=ListT&json=version";
	public static final String URL_LOGIN = "?user_login=Login";
	public static final String URL_LOGOUT = "?logout=logout";
	public static final String URL_USER = "&userf=";
	public static final String URL_PASSWORD = "&passwdf=";
	
	public static final String URL_JSON = "&tpl=json";
	
	/**
	 * As long as this is true the app will not try to login again, 
	 * only when it turns to false will a relogin be required
	 */
	private static boolean loggedIn = false;
	private static SharedPreferences preferenceManager;
	private static TorrentAdapter torrentAdapter;
	private static Map<String, Torrent> torrents = new HashMap<String, Torrent>();
	private static Context context;
	
	public static boolean checkConnect(String baseURL) {
		try {
			JSONObject res = downloadUrl(baseURL + URL_CONNECT_TORRENT + URL_JSON);
			updateLoginStatus(res);
			res = res.getJSONObject("json");
			if(res.getInt("version") < JSON_VERSION) {
				return false;
			}
		} catch (JSONException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean doLogin(String baseURL, String user, String passwd) {
		try {
			JSONObject res = downloadUrl(baseURL + URL_LOGIN + URL_USER + user + URL_PASSWORD + passwd + URL_JSON);
			return updateLoginStatus(res);
		} catch (JSONException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
	
	public static boolean loadTorrents() {
		try {
			JSONObject res = downloadUrl(getBaseUrl() + URL_LIST_TORRENT + URL_JSON);
			updateLoginStatus(res);
			return updateTorrents(res);
		} catch (JSONException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
	}
	
	
	private static boolean updateTorrents(JSONObject res) {
		if(!res.has("torrents") || !res.has("count"))
			return false;
		
		try {
			JSONArray ts = res.getJSONArray("torrents");
			int count = res.getInt("count");
			
			Map<String, Torrent> current = getTorrents();
			torrents = new HashMap<String, Torrent>();
			
			for (int i = 0; i < count; i++) {
				//TODO load the torrents into a variable prolly map hash->torrent
				JSONObject td = ts.getJSONObject(i);
				String hash = td.getString("hash");
				Torrent t = null;
				if(current.containsKey(hash)) {
					t = current.get(hash);
				}
				else {
					t = new Torrent();
				}
				
				t.readJSONData(td);
				
				torrents.put(hash, t);
			}
			
		} catch (JSONException e) {
			return false;
		}
		return true;
	}
	
	public static Torrent getTorrent(String hash) {
		return torrents.get(hash);
	}
	
	public static Set<String> getTorrentHashes(){
		return torrents.keySet();
	}

	private static Map<String, Torrent> getTorrents() {
		return torrents ;
	}

	/**
	 * Sends an request to add a torrent.
	 * @param data
	 * @return An result object containing the result from the server
	 */
	public static AddTorrentResult addTorrent(AddTorrent data){
		
		
		
		return null;
	}

	public static JSONObject downloadUrl(String myurl) throws JSONException, IOException {
		InputStream is = null;
	    StringBuilder builder = new StringBuilder();
	        
	    try {
	        URL url = new URL(myurl);
	        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	        conn.setReadTimeout(10000 /* milliseconds */);
	        conn.setConnectTimeout(15000 /* milliseconds */);
	        conn.setRequestMethod("GET");
	        conn.setDoInput(true);
	        // Starts the query
	        conn.connect();
	        int response = conn.getResponseCode();
	        Log.d(DEBUG_TAG, "The response code is: " + response);
	        if (response == 200) {
	        	is = conn.getInputStream();
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	        	String line;
	        	while ((line = reader.readLine()) != null) {
	        		builder.append(line);
	        	}
	        
	        	return new JSONObject(builder.toString());
	        }
	        return null;
	    // Makes sure that the InputStream is closed after the app is
	    // finished using it.
	    } finally {
	        if (is != null) {
	            try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	        } 
	    }
	}
	
	private static boolean updateLoginStatus(JSONObject data) {
		try {
			loggedIn = data.getBoolean("login");
		} catch (JSONException e) {
			loggedIn = false;
		}
		return loggedIn;
	}


	public static boolean isLoggedIn() {
		return loggedIn;
	}
	
	public static void resetLoggedIn() {
		loggedIn = false;
		
	}

	public static String humanReadableByteCount(long bytes, boolean si) {
	    int unit = si ? 1000 : 1024;
	    if (bytes < unit) return bytes + " B";
	    int exp = (int) (Math.log(bytes) / Math.log(unit));
	    String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp-1) + (si ? "" : "i");
	    return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
	}
	
	public static URI getBaseUrl() {
		try {
			return new URI(getPreferenceManager().getString(SETTINGS_URL, ""));
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static String getUsername() {
		return getPreferenceManager().getString(SETTINGS_USER, "admin");
	}
	
	public static String getPassword() {
		return getPreferenceManager().getString(SETTINGS_PASSWORD, "");
	}
	
	public static SharedPreferences getPreferenceManager() {
		return preferenceManager;
	}
	
	public static TorrentAdapter getTorrentAdapter(){
		if(torrentAdapter == null){
			torrentAdapter = new TorrentAdapter(context);
		}
		return torrentAdapter;
	}


	public static void setPreferenceManager(
			SharedPreferences defaultSharedPreferences) {
		preferenceManager = defaultSharedPreferences;
	}

	public static void setContext(Context wtorrentApplication) {
		context = wtorrentApplication;
	}
}
