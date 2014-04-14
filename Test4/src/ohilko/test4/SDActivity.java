package ohilko.test4;

import java.io.File;
import java.io.IOException;

import ohilko.test4.OpenFileDialog.OnFileSelectedListener;

import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SDActivity extends Activity {

	private EditText path;
	private Button browse;
	private Button open;

	OnClickListener browseClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			openDialog();
		}
	};

	OnClickListener openClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (path.getText().toString().equals("")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SDActivity.this);

				builder.setTitle(R.string.errorTitleEmptyField);
				builder.setMessage(R.string.errorMessageEmptyField);
				builder.setPositiveButton(R.string.errorButton, null);
				builder.show();
			} else {
				File f = new File(path.getText().toString());
				int dotPosition = f.getName().lastIndexOf(".");
				String fileExtention = f.getName().substring(dotPosition);
				if (f.isFile() && fileExtention.equals(".xml")) {
					ParserXmlFile parser = new ParserXmlFile(f, SDActivity.this);
					parser.parser();
				} else {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							SDActivity.this);

					builder.setTitle(R.string.errorTitleIsNotFile);
					builder.setMessage(R.string.errorMessageIsNotFile);
					builder.setPositiveButton(R.string.errorButton, null);
					builder.show();
				}
			}
			
			startNewActivity(ListRequestActivity.class);
		}
	};

	private void openDialog() {
		String[] fileFor = { ".xml" };
		OnFileSelectedListener listener = new OnFileSelectedListener() {
			@Override
			public void onFileSelected(File f) throws IOException {
				/*
				 * File dest = new
				 * File(Environment.getExternalStorageDirectory().toString() +
				 * nameFile); path.setText(f.getPath()); if(!dest.exists()) {
				 * try { dest.createNewFile(); } catch (IOException e) {
				 * e.printStackTrace(); } }
				 * 
				 * FileChannel sourceChannel = new
				 * FileInputStream(f).getChannel(); try { FileChannel
				 * destChannel = new FileOutputStream(dest).getChannel(); try {
				 * destChannel.transferFrom(sourceChannel, 0,
				 * sourceChannel.size()); } finally { destChannel.close(); } }
				 * finally { sourceChannel.close(); }
				 */
				path.setText(f.getPath());
			}
		};
		OpenFileDialog ofd = new OpenFileDialog(this, Environment
				.getExternalStorageDirectory().toString(), fileFor, listener);
		ofd.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sd);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		path = (EditText) findViewById(R.id.editText_path);
		browse = (Button) findViewById(R.id.button_browse);
		open = (Button) findViewById(R.id.button_open_sd_file);

		browse.setOnClickListener(browseClicked);
		open.setOnClickListener(openClicked);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sd, menu);

		SubMenu sm = menu.addSubMenu(Menu.FIRST, 1, 1, "SubMenu").setIcon(
				android.R.drawable.ic_dialog_dialer);
		menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
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
