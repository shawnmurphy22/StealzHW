package com.example.stealzhomeworkshawn;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;
import com.estimote.sdk.connection.BeaconConnection;

public class MainActivity extends ActionBarActivity {

	// Variables to be used
	private LinearLayout myLinearLayout;
	private TextView valueTV;
	private TextView valueTV2;
	private ImageView valueIm;
	private ImageView valueIm2;
	private PopupWindow popUp;
	private LinearLayout layoutPopUp;
	private Button btn;
	private int count = 0;
	private boolean click = true;
	private static final String ID = "id";
	private static final String LAT = "latitude";
	private static final String LON = "longitude";
	private static final String NAME = "name";
	private static final String ADD1 = "address_line1";
	private static final String ADD2 = "address_line2";
	private static final String CITY = "city";
	private static final String STATE = "state";
	private static final String ZIP = "zip";
	private static final String PHONE = "phone";
	private static final String WEBSITE = "website";
	private static final String IMAGE = "image";
	private static final String IMAGE_ROW = "image_row";

	//beacon stuff
	private static final String ESTIMOTE_PROXIMITY_UUID = "B9407F30-F5F8-466E-AFF9-25556B57FE6D";
	private static final Region ALL_ESTIMOTE_BEACONS = new Region("regionId", ESTIMOTE_PROXIMITY_UUID, null, null);
	private BeaconManager beaconManager = new BeaconManager(this);
	private Beacon beaconTemp;
	private BeaconConnection connection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);
		try {
			// get the json feed
			JSONObject obj = new JSONObject(JSONData());
			JSONArray jArry = obj.getJSONArray("business");
			// variables used to store json feed info
			String id = "";
			String lat= "";
			String lon= "";
			String name= "";
			String add1= "";
			String add2= "";
			String city= "";
			String state= "";
			String zip= "";
			String phone= "";
			String website= "";
			String image= ""; 
			String imageRow= ""; 
			// Used to parse through the json entries
			for( int i = 0; i < jArry.length(); i++ ) {
				JSONObject objIn = jArry.getJSONObject(i);
				// obtain all of the various information
				id = objIn.getString(ID);
				lat = objIn.getString(LAT);
				lon = objIn.getString(LON);
				name = objIn.getString(NAME);
				add1 = objIn.getString(ADD1);
				add2 = objIn.getString(ADD2);
				city = objIn.getString(CITY);
				state = objIn.getString(STATE);
				zip = objIn.getString(ZIP);
				phone = objIn.getString(PHONE);
				website = objIn.getString(WEBSITE);
				image = objIn.getString(IMAGE);
				imageRow = objIn.getString(IMAGE_ROW);	
			}
			// get the layout so we cna build it dynamically
			myLinearLayout = (LinearLayout) findViewById(R.id.linearLayout1);

			//add LayoutParams
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			// set the orientation
			myLinearLayout.setOrientation(LinearLayout.VERTICAL);
			if(!add2.equals("")) {
				// append second address to first address line
				add1 += "/n" + add2;
			}
			//add textView
			valueTV = new TextView(this);
			valueTV.setTextSize(18);
			// add the various business information
			valueTV.setText(id + ". " + name + "\nCoords: " +  lat + ", " + lon + "\n" + add1 + "\n" + city + ", " + state + ", " + zip + "\n" + phone + "\n" + website );
			valueTV.setId(5);
			valueTV.setLayoutParams(params);
			//add image
			valueIm = new ImageView(this);
			new DownloadImage(valueIm).execute(image);
			valueIm.setLayoutParams(params);

			valueIm2 = new ImageView(this);
			new DownloadImage(valueIm2).execute(imageRow);
			valueIm2.setLayoutParams(params);
			//build the popUp layout
			layoutPopUp = new LinearLayout(this);
			layoutPopUp.setOrientation(LinearLayout.VERTICAL);
			// create popUp
			popUp = new PopupWindow(this);
			// create and fill the textView
			valueTV2 = new TextView(this);
			valueTV2.setTextColor(Color.WHITE);
			valueTV2.setText("TESTING");
			valueTV2.setGravity(Gravity.CENTER);
			// create a button
			btn = new Button(this);
			btn.setText("OK");
			//specify the click action
			btn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if(click) {
						popUp.dismiss();
						click = false;
					}else {
						click = true;
					}
				}
			});
			// add everything to the layout
			layoutPopUp.addView(valueTV2, params);
			layoutPopUp.setGravity(Gravity.CENTER);
			layoutPopUp.addView(btn, params);
			// add layout to popUp
			popUp.setContentView(layoutPopUp);


		} catch (JSONException e) {
			e.printStackTrace();
		}
		//add the views to the layout
		myLinearLayout.addView(valueIm);
		myLinearLayout.addView(valueTV);
		myLinearLayout.addView(valueIm2);
		// beacon manager
		beaconManager.setRangingListener(new BeaconManager.RangingListener() {
			// Discovery function
			@Override public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
				// Make sure we don't run on main UI thread
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// check for beacons
						if((beacons.size() >= 1) && (count < 1)) {
							// get the closest beacon
							beaconTemp = beacons.get(0);
							// connect to the closest beacon
							connection = new BeaconConnection(getApplicationContext(), beaconTemp, createConnectionCallback());
							connection.authenticate();
							// increment count
							count++;
							// check for when beacon is out of range    
						}else if( (beacons.size() == 0) && ( count > 0 )) {
							// Update and show pop-up window
							valueTV2.setText("Beacon out of range!");
							popUp.showAtLocation(myLinearLayout, Gravity.CENTER, 0, 0);
							popUp.update(Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL, 200, 150);
							connection.close();
							count--;

						}
					}
				});
			}
		});
	}


	@Override
	protected void onStart() {
		super.onStart();
		beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
			@Override public void onServiceReady() {
				try {
					beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
				} catch (RemoteException e) {
					Log.e("TAG", "Cannot start ranging", e);
				}
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
		} catch (RemoteException e) {
			Log.e("TAG", "Cannot stop but it does not matter now", e);
		}

		// When no longer needed. Should be invoked in #onDestroy.
		beaconManager.disconnect();
	}

	@Override
	protected void onDestroy()  {
		connection.close();
		super.onDestroy();
	}


	@TargetApi(Build.VERSION_CODES.CUPCAKE)
	private class DownloadImage extends AsyncTask<String, Void, Bitmap> {
		ImageView bmImage;

		public DownloadImage(ImageView bmImage) {
			this.bmImage = bmImage;
		}

		protected Bitmap doInBackground(String... urls) {
			String url = urls[0];
			// open up an http connection
			Bitmap mapTemp = null;
			try {
				URL urlTemp = new URL(url);
				HttpURLConnection connection = (HttpURLConnection) urlTemp.openConnection();
				connection.setDoInput(true);
				connection.connect();
				InputStream input = connection.getInputStream();
				mapTemp = BitmapFactory.decodeStream(input);
			} catch (Exception e) {
				Log.e("Error", e.getMessage());
				e.printStackTrace();
			}
			return mapTemp;
		}

		protected void onPostExecute(Bitmap result) {
			bmImage.setImageBitmap(result);
		}
	}

	// Used to read in the JSON feed
	public String JSONData() {
		String jsonString = null;
		try {
			// Open and read the JSONfeed file
			InputStream is = this.getResources().openRawResource(R.raw.jsonfeed);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			// add the feed to the string
			jsonString = new String(buffer, "UTF-8");

		} catch (IOException ex) {
			ex.printStackTrace();
			return null;
		}
		// return the json feed
		return jsonString;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	// Used when connecting to the beacon 
	private BeaconConnection.ConnectionCallback createConnectionCallback() {
		return new BeaconConnection.ConnectionCallback() {
			// Connecting to the beacon
			@Override public void onAuthenticated(final BeaconConnection.BeaconCharacteristics beaconChars) {
				// Make sure we don't run on main UI thread
				runOnUiThread(new Runnable() {
					@Override public void run() {
						// gather the information and put it together
						StringBuilder sb = new StringBuilder()
						.append("Firmware: ").append(beaconChars.getSoftwareVersion()).append("\n")
						.append("Broadcasting power: ").append(beaconChars.getBroadcastingPower()).append(" dBm\n")
						.append("Battery: ").append(beaconChars.getBatteryPercent()).append(" %\n")
						.append("Distance: ").append(String.format("%.2f",Utils.computeAccuracy(beaconTemp))).append(" m");
						// add the string to the textview
						valueTV2.setText(sb.toString());
						// position the popUp window in middle of screen
						popUp.showAtLocation(myLinearLayout, Gravity.CENTER, 0, 0);
						popUp.update(Gravity.CENTER_HORIZONTAL, Gravity.CENTER_VERTICAL, 200, 150);
					}
				});
			}

			@Override public void onAuthenticationError() {

			}

			@Override public void onDisconnected() {
				
			}
		};
	}

}
