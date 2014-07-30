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
import android.graphics.Color;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener, OnClickListener {
	
	/*Dev/Prod vars urls*/	
	public String SERVER_URL = "http://192.168.1.14";    
	
	/*main Activity vars*/
	public OnClickListener listener1 = null;
	public OnClickListener listener2 = null;	
	public OnClickListener listener3 = null;
	
	TextView tvIsConnected;
	public static Button button2;
	public static Button button3;
	public static TextView tview;
		 
	/* voice vars */
	public ListView mList;
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
		
		// check connection network
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
		        
		        Toast.makeText(getApplicationContext(),"Calling Ami API...", Toast.LENGTH_SHORT).show();				 
		        actionsLights(true);
		      }
		    };
		    
		    listener3 = new OnClickListener() {
			      public void onClick(View v) {		        
			        
			        Toast.makeText(getApplicationContext(),"Calling Ami API...", Toast.LENGTH_SHORT).show();				 
			        actionsLights(false);
			      }
			    };				    
		
		    /* hide buttons*/
		    
		    tview = (TextView) findViewById(R.id.textView1);
		    tview.setVisibility(View.INVISIBLE);
		        
		    button2 = (Button) findViewById(R.id.button2);
		    button2.setVisibility(View.INVISIBLE);
		    button2.setOnClickListener(listener2);
		    
		    button3 = (Button) findViewById(R.id.button3);
		    button3.setVisibility(View.INVISIBLE);
		    button3.setOnClickListener(listener3);	
		  
			speakButton = (Button) findViewById(R.id.btn_speak);
			speakButton.setVisibility(View.INVISIBLE);
			speakButton.setOnClickListener(this);
			
			voiceinputbuttons();		
	}
	
	protected void onPostExecute(String result) {		
		Toast.makeText(getBaseContext(), "END API call!", Toast.LENGTH_SHORT).show();           
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
		String apiURL;
		
		if (switched)
			apiURL = SERVER_URL + ":8888/commands/light/on";
		else
			apiURL = SERVER_URL + ":8888/commands/light/off";
		
		new CallAPI().execute(apiURL);		
	}
	
	
	public void actionsUnknow(){		
		String apiURL = SERVER_URL + ":8888/commands/unknow";		
		new CallAPI().execute(apiURL);		
	}	
	
	public void actionsTemperature(){		
		String apiURL = SERVER_URL + ":8888/commands/temperature";
		new CallAPI().execute(apiURL);		
	}
	
	//voice	
		public void voiceinputbuttons() {
		    speakButton = (Button) findViewById(R.id.btn_speak);
		    mList = (ListView) findViewById(R.id.list);
		}

		public void startVoiceRecognitionActivity() {
		    Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
		    intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
		    
		    //intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ITALIAN);
		    
		    intent.putExtra(RecognizerIntent.EXTRA_PROMPT,  "Speech recognition demo");
		    startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
		}

		public void onClick(View v) {
		    // TODO Auto-generated method stub
			mList.setVisibility(View.VISIBLE);
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
		        
		        mList.setBackgroundColor(Color.parseColor("#00cc00"));
		        
		        
		        if (matches.contains("temperature")) {
		        	Toast.makeText(getBaseContext(), "HEATER SWITCHED ON!", Toast.LENGTH_SHORT).show();
		        	actionsTemperature();
		        }
		        
		        else if (matches.contains("light on")) {
		        	Toast.makeText(getBaseContext(), "ON (light) Detected!", Toast.LENGTH_SHORT).show();
		        	actionsLights(true);
		        }
		        
		        else if (matches.contains("light off")) {
		        	Toast.makeText(getBaseContext(), "OFF (light) Detected!", Toast.LENGTH_SHORT).show();
		        	actionsLights(false);
		        }
		        
		        else {
		        	Toast.makeText(getBaseContext(), "Sorry I can not understand you!", Toast.LENGTH_SHORT).show();
		        	actionsUnknow();		        	
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
						
			if (getArguments().getInt(ARG_SECTION_NUMBER) == 1){
				
				tview.setVisibility(View.VISIBLE);				
				button2.setVisibility(View.INVISIBLE);
				button3.setVisibility(View.INVISIBLE);
				speakButton.setVisibility(View.INVISIBLE);
			}
			else if (getArguments().getInt(ARG_SECTION_NUMBER) == 2){
				
				tview.setVisibility(View.INVISIBLE);				
				button2.setVisibility(View.VISIBLE);
				button3.setVisibility(View.VISIBLE);
				speakButton.setVisibility(View.INVISIBLE);				
			}
							
			else{
				tview.setVisibility(View.INVISIBLE);				
				button2.setVisibility(View.INVISIBLE);
				button3.setVisibility(View.INVISIBLE);
				speakButton.setVisibility(View.VISIBLE);
				
			}
							
			return rootView;
		}
	}		
	
	//private class
	
	public class CallAPI extends AsyncTask<String, String, String> {
		
		private String room;

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
		           room = name;
		           String age = json.get("age").toString();
		           
		 
		        } catch (Exception e ) {		        		 
		           System.out.println(e.getMessage());		 
		           return e.getMessage();		 
		        }    
		 
		        return resultToDisplay;      
		     }

		
		protected void onPostExecute(String result) {			
			Toast.makeText(getBaseContext(), "Command executed at room ["+room+"]", Toast.LENGTH_SHORT).show();
			System.out.println("############## Room is: "+room);
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	        
			mList.setVisibility(View.INVISIBLE);	        
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