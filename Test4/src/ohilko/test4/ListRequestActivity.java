package ohilko.test4;

import java.util.ArrayList;
import java.util.HashMap;

import ohilko.test4.R;
import ohilko.test4.db.DatabaseConnector;

import android.app.*;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class ListRequestActivity extends Activity {

	public static final String ROW_ID = "row_id";
	private ArrayList<HashMap<String, Object>> myRequests;
	private static final String IMGKEY = "iconfromraw";
	private DatabaseConnector db;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_request);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		ListView listView = (ListView) findViewById(R.id.listView_see);

		db = new DatabaseConnector(this);
		db.open();

		myRequests = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> hm;

		Cursor requests = db.getAllRows(DatabaseConnector.TABLE_NAME[2],
				DatabaseConnector.REQUEST_FIELDS, "date", null);

		while (requests.moveToNext()) {
			hm = new HashMap<String, Object>();
			Cursor provider = db.getRowById(DatabaseConnector.TABLE_NAME[1],
					Long.parseLong(requests.getString(1)));

			if (provider.moveToFirst()) {

				hm.put(DatabaseConnector.REQUEST_FIELDS[1],
						provider.getString(2));
			}
			hm.put(DatabaseConnector.REQUEST_FIELDS[2], requests.getString(2));
			hm.put(DatabaseConnector.REQUEST_FIELDS[3], requests.getString(3));
			hm.put(IMGKEY, R.drawable.ic_launcher);

			myRequests.add(hm);
		}

		SimpleAdapter adapter = new SimpleAdapter(this, myRequests,
				R.layout.row, new String[] {
						DatabaseConnector.REQUEST_FIELDS[1],
						DatabaseConnector.REQUEST_FIELDS[2],
						DatabaseConnector.REQUEST_FIELDS[3], IMGKEY },
				new int[] { R.id.textview_provider, R.id.textview_date,
						R.id.textview_allcost, R.id.imageView_list });

		listView.setAdapter(adapter);
		// listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		listView.setOnItemClickListener(viewRequestListener);
		db.close();
	}

	OnItemClickListener viewRequestListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			Intent viewContact = new Intent(ListRequestActivity.this,
					ViewRequestActivity.class);

			viewContact.putExtra(ROW_ID, arg3);
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

}