package chris.test.nfcproject;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
	
	public static final String MIME_TEXT_PLAIN = "text/plain";
	public static final String TAG = "NfcDemo";
	
	TextView explanation;
	TextView playButton;
	TextView resetButton;
	MyView myView;
	
	private ListView mDrawerList;
	private ArrayAdapter<String> mAdapter;
	private ActionBarDrawerToggle mDrawerToggle;
	private DrawerLayout mDrawerLayout;
	
	ArrayList<String> drawer_items;
	
	private NfcAdapter mNfcAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		explanation = (TextView) findViewById(R.id.textView_explanation);
		
		playButton = (TextView) findViewById(R.id.buttonPlay);
		resetButton = (TextView) findViewById(R.id.buttonReset);
		
		myView = (MyView) findViewById(R.id.myView);
		
		mDrawerList = (ListView) findViewById(R.id.drawer_list);
		
		drawer_items = new ArrayList<String>();
		drawer_items.add("Chris Tokmakidis");
		drawer_items.add("anapdev@gmail.com");
		drawer_items.add("logout");
		
		mAdapter = new ArrayAdapter<String>(this, R.layout.list_item_drawer, R.id.nav_textview, drawer_items) {
			public View getView (int position, View convertView, ViewGroup parent) {
				
				LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				
				if (convertView == null) {
					convertView = inflater.inflate(R.layout.list_item_drawer, null);
				}
				
				
				TextView textView = (TextView) convertView.findViewById(R.id.nav_textview);
				textView.setText(drawer_items.get(position));
				
				return convertView;
			}
		};
		mDrawerList.setAdapter(mAdapter);
		
		
		getSupportActionBar( ).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar( ).setHomeButtonEnabled(true);
		
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {
			
			public void onDrawerOpened (View drawerView) {
				super.onDrawerOpened(drawerView);
			}
			
			public void onDrawerClosed (View view) {
				super.onDrawerClosed(view);
			}
		};
		
		mDrawerToggle.setDrawerIndicatorEnabled(true);
		mDrawerLayout.addDrawerListener(mDrawerToggle);
		
		
		
		
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		
		if (mNfcAdapter == null) {
			Toast.makeText(this, "This device doesn't support NFC.", Toast.LENGTH_LONG).show();
			finish();
			return;
		}
		
		if (!mNfcAdapter.isEnabled()) {
			explanation.setText("NFC is disabled.");
		} else {
			explanation.setText("NFC is enabled.");
		}
		
		handleIntent(getIntent());
	}
	
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		handleIntent(intent);
	}
	
	private void handleIntent(Intent intent) {
		
		Log.d("handleIntent", "handleIntent");
		
		String action = intent.getAction();
		if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)) {
			
			String type = intent.getType();
			if (MIME_TEXT_PLAIN.equals(type)) {
				
				Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
				new NdefReaderTask().execute(tag);
				
			} else {
				Log.d(TAG, "Wrong mime type: " + type);
			}
		} else if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
			
			// In case we would still use the Tech Discovered Intent
			Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
			String[] techList = tag.getTechList();
			String searchedTech = Ndef.class.getName();
			
			for (String tech : techList) {
				if (searchedTech.equals(tech)) {
					new NdefReaderTask().execute(tag);
					break;
				}
			}
		}
	}
	
	public static void setupForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		
		final Intent intent = new Intent(activity.getApplicationContext(), activity.getClass());
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		
		final PendingIntent pendingIntent = PendingIntent.getActivity(activity.getApplicationContext(), 0, intent, 0);
		
		IntentFilter[] filters = new IntentFilter[1];
		String[][] techList = new String[][]{};
		
		// Notice that this is the same filter as in our manifest.
		filters[0] = new IntentFilter();
		filters[0].addAction(NfcAdapter.ACTION_NDEF_DISCOVERED);
		filters[0].addCategory(Intent.CATEGORY_DEFAULT);
		try {
			filters[0].addDataType(MIME_TEXT_PLAIN);
		} catch (IntentFilter.MalformedMimeTypeException e) {
			throw new RuntimeException("Check your mime type.");
		}
		
		adapter.enableForegroundDispatch(activity, pendingIntent, filters, techList);
		
	}
	
	public static void stopForegroundDispatch(final Activity activity, NfcAdapter adapter) {
		
		adapter.disableForegroundDispatch(activity);
		
	}
	
	
	private class NdefReaderTask extends AsyncTask<Tag, Void, String> {
		
		@Override
		protected String doInBackground(Tag... params) {
			
			Log.d("ndef reader", "ndef reader");
			Tag tag = params[0];
			
			Ndef ndef = Ndef.get(tag);
			if (ndef == null) {
				// NDEF is not supported by this Tag.
				Log.d("ndef is null", "ndef is null");
				return null;
			}
			
			NdefMessage ndefMessage = ndef.getCachedNdefMessage();
			
			NdefRecord[] records = ndefMessage.getRecords();
			for (NdefRecord ndefRecord : records) {
				Log.d("doInBackground", "ndefRecord");
				if (ndefRecord.getTnf() == NdefRecord.TNF_WELL_KNOWN && Arrays.equals(ndefRecord.getType(), NdefRecord.RTD_TEXT)) {
					try {
						
						Log.d("doInBackground", "tried");
						return readText(ndefRecord);
					} catch (UnsupportedEncodingException e) {
						Log.e(TAG, "Unsupported Encoding", e);
					}
				}
			}
			
			Log.d("doInBackground", "returning null");
			return null;
		}
		
		private String readText(NdefRecord record) throws UnsupportedEncodingException {
			
			Log.d("readText", "entered");
			
			byte[] payload = record.getPayload();
			
			// Get the Text Encoding
			String textEncoding = ((payload[0] & 128) == 0) ? "UTF-8" : "UTF-16";
			
			// Get the Language Code
			int languageCodeLength = payload[0] & 0063;
			
			// Get the Text
			return new String(payload, languageCodeLength + 1, payload.length - languageCodeLength - 1, textEncoding);
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (result != null) {
				
				explanation.setText("Read content: " + result);
				
				if (result.equals("unlock")) {
					
					new PerformCurlRequestTask().execute();
					myView.playAnimation();
					
				}
				
			}
			
		}
	}
	
	private class PerformCurlRequestTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected String doInBackground (String[] params) {
			
			HttpURLConnection httpcon = null;
			
			Log.d("start", "start");
			try {
				httpcon = (HttpURLConnection) ((new URL("https://api.getkisi.com/locks/5124/access").openConnection( )));
			} catch (IOException e) {
				e.printStackTrace( );
			}
			httpcon.setDoOutput(true);
			httpcon.setRequestProperty("Authorization", "KISI-LINK 75388d1d1ff0dff6b7b04a7d5162cc6c");
			try {
				httpcon.setRequestMethod("POST");
			} catch (ProtocolException e) {
				e.printStackTrace( );
			}
			try {
				httpcon.connect( );
			} catch (IOException e) {
				e.printStackTrace( );
			}
			
			byte[] outputBytes = new byte[0];
			try {
				outputBytes = "{'value': 7.5}".getBytes("UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace( );
			}
			OutputStream os = null;
			try {
				os = httpcon.getOutputStream( );
			} catch (IOException e) {
				e.printStackTrace( );
			}
			try {
				os.write(outputBytes);
			} catch (IOException e) {
				e.printStackTrace( );
			}
			
			try {
				os.close( );
			} catch (IOException e) {
				e.printStackTrace( );
			}
			
			Log.d("finish", "finish");
			return "some message";
		}
		
	}
	
	@Override
	protected void onResume() {
		
		super.onResume();
		setupForegroundDispatch(this, mNfcAdapter);
		
	}
	
	@Override
	protected void onPause() {
		
		stopForegroundDispatch(this, mNfcAdapter);
		super.onPause();
		
	}
		
	public void playAnim (View v) {
		
		myView.playAnimation();
		
	}
	
	public void resetAnim (View v) {
		
		myView.resetAnimation();
		
	}
	
	
	@Override
	public boolean onOptionsItemSelected (MenuItem item) {
		
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		
		return super.onOptionsItemSelected(item);
		
	}
	
	
	@Override
	public void onBackPressed () {
		
		if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
			this.mDrawerLayout.closeDrawer(GravityCompat.START);
		}
		
	}
	
	
	@Override
	protected void onPostCreate (Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState( );
	}
	
	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
		
	}
	
	
}
