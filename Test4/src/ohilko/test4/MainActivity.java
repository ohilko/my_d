package ohilko.test4;

import ohilko.test4.db.DatabaseConnector;
import android.app.Activity;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {

	private Button downloadSD;
	private Button downloadCloud;
	private Button loadPreviousData;

	OnClickListener downloadSDClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent Request = new Intent(MainActivity.this, SDActivity.class);
			startActivity(Request);
		}
	};

	OnClickListener downloadCloudClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent Request = new Intent(MainActivity.this, CloudActivity.class);
			startActivity(Request);
		}
	};

	OnClickListener loadPreviousDataClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent Request = new Intent(MainActivity.this, ListRequestActivity.class);
			startActivity(Request);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DatabaseConnector db = new DatabaseConnector(this);
		db.open();
		
		if (db.getAllRows(DatabaseConnector.TABLE_NAME[0],
				DatabaseConnector.PRODUCT_FIELDS, null).moveToFirst()) {
		setContentView(R.layout.activity_main_with_load_button);

		downloadSD = (Button) findViewById(R.id.download_SD);
		downloadCloud = (Button) findViewById(R.id.download_cloud);

		downloadSD.setOnClickListener(downloadSDClicked);
		downloadCloud.setOnClickListener(downloadCloudClicked);
		

		loadPreviousData = (Button) findViewById(R.id.button_load_previous_data);
		loadPreviousData.setOnClickListener(loadPreviousDataClicked);
		
		db.close();
		} else {
			setContentView(R.layout.activity_main);

			downloadSD = (Button) findViewById(R.id.download_SD);
			downloadCloud = (Button) findViewById(R.id.download_cloud);

			downloadSD.setOnClickListener(downloadSDClicked);
			downloadCloud.setOnClickListener(downloadCloudClicked);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		SubMenu sm = menu.addSubMenu(Menu.FIRST, 1, 1, "SubMenu").setIcon(
				android.R.drawable.ic_dialog_dialer);
		menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		sm.add(Menu.FIRST, 100, 100, "About...");
		sm.add(Menu.FIRST, 200, 200, "Settings...");

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case 100: {
			startNewActivity(ListRequestActivity.class);
			break;
		}
		case 200: {
			startNewActivity(SettingsActivity.class);
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