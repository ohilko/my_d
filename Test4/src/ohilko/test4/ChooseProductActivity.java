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

public class ChooseProductActivity extends Activity implements
		SearchView.OnQueryTextListener, SearchView.OnCloseListener {

	public static final String PRODUCTS_ID = "products_id";
	ListView myListView;
	private DatabaseConnector db;
	private ArrayList<Product> listproduct;
	private MyAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_product);

		listproduct = new ArrayList<Product>();

		loadData(listproduct);

		myListView = (ListView) findViewById(R.id.listView_products);
		adapter = new MyAdapter(this, listproduct);
		myListView.setAdapter(adapter);

	}

	private void loadData(ArrayList<Product> listproduct) {
		db = new DatabaseConnector(this);
		db.open();
		Cursor products = db.getRow(DatabaseConnector.TABLE_NAME[0], DatabaseConnector.PRODUCT_FIELDS,
				"isDirectory", "false");

		String[] prs = new String[products.getCount()];
		int i = 0;

		while (products.moveToNext()) {
			Product product = new Product(products.getString(3),
					products.getString(4), products.getString(5), "0");
			listproduct.add(product);

			prs[i] = products.getString(3);
			i++;
		}

		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
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
	public boolean onClose() {
		adapter.filterData("");
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		adapter.filterData(newText);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		adapter.filterData(query);
		return false;
	}
}
