package com.haider.ystockquote;

import java.util.Collection;
import java.util.Iterator;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity {
	public static final String TAG = "STOCKS";
			
	Button searchBT;
	EditText stockSymbolET;
	TableLayout stockSymbolTB;
	
	SharedPreferences stockSP;

	/************************************************************************
	 * the onCreate function gets called when the Activity is loaded
	 ************************************************************************/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		/* set the contents view to the activity_main.xml*/
		setContentView(R.layout.activity_main);
		
		/* get a reference/handle to the "resultsTL" and "searchET" 
		 * widget. look at the activity_main.xml for the widget definition */
		searchBT = (Button)findViewById (R.id.searchBT);
		stockSymbolET = (EditText)findViewById (R.id.stockSymbolET);
		stockSymbolTB = (TableLayout)findViewById (R.id.stockSymbolTB);
		
		/* get or create the local database for the application by the name "yhooQuote" in private mode */
		stockSP = getSharedPreferences (TAG, MODE_PRIVATE);
		
		Collection<String> data = (Collection<String>) stockSP.getAll ().values ();
		Iterator<String> iter = data.iterator ();
		
		/* get the data from the database, iterate it and display it */
		while (iter.hasNext ()) {
			String stockSymbol = iter.next ();
			
			insertStockIntoView (stockSymbol);
		}
	}
	
	/***************************************************************************
	 * insert the stock to the view
	 ***************************************************************************/	
	private void insertStockIntoView (String stockSymbol) {
		LayoutInflater stockRowLI = (LayoutInflater)getSystemService (LAYOUT_INFLATER_SERVICE);
		
		View stockRowView = stockRowLI.inflate (R.layout.stock_row, null);
		
		final TextView stockQuoteTV = (TextView) stockRowView.findViewById (R.id.stockQuoteTV);
		
		Button showBT = (Button)stockRowView.findViewById(R.id.showBT);
		
		showBT.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent (MainActivity.this, ShowStockInfo.class);
				
				String stock = stockQuoteTV.getText ().toString ();
				
				intent.putExtra (TAG, stock);
				
				startActivity (intent);
				
			}
		});
		
		stockQuoteTV.setText (stockSymbol);
		
		stockSymbolTB.addView (stockRowView);
		
	}
	
	/***************************************************************************
	 * the searchAction event handler is called when the Search button widget is
	 * clicked. look at the activity_main.xml for the event handler definition
	 ***************************************************************************/	
	public void searchAction (View view) {
		String stockSymbol = stockSymbolET.getText ().toString ();
		stockSymbolET.setText ("");
		
		InputMethodManager imm =  (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow (stockSymbolET.getWindowToken (), 0);
		
		insertStockIntoView (stockSymbol);
		
		/* insert the stock to the local database and add it to the view too*/
		SharedPreferences.Editor editor = stockSP.edit ();
		editor.putString(stockSymbol, stockSymbol);
		editor.commit ();
	}
	
	/***************************************************************************
	 * the clearAllAction event handler is called when the Clear All button is
	 * clicked. look at the activity_main.xml for the event handler definition
	 ***************************************************************************/	
	public void clearAllAction (View view) {
		stockSymbolTB.removeAllViews ();
		
		SharedPreferences.Editor editor = stockSP.edit ();
		editor.clear ();
		editor.commit ();
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
}
