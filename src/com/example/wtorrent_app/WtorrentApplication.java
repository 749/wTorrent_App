package com.example.wtorrent_app;

import java.net.CookieHandler;
import java.net.CookieManager;

import android.app.Application;
import android.preference.PreferenceManager;

/**
 * The central class for the wTorrent app
 * 
 * @author Jan Giesenberg
 */
public class WtorrentApplication extends Application {

	public static final int JSON_VERSION = 1;

	private CookieManager cookieManager;

	@Override
	public void onCreate() {
		super.onCreate();

		setupCookieHandler();

		// store the preference manager
		Wtorrent.setPreferenceManager(PreferenceManager
				.getDefaultSharedPreferences(this));
	}

	private void setupCookieHandler() {
		// enable VM wide cookie management
		cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);
	}

	public CookieManager getCookieManager() {
		return cookieManager;
	}
}
