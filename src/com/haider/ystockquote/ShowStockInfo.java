package com.haider.ystockquote;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class ShowStockInfo extends ActionBarActivity {
	
	String yqlStockQuote = "https://query.yahooapis.com/v1/public/yql?q=select%20*%20from%20yahoo.finance.quote%20where%20symbol%20in%20(%22<<stock_symbol>>%22)&format=json&diagnostics=true&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys&callback=";

	TextView stockNameTV,
			 changeTV,
			 daysHighTV,
			 daysLowTV;	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_stock_info);
		
		stockNameTV = (TextView) findViewById(R.id.stockNameTV);
		changeTV = (TextView) findViewById(R.id.changeTV);
		daysHighTV = (TextView) findViewById(R.id.daysHighTV);
		daysLowTV = (TextView) findViewById(R.id.daysLowTV);	
		
		Intent intent = getIntent ();
		String stock = intent.getStringExtra (MainActivity.TAG);
		
		new MyAsyncTask ().execute (stock);
	}

	private class MyAsyncTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPostExecute (String result) {
			super.onPostExecute (result);
			
			try {
				JSONObject root = new JSONObject (result);
				JSONObject queryObject = root.getJSONObject ("query");
				JSONObject resultsObject = queryObject.getJSONObject ("results");
				JSONObject quoteObject = resultsObject.getJSONObject ("quote");
				
				String stockName = quoteObject.getString ("Name");
				String stockChange = quoteObject.getString ("Change");
				String stockDaysHigh = quoteObject.getString ("DaysHigh");
				String stockDaysLow = quoteObject.getString ("DaysLow");
				
				stockNameTV.setText (stockName);
				changeTV.setText (stockChange);
				daysHighTV.setText (stockDaysHigh);
				daysLowTV.setText (stockDaysLow);
			} 
			catch (JSONException e) {
				e.printStackTrace();
			}
		}

		@Override
		protected String doInBackground(String... params) {
			String stock = params[0];
			
			String newURL = yqlStockQuote.replace ("<<stock_symbol>>", stock);
			
			StringBuffer buffer = new StringBuffer ();
			
			HttpURLConnection con =  null;
			
			try {
				URL url = new URL (newURL);
				
				con =  (HttpURLConnection) url.openConnection ();
				
				BufferedReader bufferedReader = new BufferedReader (new InputStreamReader (con.getInputStream ()));
				String line = null;
				
				while ((line = bufferedReader.readLine ()) != null) {
					buffer.append (line + "\n");
				}
			} 
			catch (MalformedURLException e) {
				e.printStackTrace();
			} 
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			finally {
				con.disconnect();
			}
			
			return buffer.toString ();
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.show_stock_info, menu);
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
}
