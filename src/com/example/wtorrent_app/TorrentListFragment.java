package com.example.wtorrent_app;

import com.example.wtorrent_app.model.Torrent;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class TorrentListFragment extends ListFragment {
	
	

	private TorrentSelectHandler torrentSelectHandler;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setListAdapter(Wtorrent.getTorrentAdapter());
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		// do something with the data
		if(torrentSelectHandler != null) {
			torrentSelectHandler.onSelectTorrent((Torrent) Wtorrent.getTorrentAdapter().getItem(position));
		}
	}
	
	public void setTorrentSelectHandler(TorrentSelectHandler tsh){
		torrentSelectHandler = tsh;
	}

	public interface TorrentSelectHandler {
		public void onSelectTorrent(Torrent t);
	}
}
