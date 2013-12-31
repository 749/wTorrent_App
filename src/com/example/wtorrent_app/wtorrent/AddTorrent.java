package com.example.wtorrent_app.wtorrent;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;

import com.example.wtorrent_app.Wtorrent;

/**
 * Data structur used to send a file to be downloaded by wTorrent
 * 
 * @author BlackDeath
 * 
 */
public class AddTorrent extends RequestBuilder {
	private static final String TORRENT_FILE_FIELD = "uploadedfile";
	private static final String TORRENT_URL_FIELD = "torrenturl";
	private static final String TORRENT_PATH_FIELD = "download_dir";
	private static final String TORRENT_START_NOW_FIELD = "start_now";
	private static final String TORRENT_PRIVATE_FIELD = "private";

	private File file;
	private String url;
	private String path;
	private boolean start;
	private boolean privat;

	public AddTorrent(File file, String url, String path, boolean start,
			boolean privat) {
		this.file = file;
		this.url = url;
		this.path = path;
		this.start = start;
		this.privat = privat;
	}

	public File getFile() {
		return file;
	}

	public String getUrl() {
		return url;
	}

	public String getPath() {
		return path;
	}

	public String getStart() {
		return (start) ? "on" : "off";
	}

	public String getPrivat() {
		return (privat) ? "on" : "off";
	}

	public HttpRequestBase generateRequest() {
		HttpPost req = new HttpPost(getBaseURL() + Wtorrent.URL_ADD_TORRENT
				+ Wtorrent.URL_JSON);
		MultipartEntity entity = new MultipartEntity();

		try {
			if (getFile() != null && getFile().canRead()) {
				entity.addPart(TORRENT_FILE_FIELD, new FileBody(getFile()));
			} else if (getUrl() != null && !getUrl().isEmpty()) {
				entity.addPart(TORRENT_URL_FIELD, new StringBody(getUrl()));
			} else {
				return null;
			}
			entity.addPart(TORRENT_PATH_FIELD, new StringBody(getPath()));
			entity.addPart(TORRENT_START_NOW_FIELD, new StringBody(getStart()));
			entity.addPart(TORRENT_PRIVATE_FIELD, new StringBody(getPrivat()));
			req.setEntity(entity);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return req;
	}
}