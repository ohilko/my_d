package ohilko.test4;

import ohilko.test4.db.DatabaseConnector;
import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

public class ChooseProductActivity extends Activity implements
		OnQueryTextListener {

	public static final String PRODUCTS_ID = "products_id";
	ListView lv;
	private CursorAdapter requestAdapter;

	String temp[] = { "Gplus", "Facebook", "Instagram", "Linkdin", "Pintrest",
			"Twitter", "Snapchat", "Skype" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_product);
		DatabaseConnector db = new DatabaseConnector(this);
		db.open();
		Cursor products = db.getAllRows(DatabaseConnector.TABLE_NAME[0],
				DatabaseConnector.PRODUCT_FIELDS, "name");

		String[] prs = new String[products.getCount()];
		int i = 0;

		while (products.moveToNext()) {
			prs[i] = products.getString(3);
			i++;
		}
		
		lv = (ListView) findViewById(R.id.listView1);

		// String[] from = new String[] { "name", "unitMeasurement" };
		// int[] to = new int[] { R.id.text1, R.id.text2 };
		// requestAdapter = new SimpleCursorAdapter(ChooseProductActivity.this,
		// R.layout.request_list_item, null, from, to);
		// lv.setAdapter(requestAdapter);
		lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_1, prs));
		lv.setTextFilterEnabled(true);

		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose_product, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(
				R.id.menu_item_search).getActionView();

		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setSubmitButtonEnabled(true);
		searchView.setOnQueryTextListener(this);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		// this is your adapter that will be filtered
		if (TextUtils.isEmpty(newText)) {
			lv.clearTextFilter();
		} else {
			lv.setFilterText(newText.toString());
		}

		return true;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		// TODO Auto-generated method stub
		return false;
	}

}
