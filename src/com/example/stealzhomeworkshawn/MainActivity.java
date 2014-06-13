package com.example.stealzhomeworkshawn;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	
	// Variables to be used
	private LinearLayout myLinearLayout;
	private TextView valueTV;
	private ImageView valueIm;
	private ImageView valueIm2;
	private Bitmap bitmap;
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
				JSONObject obj_in = jArry.getJSONObject(i);
				// obtain all of the various information
				id = obj_in.getString(ID);
				lat = obj_in.getString(LAT);
				lon = obj_in.getString(LON);
				name = obj_in.getString(NAME);
				add1 = obj_in.getString(ADD1);
				add2 = obj_in.getString(ADD2);
				city = obj_in.getString(CITY);
				state = obj_in.getString(STATE);
				zip = obj_in.getString(ZIP);
				phone = obj_in.getString(PHONE);
				website = obj_in.getString(WEBSITE);
				image = obj_in.getString(IMAGE);
				imageRow = obj_in.getString(IMAGE_ROW);	
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
			valueTV.setText(name + "\nCoords: " +  lat + ", " + lon + "\n" + add1 + "\n" + city + ", " + state + ", " + zip + "\n" + phone + "\n" + website );
			valueTV.setId(5);
			valueTV.setLayoutParams(params);
			
			//Log.d("URL", image);
			// get the bitmap
			bitmap = getBitmapFromURL(image);
			//add image
			valueIm = new ImageView(this);
			valueIm.setImageBitmap(bitmap);
			valueIm.setId(5);
			valueIm.setLayoutParams(params);
			
			// get the bitmap
			bitmap = getBitmapFromURL(imageRow);
			// add image
			valueIm2 = new ImageView(this);
			valueIm2.setImageBitmap(bitmap);
			valueIm2.setId(5);
			valueIm2.setLayoutParams(params);
			 
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//add the views to the layout
		myLinearLayout.addView(valueIm);
		myLinearLayout.addView(valueTV);
		myLinearLayout.addView(valueIm2);
	}
	
	// Used to get the bitmap of a image from a URL
	public static Bitmap getBitmapFromURL(String src) {
	    try {
	    	// open up an http connection
	        URL url = new URL(src);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setDoInput(true);
	        connection.connect();
	        InputStream input = connection.getInputStream();
	        // decode the image to be a bitmap
	        Bitmap myBitmap = BitmapFactory.decodeStream(input);
	        return myBitmap;
	    } catch (IOException e) {
	        e.printStackTrace();
	        Log.e("Exception",e.getMessage());
	        return null;
	    }
	}
	
	// Used to read in the JSON feed
	public String JSONData() {
		String jsonString = null;
		try {
			// Open and read the jsonfeed file
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
