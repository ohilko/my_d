package ohilko.test4;

import java.util.ArrayList;
import java.util.HashMap;

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
import android.widget.SimpleAdapter;
import android.widget.SearchView.OnQueryTextListener;

public class ChooseProductActivity extends Activity {

	public static final String PRODUCTS_ID = "products_id";
	ListView lv;
	private ArrayList<HashMap<String, Object>> myProducts;
	private DatabaseConnector db;

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
		
		lv.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
				android.R.layout.simple_list_item_multiple_choice, prs));
		lv.setTextFilterEnabled(true);
		
		
		myProducts = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> hm;

		while (products.moveToNext()) {
			hm = new HashMap<String, Object>();

			hm.put(DatabaseConnector.PRODUCT_FIELDS[3], products.getString(3));
//			hm.put(DatabaseConnector.PRODUCT_FIELDS[4], products.getString(4));
//			hm.put(DatabaseConnector.PRODUCT_FIELDS[5], products.getString(5));
			hm.put("checkbox", false);

			myProducts.add(hm);
		}

//		SimpleAdapter adapter = new SimpleAdapter(this, myProducts,
//				R.layout.row_add_product, new String[] {
//						DatabaseConnector.PRODUCT_FIELDS[3],
////						DatabaseConnector.PRODUCT_FIELDS[4],
////						DatabaseConnector.PRODUCT_FIELDS[5], 
//						"checkbox" },
//				new int[] { R.id.textView_name, 
////				R.id.textView_um,
////						R.id.textView_price, 
//						R.id.checkBox_choice });
//
//		lv.setAdapter(adapter);
//		lv.setTextFilterEnabled(true);

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
		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			
			@Override
			public boolean onQueryTextSubmit(String query) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(query)) {
					lv.clearTextFilter();
				} else {
					lv.setFilterText(query.toString());
				}
				return true;
			}
			
			@Override
			public boolean onQueryTextChange(String newText) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		return super.onCreateOptionsMenu(menu);
	}
}
