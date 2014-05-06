package ohilko.test4;

import ohilko.test4.R;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class CloudActivity extends Activity {

	private Spinner chooseCloud;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		chooseCloud = (Spinner) findViewById(R.id.spinner_choose_cloud);
		
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.cloud_array, android.R.layout.simple_spinner_item);		
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);		
		chooseCloud.setAdapter(adapter);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sd, menu);
		
		SubMenu sm = menu.addSubMenu (Menu.FIRST, 1, 1, "SubMenu").setIcon(android.R.drawable.ic_dialog_dialer);
		menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		sm.add(Menu.FIRST, 100, 100, "About...");
		sm.add(Menu.FIRST, 200, 200, "Settings...");
		return true;
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			break;
		}
		case 100: {
			Intent intent = new Intent(CloudActivity.this,
					AboutActivity.class);
			startActivity(intent);
			break;
		}
		case 200: {
			Intent intent = new Intent(CloudActivity.this,
					SettingsActivity.class);
			startActivity(intent);
			break;
		}
		default:
			return false;
		}
		
		return true;
	}

}
