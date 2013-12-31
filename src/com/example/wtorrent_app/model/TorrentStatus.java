package com.example.wtorrent_app.model;

public enum TorrentStatus { 
	TORRENT_HASHING, TORRENT_STOPPED, TORRENT_CLOSED, TORRENT_DOWNLOADING, TORRENT_SEEDING, TORRENT_MESSAGE;
	/**
	 * converts a string to a TorrentStatus enum value
	 * 
	 * @param status
	 *            the string to be converted
	 * @return the corresponding enum value
	 */
	public static TorrentStatus fromString(String status) {
		if (status.equalsIgnoreCase("chash"))
			return TORRENT_HASHING;
		if (status.equalsIgnoreCase("stopped"))
			return TORRENT_STOPPED;
		if (status.equalsIgnoreCase("downloading"))
			return TORRENT_DOWNLOADING;
		if (status.equalsIgnoreCase("seeding"))
			return TORRENT_SEEDING;
		if (status.equalsIgnoreCase("message"))
			return TORRENT_MESSAGE;
		// if(status.equalsIgnoreCase("closed")) //default to closed
		return TORRENT_CLOSED;
	}
	
	public static TorrentStatus getDefault(){
		return TORRENT_CLOSED;
	}
}
