package ohilko.test4;

import java.util.ArrayList;

import ohilko.test4.R;
import ohilko.test4.adapters.MyAdapterProduct;
import ohilko.test4.adapters.MyAdapterProvider;
import ohilko.test4.db.DatabaseConnector;
import ohilko.test4.models.Product;
import ohilko.test4.models.Provider;
import android.os.Bundle;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class ChooseProviderActivity extends Activity implements
		SearchView.OnQueryTextListener, SearchView.OnCloseListener {

	public static final String PROVIDER_NAME = "provider_name";
	public static final String PROVIDER_ID = "provider_id";
	private DatabaseConnector db;
	private ArrayList<Provider> listprovider;
	private MyAdapterProvider adapter;
	private ListView myListView;

	private OnItemClickListener setProviderListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			Intent answerInent = new Intent();
			Provider provider = listprovider.get(position);
			
			answerInent.putExtra(PROVIDER_ID, provider.getId());
			answerInent.putExtra(PROVIDER_NAME, provider.getName());
			setResult(RESULT_OK, answerInent);
			finish();
		}
	};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_provider);
		
		listprovider = new ArrayList<Provider>();

		loadData(listprovider);

		myListView = (ListView) findViewById(R.id.listView_choose_provider);
		adapter = new MyAdapterProvider(this, listprovider);
		myListView.setAdapter(adapter);
		myListView.setOnItemClickListener(setProviderListener);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.choose, menu);
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

	private void loadData(ArrayList<Provider> listprovider) {
		db = new DatabaseConnector(this);
		db.open();
		Cursor providers = db.getAllRows(DatabaseConnector.TABLE_NAME[1],
				DatabaseConnector.PROVIDER_FIELDS, "name", null);

		while (providers.moveToNext()) {
			Provider provider = new Provider(providers.getString(2),
					providers.getString(3), providers.getString(4),
					providers.getLong(0));
			listprovider.add(provider);
		}

		db.close();
	}

}
