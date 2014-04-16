package ohilko.test4;

import ohilko.test4.db.DatabaseConnector;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.widget.*;

public class ViewRequestActivity extends Activity {

	private long rowID;
	private DatabaseConnector db;
	private static int COUNTCOLOMNS = 5;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_request);

		Bundle extras = getIntent().getExtras();
		rowID = extras.getLong(ListRequestActivity.ROW_ID);

		db = new DatabaseConnector(this);
		db.open();

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		TableLayout table = createTable();

		setContentView(table);

		db.close();

	}

	private TableLayout createTable() {
		TableLayout table = new TableLayout(this);

		table.setStretchAllColumns(true);
		table.setShrinkAllColumns(true);

		TableRow rowTitleRequest = new TableRow(this);
		rowTitleRequest.setGravity(Gravity.CENTER_HORIZONTAL);

		TableRow rowProvider = new TableRow(this);
		TableRow rowDate = new TableRow(this);
		TableRow rowAllCost = new TableRow(this);

		TableRow rowTitleProducts = new TableRow(this);
		rowTitleRequest.setGravity(Gravity.CENTER_HORIZONTAL);

		TableRow rowProductsLabel = new TableRow(this);

		TextView title = new TextView(this);
		title.setText("Detailed view of the request");

		title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		title.setGravity(Gravity.CENTER);
		title.setTypeface(Typeface.SERIF, Typeface.BOLD);

		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.span = COUNTCOLOMNS;

		rowTitleRequest.addView(title, params);

		addViewInRow(rowProvider, "Provider", Typeface.DEFAULT_BOLD, Gravity.CENTER);

		addViewInRow(rowDate, "Date", Typeface.DEFAULT_BOLD, Gravity.CENTER);
		
		addViewInRow(rowAllCost, "All cost", Typeface.DEFAULT_BOLD, Gravity.CENTER);
		
		addViewInRow(rowTitleProducts, "Products", Typeface.DEFAULT_BOLD, Gravity.CENTER);
		
		addViewInRow(rowProductsLabel, "№", null, Gravity.CENTER_HORIZONTAL);

		for (int i = 3; i < COUNTCOLOMNS + 1; i++) {
			addViewInRow(rowProductsLabel, DatabaseConnector.PRODUCT_FIELDS[i], null, Gravity.CENTER_HORIZONTAL);
		}
		addViewInRow(rowProductsLabel, "Amount", null, Gravity.CENTER_HORIZONTAL);

		Cursor request = db.getRow(DatabaseConnector.TABLE_NAME[2], "_id", rowID);

		if (request.moveToFirst()) {
			Cursor provider = db.getRow(DatabaseConnector.TABLE_NAME[1], "_id",
					Long.parseLong(request.getString(1)));

			if (provider.moveToFirst()) {
				addViewInRow(rowProvider, provider.getString(2), null, Gravity.CENTER_HORIZONTAL);
			}

			addViewInRow(rowDate, provider.getString(2), null, Gravity.CENTER_HORIZONTAL);

			addViewInRow(rowAllCost, provider.getString(3), null, Gravity.CENTER_HORIZONTAL);
			
			table.addView(rowTitleRequest);
			table.addView(rowProvider);
			table.addView(rowDate);
			table.addView(rowAllCost);
			table.addView(rowTitleProducts);
			table.addView(rowProductsLabel);

			Cursor request_products = db.getRow(
					DatabaseConnector.TABLE_NAME[4], "request_id",
					Long.parseLong(request.getString(1)));
			
			while (request_products.moveToNext()) {
				Cursor product = db.getRow(DatabaseConnector.TABLE_NAME[0], "_id",
						Long.parseLong(request_products.getString(2)));
				int i = 1;
				
				if(product.moveToFirst()) {
					TableRow rowProduct = new TableRow(this);
					addViewInRow(rowProduct, getString(i), null, Gravity.CENTER_HORIZONTAL);
					addViewInRow(rowProduct, product.getString(3), null, Gravity.CENTER_HORIZONTAL);
					addViewInRow(rowProduct, product.getString(4), null, Gravity.CENTER_HORIZONTAL);
					addViewInRow(rowProduct, product.getString(5), null, Gravity.CENTER_HORIZONTAL);
					addViewInRow(rowProduct, request_products.getString(3), null, Gravity.CENTER_HORIZONTAL);

					table.addView(rowProduct);
				}
				
			}
		}
		
		
		
		return table;
	}

	private void addViewInRow(TableRow rowName, String text, Typeface tf, int gravity) {
		TextView label = new TextView(this);
		label.setText(text);
		if (tf != null) {
			label.setTypeface(tf);
		}
		label.setGravity(gravity);
		
		rowName.addView(label);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sd, menu);

		SubMenu sm = menu.addSubMenu(Menu.FIRST, 1, 1, "SubMenu").setIcon(
				android.R.drawable.ic_dialog_dialer);
		menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		sm.add(Menu.FIRST, 300, 300, "Delete").setIcon(
				android.R.drawable.ic_menu_delete);
		sm.add(Menu.FIRST, 400, 400, "Edit requests").setIcon(
				android.R.drawable.ic_menu_edit);
		;
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
			Intent intent = new Intent(ViewRequestActivity.this,
					ListRequestActivity.class);
			startActivity(intent);
			break;
		}
		case 200: {
			Intent intent = new Intent(ViewRequestActivity.this,
					SettingsActivity.class);
			startActivity(intent);
			break;
		}
		case 300: {
			/** Удаление заявки */
			break;
		}
		case 400: {
			Intent intent = new Intent(ViewRequestActivity.this,
					AddEditRequestActivity.class);
			startActivity(intent);
			break;
		}
		default:
			return false;
		}

		return true;
	}

}
