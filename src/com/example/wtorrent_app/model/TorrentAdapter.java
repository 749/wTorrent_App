package com.example.wtorrent_app.model;

import java.util.EmptyStackException;
import java.util.HashSet;
import java.util.Set;

import com.example.wtorrent_app.R;
import com.example.wtorrent_app.Wtorrent;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class TorrentAdapter extends BaseAdapter {

	private Object[] keys = new String[0];
	private Context context;

	public TorrentAdapter(Context context) {
		this.context = context;
	}

	public void notifyDataSetChanged() {
		keys = Wtorrent.getTorrentHashes().toArray();
		super.notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return keys.length;
	}

	@Override
	public Object getItem(int position) {
		return Wtorrent.getTorrent((String)keys[position]);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	protected static class TorrentListItem {
		public TextView name;
		public TextView percent;
		public TextView clients;
		public TextView speed;
		public TextView size;
		public TextView eta;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Torrent t = (Torrent) getItem(position);
		View vi = convertView;

		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		TorrentListItem it;
		if (vi == null) {
			vi = mInflater.inflate(R.layout.torrent_list_item, null );
			it = new TorrentListItem();
			it.name = (TextView) vi.findViewById(R.id.textTorrentName);
			it.percent = (TextView) vi.findViewById(R.id.textTorrentPercent);
			it.clients = (TextView) vi.findViewById(R.id.textTorrentClients);
			it.speed = (TextView) vi.findViewById(R.id.textTorrentSpeed);
			it.size = (TextView) vi.findViewById(R.id.textTorrentBytes);
			it.eta = (TextView) vi.findViewById(R.id.textTorrentEta);
			vi.setTag(it);
		} else
			it = (TorrentListItem) convertView.getTag();

		it.name.setText(t.getName());
		it.percent.setText(String.format("%.1f %%", t.getProgress() * 100));
		it.clients.setText(String.format("(%d) %d / (%d) %d",
				t.getPeersTotal(), t.getPeersConnected(), t.getSeedsTotal(),
				t.getSeedsConnected()));
		it.speed.setText(String.format("DL: %.2fKB/s UP: %.2fKB/s",
				t.getSpeedDown(), t.getSpeedUp()));
		it.size.setText(String.format("%s / %s",
				Wtorrent.humanReadableByteCount(t.getDataCompleted(), true),
				Wtorrent.humanReadableByteCount(t.getDataTotal(), true)));
		it.eta.setText(String.format("ETA: %s", t.getETA()));

		return vi;
	}

}
