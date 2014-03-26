package ohilko.test4;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.Button;
import android.widget.EditText;

public class SDActivity extends Activity {
	
	private EditText path;
	private Button browse;
	private Button open;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sd);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		path = (EditText) findViewById(R.id.editText_path);
		browse = (Button) findViewById(R.id.button_browse);
		open = (Button) findViewById(R.id.button_open_sd_file);
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
			Intent intent = new Intent(SDActivity.this,
					ListRequestActivity.class);
			startActivity(intent);
			break;
		}
		case 200: {
			Intent intent = new Intent(SDActivity.this,
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
