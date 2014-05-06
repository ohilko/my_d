package ohilko.test4;

import ohilko.test4.R;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

public class SettingsActivity extends Activity {
	
	public static String NAME = "MainActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
//		SubMenu sm = menu.addSubMenu (Menu.FIRST, 1, 1, "SubMenu").setIcon(android.R.drawable.ic_dialog_dialer);
//		menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
//		sm.add(Menu.FIRST, 200, 200, "Settings...");
		
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override 
	public boolean onOptionsItemSelected(MenuItem item) 
	{
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			break;
		}
//		case 100: {
//			Intent intent = new Intent(SettingsActivity.this,
//					AboutActivity.class);
//			startActivity(intent);
//			break;
//		}
		default:
			return false;
		}
		
		return true;
	}

}
