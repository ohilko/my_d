package ohilko.test4;

import ohilko.test4.dropbox.OpenDropboxFileTask;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

public class CloudActivity extends Activity implements OnClickListener {

	protected DropboxAPI<AndroidAuthSession> dropbox;

	private final static String FILE_DIR = "";
	private final static String DROPBOX_NAME = "dropbox_prefs";
	private final static String ACCESS_KEY = "1aj0og67u7olccs";
	private final static String ACCESS_SECRET = "q1zmysqohfdl3p3";
	private boolean isLoggedIn;
	private Button logIn;
	private Button listFiles;
	private boolean isDownload;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		Bundle extras = getIntent().getExtras();
		isDownload = true;
		if (extras != null) {
			isDownload = extras.getBoolean(MainActivity.ISDOWNLOAD);
		}
		logIn = (Button) findViewById(R.id.dropbox_login);
		logIn.setOnClickListener(this);
		listFiles = (Button) findViewById(R.id.list_files);
		listFiles.setOnClickListener(this);
		if (isDownload) {
			listFiles.setText("Выберите файл для загрузки");
		} else {
			listFiles.setText("Выберите папку для выгрузки");
		}

		loggedIn(false);

		AndroidAuthSession session;
		AppKeyPair pair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);

		SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
		String key = prefs.getString(ACCESS_KEY, null);
		String secret = prefs.getString(ACCESS_SECRET, null);

		if (key != null && secret != null) {
			AccessTokenPair token = new AccessTokenPair(key, secret);
			session = new AndroidAuthSession(pair, AccessType.DROPBOX, token);
		} else {
			session = new AndroidAuthSession(pair, AccessType.DROPBOX);
		}

		dropbox = new DropboxAPI<AndroidAuthSession>(session);
	}

	@Override
	protected void onResume() {
		super.onResume();

		AndroidAuthSession session = dropbox.getSession();
		if (session.authenticationSuccessful()) {
			try {
				session.finishAuthentication();

				TokenPair tokens = session.getAccessTokenPair();
				SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
				Editor editor = prefs.edit();
				editor.putString(ACCESS_KEY, tokens.key);
				editor.putString(ACCESS_SECRET, tokens.secret);
				editor.commit();

				loggedIn(true);
			} catch (IllegalStateException e) {
				Toast.makeText(this, "Ошибка при авторизации",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sd, menu);

		SubMenu sm = menu.addSubMenu(Menu.FIRST, 1, 1, "SubMenu").setIcon(
				android.R.drawable.ic_dialog_dialer);
		menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		sm.add(Menu.FIRST, 100, 100, "О программе");
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
		default:
			return false;
		}

		return true;
	}

	private void startNewActivity(Class l) {
		Intent intent = new Intent(this, l);
		startActivity(intent);
	}
	
	public void loggedIn(boolean isLogged) {
		isLoggedIn = isLogged;
		listFiles.setEnabled(isLogged);
		logIn.setText(isLogged ? "Выйти" : "Войти");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.dropbox_login:
			if (isLoggedIn) {
				dropbox.getSession().unlink();
				loggedIn(false);
			} else {
				dropbox.getSession().startAuthentication(CloudActivity.this);
			}
			break;
		case R.id.list_files: {
			OpenDropboxFileTask ofd = new OpenDropboxFileTask(this, FILE_DIR,
					dropbox, isDownload);
			ofd.show();
		}
			break;
		default:
			break;
		}
	}
}
