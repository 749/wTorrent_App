package com.example.wtorrent_app.model;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

public class Torrent implements WtorrentJsonHandler {
	private static final long INFLIMIT = 1209600; // 2 week in seconds

	private String hash;
	private String name = "";
	private TorrentStatus status = TorrentStatus.getDefault();
	private String message = "";
	private boolean privat = false;
	private double ratio = 1.0;
	private double speedDown = 0.0;
	private double speedUp = 0.0;
	private long chunk_size = 1024;
	private long chunks_completed = 0;
	private long chunks_total = 1;
	
	private int peers_connected = 0;
	private int peers_total = 0;

	private int seeds_connected = 0;
	private int seeds_total = 0;

	public String getHash() {
		return hash;
	}

	public String getName() {
		return name;
	}

	public TorrentStatus getStatus() {
		return status;
	}

	public double getProgress() {
		return this.getChunksCompleted() / this.getChunksTotal();
	}

	public String getMessage() {
		return message;
	}

	public String getETA() {
		if(this.getProgress() > 0.99){
			return formatETA(0);
		}
		if(this.getSpeedDown() <= 0) {
			return formatETA(INFLIMIT);
		}
		return formatETA(((double)getChunksRemaining()) / getSpeedDown());
	}

	private String formatETA(double time) {
		if (time <= 0)
		{
			return "--";
		}

		// > 2 weeks = infinite ETA
		if (time >= INFLIMIT)
	       	{
			return "∞";
		}

		int c = 0;
		StringBuilder res = new StringBuilder();
		double temp = Math.floor(time / 86400);
		if(c < 2 && temp > 0 ) {
			res.append(temp).append(" d ");
			time -= temp * 86400;
			c++;
		}
		temp = Math.floor(time / 3600);
		if(c < 2 && temp > 0 ) {
			res.append(temp).append(" h ");
			time -= temp * 3600;
			c++;
		}
		temp = Math.floor(time / 60);
		if(c < 2 && temp > 0 ) {
			res.append(temp).append(" m ");
			time -= temp * 60;
			c++;
		}
		temp = time;
		if(c < 2 && temp > 0) {
			res.append(temp).append(" s");
			c++;
		}
		
		return res.toString();
	}

	public boolean isPrivat() {
		return privat;
	}

	public double getRatio() {
		return ratio;
	}

	public double getSpeedDown() {
		return speedDown;
	}

	public double getSpeedUp() {
		return speedUp;
	}

	/**
	 * @return The size of each chunk in bytes
	 */
	public long getChunkSize() {
		return chunk_size;
	}

	public long getChunksCompleted() {
		return chunks_completed;
	}

	public long getChunksTotal() {
		if (chunks_total > 0)
			return chunks_total;
		else
			return 1;
	}
	
	public long getChunksRemaining() {
		return getChunksTotal() - getChunksCompleted();
	}
	
	public long getDataCompleted() {
		return getChunksCompleted() * getChunkSize();
	}
	
	public long getDataTotal() {
		return getChunksTotal() * getChunkSize();
	}

	public int getPeersConnected() {
		return peers_connected;
	}

	public int getPeersTotal() {
		return peers_total;
	}

	public int getSeedsConnected() {
		return seeds_connected;
	}

	public int getSeedsTotal() {
		return seeds_total;
	}

	@Override
	public void readJSONData(JSONObject data) throws JSONException {
		// If no hash is given the JSONObject is probably not a torrent
		this.hash = data.getString("hash");
		if (data.has("name")) {
			this.name = data.getString("name");
		}
		if (data.has("status")) {
			this.status = TorrentStatus.fromString(data.getString("status"));
		}
		if (data.has("private")) {
			this.privat = data.getBoolean("private");
		}
		if (data.has("ratio")) {
			this.ratio = data.getDouble("ratio");
		}
		if (data.has("speed")) {
			JSONObject speed = data.getJSONObject("speed");
			this.speedUp = speed.getDouble("up");
			this.speedDown = speed.getDouble("down");
		}
		if (data.has("chunkinfo")) {
			JSONObject chunkinfo = data.getJSONObject("chunkinfo");
			this.chunk_size = chunkinfo.getLong("size");
			this.chunks_completed = chunkinfo.getLong("completed");
			this.chunks_total = chunkinfo.getLong("total");
		}
		if (data.has("peer")) {
			JSONObject peer = data.getJSONObject("peer");
			this.peers_connected = peer.getInt("connected");
			this.peers_total = peer.getInt("total");
		}
		if (data.has("seed")) {
			JSONObject seed = data.getJSONObject("seed");
			this.seeds_connected = seed.getInt("connected");
			this.seeds_total = seed.getInt("total");
		}
	}

}
