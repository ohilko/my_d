package ohilko.test4;

import java.util.ArrayList;
import java.util.HashMap;

import ohilko.test4.R;
import ohilko.test4.db.Crypto;
import ohilko.test4.db.DatabaseConnector;
import ohilko.test4.mail.MailTask;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	private ListView list;
	private static String MAIN_INFO = "main_info";
	private static String SECONDARY_INFO = "secondary_info";
	public static String ISDOWNLOAD = "download";
	private DatabaseConnector db = new DatabaseConnector(this);
	private EditText login;
	private EditText password;
	private EditText smtp;
	private EditText pop3;
	private EditText subject;
	private RadioButton rb_dropbox; 

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		list = (ListView) findViewById(R.id.listView_settings);
		ArrayList<HashMap<String, Object>> settings = new ArrayList<HashMap<String, Object>>();

		HashMap<String, Object> hm;

		hm = new HashMap<String, Object>();
		hm.put(MAIN_INFO, getResources().getString(R.string.download_mail));
		hm.put(SECONDARY_INFO, getResources().getString(R.string.download_mail_secondary));
		settings.add(hm);

		hm = new HashMap<String, Object>();
		hm.put(MAIN_INFO, getResources().getString(R.string.download_SD));
		hm.put(SECONDARY_INFO, getResources().getString(R.string.download_SD_secondary));
		settings.add(hm);

		hm = new HashMap<String, Object>();
		hm.put(MAIN_INFO, getResources().getString(R.string.download_cloud));
		hm.put(SECONDARY_INFO, getResources()
				.getString(R.string.download_cloud_secondary));
		settings.add(hm);

		SimpleAdapter adapter = new SimpleAdapter(this, settings,
				R.layout.row_settings,
				new String[] { MAIN_INFO, SECONDARY_INFO }, new int[] {
						R.id.textview_main_info, R.id.textview_secondary_info });

		list.setAdapter(adapter);
		list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

		list.setOnItemClickListener(settingsListener);
	}

	OnItemClickListener settingsListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {
			switch (position) {
			case 0: {
				showMailDialog();
			}
				break;
			case 1: {
				Intent intent = new Intent(MainActivity.this, SDActivity.class);
				intent.putExtra(ISDOWNLOAD, true);
				startActivity(intent);
			}
				break;
			case 2: {
				Intent intent = new Intent(MainActivity.this, CloudActivity.class);
				intent.putExtra(ISDOWNLOAD, true);
				startActivity(intent);
			}
				break;
			default:
				break;
			}

		}

	};

	private void showMailDialog() {
		final AlertDialog.Builder dialog_mail = new AlertDialog.Builder(
				MainActivity.this);
		dialog_mail.setTitle("Соединение с почтой");

		View linearlayout = getLayoutInflater().inflate(R.layout.dialog_auth,
				null);
		dialog_mail.setView(linearlayout);

		login = (EditText) linearlayout
				.findViewById(R.id.editText_dialog_login);
		password = (EditText) linearlayout
				.findViewById(R.id.editText_dialog_password);
		smtp = (EditText) linearlayout.findViewById(R.id.editText_smtp);
		pop3 = (EditText) linearlayout.findViewById(R.id.editText_pop3);
		subject = (EditText) linearlayout.findViewById(R.id.editText_subject);

		db.open();
		Cursor mail_cursor = db.getAllRows(DatabaseConnector.TABLE_NAME[5],
				DatabaseConnector.MAIL_DATA_FIELDS, null, null);
		if (mail_cursor.moveToFirst()) {
			login.setText(mail_cursor.getString(1));
			password.setText(Crypto.decryptionPassword(mail_cursor.getString(2)));
			smtp.setText(mail_cursor.getString(3));
			pop3.setText(mail_cursor.getString(4));
			subject.setText(mail_cursor.getString(5));

		}
		db.close();
		dialog_mail.setPositiveButton("Соединить",
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (login.getText().toString().equals("")
								|| password.getText().toString().equals("")
								|| smtp.getText().toString().equals("")
								|| pop3.getText().toString().equals("")
								|| subject.getText().toString().equals("")) {
							Toast error = Toast.makeText(MainActivity.this,
									"Необходимо заполнить все поля",
									Toast.LENGTH_LONG);
							error.show();

						} else {
							new MailTask(login.getText().toString(), password
									.getText().toString(), smtp.getText()
									.toString(), pop3.getText().toString(),
									subject.getText().toString(),
									MainActivity.this, db, false, true)
									.execute("");
						}
					}
				}).setNegativeButton("Отмена",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		dialog_mail.create();
		dialog_mail.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		SubMenu sm = menu.addSubMenu(Menu.FIRST, 1, 1, "SubMenu").setIcon(
				android.R.drawable.ic_dialog_dialer);
		menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		sm.add(Menu.FIRST, 100, 100, "О программе");

		return super.onCreateOptionsMenu(menu);
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