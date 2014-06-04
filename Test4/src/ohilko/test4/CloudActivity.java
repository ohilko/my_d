package ohilko.test4;

import ohilko.test4.dropbox.OpenDropboxFileTask;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cloud);

		logIn = (Button) findViewById(R.id.dropbox_login);
		logIn.setOnClickListener(this);
		listFiles = (Button) findViewById(R.id.list_files);
		listFiles.setOnClickListener(this);

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
				Toast.makeText(this, "Error during Dropbox authentication",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	public void loggedIn(boolean isLogged) {
		isLoggedIn = isLogged;
		listFiles.setEnabled(isLogged);
		logIn.setText(isLogged ? "Log out" : "Log in");
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
					dropbox);
			ofd.show();
		}
			break;
		default:
			break;
		}
	}
}
