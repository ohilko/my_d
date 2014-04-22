package ohilko.test4;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class ChooseProviderActivity extends Activity {

	public static final String PROVIDER_NAME = "provider_name";
	public static final String PROVIDER_ID = "provider_id";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_provider);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choose, menu);
		return true;
	}

}
