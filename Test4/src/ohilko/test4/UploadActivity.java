package ohilko.test4;

import ohilko.test4.R;
import ohilko.test4.db.DatabaseConnector;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class UploadActivity extends Activity {

	private Button downloadSD;
	private Button downloadCloud;
	private Button loadPreviousData;

	OnClickListener downloadSDClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent Request = new Intent(UploadActivity.this, SDActivity.class);
			startActivity(Request);
		}
	};

	OnClickListener downloadCloudClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent Request = new Intent(UploadActivity.this, CloudActivity.class);
			startActivity(Request);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setContentView(R.layout.activity_main);

		downloadSD = (Button) findViewById(R.id.download_SD);
		downloadCloud = (Button) findViewById(R.id.download_cloud);

		downloadSD.setOnClickListener(downloadSDClicked);
		downloadCloud.setOnClickListener(downloadCloudClicked);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.upload, menu);
		return true;
	}

}
