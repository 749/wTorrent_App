package com.example.wtorrent_app;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.wtorrent_app.model.Torrent;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Fragment;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity implements
		TorrentListFragment.TorrentSelectHandler {

	protected SharedPreferences sharedPref;
	private ProgressDialog progDialog = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Wtorrent.setContext(this);
		setContentView(R.layout.activity_main);
		sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

		// Check that the activity is using the layout version with
		// the fragment_container FrameLayout
		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}

			Fragment tlf = new TorrentListFragment();

			tlf.setArguments(getIntent().getExtras());

			getFragmentManager().beginTransaction()
					.add(R.id.fragment_container, tlf).commit();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (!checkInternetConnection())
			return;

		updateData();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_settings:
			openSettings();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onSelectTorrent(Torrent t) {
		// TODO Auto-generated method stub

	}

	/**
	 * This function will download the current torrent data, displaying a
	 * ProgressDialog while it is active
	 */
	private void updateData() {
		if (progDialog == null) {
			progDialog = ProgressDialog.show(this, "", getString((Wtorrent
					.isLoggedIn()) ? R.string.status_loading
					: R.string.status_logon));
		}
		if (!Wtorrent.isLoggedIn()) {
			checkServerConnectivity();
			return;
		}
		progDialog.setMessage(getString(R.string.status_loading));
		// TODO we're successfully connected! Now load data from the server
		wTorrentUpdateData wud = new wTorrentUpdateData();
		wud.execute("");
	}

	private void closeProgressDialog() {
		if (progDialog != null)
			progDialog.dismiss();
		progDialog = null;
	}

	private void openSettings() {
		Intent next = new Intent(this, SettingsActivity.class);
		startActivity(next);
	}

	public boolean checkInternetConnection() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo == null || !netInfo.isConnectedOrConnecting()) {
			closeProgressDialog();
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);

			// set title
			alertDialogBuilder
					.setTitle(getString(R.string.warn_internet_title));

			// set dialog message
			alertDialogBuilder
					.setMessage(getString(R.string.warn_internet_message))
					.setCancelable(false)
					.setPositiveButton(getString(R.string.warn_exit),
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									// if this button is clicked, close
									// current activity
									MainActivity.this.finish();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
			return false;
		}
		return true;
	}

	public void checkServerConnectivity() {
		progDialog.setMessage(getString(R.string.status_connect));
		wTorrentCheckConnectivity wcc = new wTorrentCheckConnectivity();
		String url = sharedPref.getString(Wtorrent.SETTINGS_URL, "");
		if (url.isEmpty()) {
			closeProgressDialog();
			openSettings();
		} else
			wcc.execute(url);
	}

	public class wTorrentCheckConnectivity extends
			AsyncTask<String, Void, Boolean> {
		private String url;

		@Override
		protected Boolean doInBackground(String... url) {
			this.url = url[0];
			return Wtorrent.checkConnect(this.url);
		}

		protected void onPostExecute(Boolean result) {
			if (result) {
				if (Wtorrent.isLoggedIn())
					MainActivity.this.updateData();
				else
					MainActivity.this.serverDoLogon();
			} else {
				closeProgressDialog();
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MainActivity.this);

				// set title
				alertDialogBuilder
						.setTitle(getString(R.string.warn_server_title));

				// set dialog message
				alertDialogBuilder
						.setMessage(
								String.format(
										getString(R.string.warn_server_message),
										this.url))
						.setCancelable(false)
						.setPositiveButton(getString(R.string.warn_sett),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										openSettings();
									}
								})
						.setNeutralButton(getString(R.string.action_retry),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										MainActivity.this
												.updateData();
									}
								})
						.setNegativeButton(getString(R.string.warn_exit),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										MainActivity.this.finish();
									}
								});
				;

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}
		}
	}

	public class wTorrentUpdateData extends AsyncTask<String, Void, Boolean> {
		@Override
		protected Boolean doInBackground(String... url) {
			return Wtorrent.loadTorrents();
		}

		protected void onPostExecute(Boolean result) {
			Wtorrent.getTorrentAdapter().notifyDataSetChanged();
			closeProgressDialog();
		}
	}

	public void serverDoLogon() {
		if (progDialog != null)
			progDialog.setMessage(getString(R.string.status_logon));
		wTorrentDoLogon doLogin = new wTorrentDoLogon();
		doLogin.execute("");
	}

	public class wTorrentDoLogon extends AsyncTask<String, Void, Boolean> {
		private String url;

		@Override
		protected Boolean doInBackground(String... url) {
			return Wtorrent.doLogin(
					sharedPref.getString(Wtorrent.SETTINGS_URL, ""),
					sharedPref.getString(Wtorrent.SETTINGS_USER, ""),
					sharedPref.getString(Wtorrent.SETTINGS_PASSWORD, ""));
		}

		protected void onPostExecute(Boolean result) {
			if (result) {
				MainActivity.this.updateData();
			} else {
				closeProgressDialog();
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						MainActivity.this);

				// set title
				alertDialogBuilder
						.setTitle(getString(R.string.warn_logon_title));

				// set dialog message
				alertDialogBuilder
						.setMessage(
								String.format(
										getString(R.string.warn_logon_message),
										this.url))
						.setCancelable(false)
						.setPositiveButton(getString(R.string.warn_sett),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										openSettings();
									}
								})
						.setNegativeButton(getString(R.string.warn_exit),
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										// if this button is clicked, close
										// current activity
										MainActivity.this.finish();
									}
								});
				;

				// create alert dialog
				AlertDialog alertDialog = alertDialogBuilder.create();

				// show it
				alertDialog.show();
			}
		}
	}
}
