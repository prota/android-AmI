package com.bdigital.piweek;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONObject;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener, OnClickListener {
	
	
	/*main Activity vars*/
	OnClickListener listener1 = null;
	OnClickListener listener2 = null;		
	TextView tvIsConnected;
	static Button button2;
	static TextView tview;
		 
	/* voice vars */
	public  ListView mList;
	public static Button speakButton;
	public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
		
	
	/**
	 * The serialization (saved instance state) Bundle key representing the
	 * current dropdown position.
	 */
	private static final String STATE_SELECTED_NAVIGATION_ITEM = "selected_navigation_item";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// get reference to the views		
		tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
		
		
		// check if you are connected or not
		if(isConnected()){
			tvIsConnected.setBackgroundColor(0xFF00CC00);
			tvIsConnected.setText("You are connected");
        }
		else{
			tvIsConnected.setText("You are NOT connected");
		}
		
		
		// Set up the action bar to show a dropdown list.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		// Set up the dropdown list navigation in the action bar.
		actionBar.setListNavigationCallbacks(
		// Specify a SpinnerAdapter to populate the dropdown list.
				new ArrayAdapter<String>(actionBar.getThemedContext(),
						android.R.layout.simple_list_item_1,
						android.R.id.text1, new String[] {
								getString(R.string.title_section1),
								getString(R.string.title_section2),
								getString(R.string.title_section3), }), this);
				
		
		    listener2 = new OnClickListener() {
		      public void onClick(View v) {		        
		        
		        Toast.makeText(getApplicationContext(),"Calling Ami API...", Toast.LENGTH_LONG).show();				 
		        actionsLights(true);
		      }
		    };
		
		
		    /* hide buttons*/
		    
		    tview = (TextView) findViewById(R.id.textView1);
		    tview.setVisibility(View.INVISIBLE);
		        
		    button2 = (Button) findViewById(R.id.button2);
		    button2.setVisibility(View.INVISIBLE);
		    button2.setOnClickListener(listener2);		    
		  
			speakButton = (Button) findViewById(R.id.btn_speak);
			speakButton.setVisibility(View.INVISIBLE);
			speakButton.setOnClickListener(this);
			
			voiceinputbuttons();		
	}
	
	protected void onPostExecute(String result) {
		
		Toast.makeText(getBaseContext(), "END API call!", Toast.LENGTH_LONG).show();        	
        
   }
	
	

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		// Restore the previously serialized current dropdown position.
		if (savedInstanceState.containsKey(STATE_SELECTED_NAVIGATION_ITEM)) {
			getActionBar().setSelectedNavigationItem(
					savedInstanceState.getInt(STATE_SELECTED_NAVIGATION_ITEM));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Serialize the current dropdown position.
		outState.putInt(STATE_SELECTED_NAVIGATION_ITEM, getActionBar()
				.getSelectedNavigationIndex());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		// When the given dropdown item is selected, show its contents in the
		// container view.
		Fragment fragment = new DummySectionFragment();
		Bundle args = new Bundle();
		args.putInt(DummySectionFragment.ARG_SECTION_NUMBER, position + 1);
		fragment.setArguments(args);
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.container, fragment).commit();
		
		System.out.println("replacing number");
		
		return true;
	}
	
	
	//Added AmI API
	
	 public boolean isConnected(){
	    	ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
	    	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    	    if (networkInfo != null && networkInfo.isConnected()) 
	    	    	return true;
	    	    else
	    	    	return false;	
	    }
		

	public void actionsLights(boolean switched){
		
		//String apiURL = "http://172.20.10.190:8281/test/piweek/lights";
		//http://192.168.1.28:8888/commands/light
		String apiURL;
		
		if (switched)
			apiURL = "http://172.20.10.190:8281/test/piweek/lights";
		else
			apiURL = "http://172.20.10.190:8281/test/piweek/lights";
		
		new CallAPI().execute(apiURL);		
	}
	
	public void actionsTemperature(){
		
		String apiURL = "http://172.20.10.190:8281/test/piweek/temperature";
		new CallAPI().execute(apiURL);		
	}
	
	//voice
		public void informationMenu() {
		    startActivity(new Intent("android.intent.action.INFOSCREEN"));
		}

		public void voiceinputbuttons() {
		    speakButton = (Button) findViewById(R.id.btn_speak);
		    mList = (ListView) findViewById(R.id.list);
		}

		public void startVoiceRecognitionActivity() {
		    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
		        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
		        "Speech recognition demo");
		    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		}

		public void onClick(View v) {
		    // TODO Auto-generated method stub
		    startVoiceRecognitionActivity();
		}

		@Override
		public void onActivityResult(int requestCode, int resultCode, Intent data) {
		    if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
		        // Fill the list view with the strings the recognizer thought it
		        // could have heard
		        ArrayList matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
		        mList.setAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, matches));
		        // matches is the result of voice input. It is a list of what the
		        // user possibly said.
		        // Using an if statement for the keyword you want to use allows the
		        // use of any activity if keywords match
		        // it is possible to set up multiple keywords to use the same
		        // activity so more than one word will allow the user
		        // to use the activity (makes it so the user doesn't have to
		        // memorize words from a list)
		        // to use an activity from the voice input information simply use
		        // the following format;
		        // if (matches.contains("keyword here") { startActivity(new
		        // Intent("name.of.manifest.ACTIVITY")

		     	/*	        
		        if (matches.contains("lights") || matches.contains("light")) {
		        	Toast.makeText(getBaseContext(), "Lights ON!", Toast.LENGTH_LONG).show();
		        	actionsLights();
		        } */
		        
		        if (matches.contains("temperature")) {
		        	Toast.makeText(getBaseContext(), "HEATER SWITCHED ON!", Toast.LENGTH_LONG).show();
		        	actionsTemperature();
		        }
		        
		        else if (matches.contains("lights on")) {
		        	Toast.makeText(getBaseContext(), "ON (lights) Detected!", Toast.LENGTH_LONG).show();
		        	actionsLights(true);
		        }
		        
		        else if (matches.contains("lights off")) {
		        	Toast.makeText(getBaseContext(), "OFF (lights) Detected!", Toast.LENGTH_LONG).show();
		        	actionsLights(false);
		        }
		        
		        else {
		        	Toast.makeText(getBaseContext(), "Do you mean Lights ON, don't you? ;)", Toast.LENGTH_LONG).show();
		        	actionsLights(true);		        	
		        }
		        	
		        
		        
		        super.onActivityResult(requestCode, resultCode, data);
		    }
		}
		
		
		//fi voice
	
	

	/**
	 * A dummy fragment representing a section of the app, but that simply
	 * displays dummy text.
	 */
	public static class DummySectionFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		public static final String ARG_SECTION_NUMBER = "section_number";

		public DummySectionFragment() {
			
			System.out.println("#################### Dummy Section####################");
			
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_dummy,
					container, false);
			TextView dummyTextView = (TextView) rootView
					.findViewById(R.id.section_label);
			dummyTextView.setText(Integer.toString(getArguments().getInt(
					ARG_SECTION_NUMBER)));
			
			System.out.println("#################### Actionnnnn ????? ####################");
			
			
			
			if (getArguments().getInt(ARG_SECTION_NUMBER) == 1){
				
				tview.setVisibility(View.VISIBLE);				
				button2.setVisibility(View.INVISIBLE);
				speakButton.setVisibility(View.INVISIBLE);
			}
			else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2){
				
				tview.setVisibility(View.INVISIBLE);				
				button2.setVisibility(View.VISIBLE);
				speakButton.setVisibility(View.INVISIBLE);
				
			}
							
			else{
				tview.setVisibility(View.INVISIBLE);				
				button2.setVisibility(View.INVISIBLE);
				speakButton.setVisibility(View.VISIBLE);
				
			}
				
						
			return rootView;
		}
	}
	
	
	
	//private class
	
	public class CallAPI extends AsyncTask<String, String, String> {

		@Override
		 protected String doInBackground(String... params) {
			 
		       String urlString=params[0]; // URL to call		 
		       String resultToDisplay = "";		 
		       InputStream in = null;
		       String resultfi = "";
		 
		       // HTTP Get
		       try {
		 
		        URL url = new URL(urlString);		 
		        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();		          	           
		       	urlConnection.setRequestMethod("GET");				
				urlConnection.setRequestProperty("Content-type", "application/json");				
				urlConnection.connect();
		 
		        in = new BufferedInputStream(urlConnection.getInputStream());
		           
		           try {
		        	   resultfi = convertInputStreamToString(in);
					
				} catch (Exception e) {
					// TODO: handle exception
				}
		           
		           
		           
		           
		           JSONObject json = new JSONObject(resultfi);
		           
		           String name = json.get("name").toString();
		           String age = json.get("age").toString();
		           
		 
		        } catch (Exception e ) {
		 
		           System.out.println(e.getMessage());
		 
		           return e.getMessage();
		 
		        }    
		 
		        return resultToDisplay;      
		     }

		
		protected void onPostExecute(String result) {
			
			Toast.makeText(getBaseContext(), "END API call!", Toast.LENGTH_LONG).show();        	
	        
	   }
		
	    private String convertInputStreamToString(InputStream inputStream) throws IOException{
	        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	        String line = "";
	        String result = "";
	        while((line = bufferedReader.readLine()) != null)
	            result += line;

	        inputStream.close();
	        return result;

	    }		
	}	
	//fi private class	
}