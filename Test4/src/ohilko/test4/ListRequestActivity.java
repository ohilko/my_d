package ohilko.test4;

import java.util.ArrayList;
import java.util.HashMap;

import ohilko.test4.R;
import ohilko.test4.adapters.MyAdapterRequest;
import ohilko.test4.db.DatabaseConnector;
import ohilko.test4.models.Request;

import android.app.*;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class ListRequestActivity extends Activity {

	public static final String ROW_ID = "row_id";
	public static final String PROVIDER_NAME = "provider_name";
	public static final String DATE = "date";
	public static final String ALL_COST = "allCost";
	private DatabaseConnector db;
	private MyAdapterRequest adapter;
	private ArrayList<Request> listRequests;
	private ListView listView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_request);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		listView = (ListView) findViewById(R.id.listView_see);

		listRequests = new ArrayList<Request>();
		loadData(listRequests);

		adapter = new MyAdapterRequest(this, listRequests);
		
		listView.setAdapter(adapter);

		listView.setOnItemClickListener(viewRequestListener);
		db.close();
	}

	OnItemClickListener viewRequestListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long idItem) {
			Intent viewContact = new Intent(ListRequestActivity.this,
					ViewRequestActivity.class);

			Request request = listRequests.get(position);
			viewContact.putExtra(ROW_ID, request.getId());
			viewContact.putExtra(PROVIDER_NAME, request.getProviderName());
			viewContact.putExtra(DATE, request.getDate());
			viewContact.putExtra(ALL_COST, request.getAllCost());
			startActivity(viewContact);
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sd, menu);

		SubMenu sm = menu.addSubMenu(Menu.FIRST, 1, 1, "SubMenu").setIcon(
				android.R.drawable.ic_dialog_dialer);
		menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		sm.add(Menu.FIRST, 300, 300, "Add").setIcon(
				android.R.drawable.ic_menu_add);
		sm.add(Menu.FIRST, 400, 400, "Unload requests");
		sm.add(Menu.FIRST, 100, 100, "About...");
		sm.add(Menu.FIRST, 200, 200, "Settings...");

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			break;
		}
		case 100: {
			startNewActivity(AboutActivity.class);
			break;
		}
		case 200: {
			startNewActivity(SettingsActivity.class);
			break;
		}
		case 300: {
			startNewActivity(AddEditRequestActivity.class);
			break;
		}
		case 400: {
			startNewActivity(UploadActivity.class);
			break;
		}
		default:
			return false;
		}

		return true;
	}

	private void startNewActivity(Class l) {
		Intent intent = new Intent(this, l);
		startActivity(intent);
	}

	private void loadData(ArrayList<Request> list) {
		db = new DatabaseConnector(this);
		db.open();
		Cursor requests = db.getAllRows(DatabaseConnector.TABLE_NAME[2],
				DatabaseConnector.REQUEST_FIELDS, "date", null);

		while (requests.moveToNext()) {
			Cursor provider = db.getRowById(DatabaseConnector.TABLE_NAME[1],
					Long.parseLong(requests.getString(1)));
			if (provider.moveToFirst()) {
				Request request = new Request(requests.getString(2),
						requests.getString(3), provider.getString(2),
						requests.getLong(0), R.drawable.ic_launcher);
				list.add(request);
			}
		}

		db.close();
	}
}