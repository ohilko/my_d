package ohilko.test4;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

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
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class AddEditRequestActivity extends Activity {

	private long rowID;
	private long providerID;
	private DatabaseConnector db;
	private static int COUNTCOLOMNS = 5;
	private Cursor request;
	private TableLayout table_request_products;
	private String allCost;
	private long[] productId;
	
	OnClickListener providerClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent Request = new Intent(AddEditRequestActivity.this, ChooseProviderActivity.class);
			startActivity(Request);
		}
	};
	
	OnClickListener addProductClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent Request = new Intent(AddEditRequestActivity.this, ChooseProductActivity.class);
			startActivity(Request);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit_request);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		TextView provider_name = (TextView) findViewById(R.id.editText_provider);
		TextView date = (TextView) findViewById(R.id.editText_date);
		TextView allCost_all = (TextView) findViewById(R.id.textView_allCost);
		Button buttonAddProduct = (Button) findViewById(R.id.button_add_product);
		
		buttonAddProduct.setOnClickListener(addProductClicked);
		
		Date date1 =  new Date();
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("dd.MM.yyyy");
		String date_time = formatter1.format(date1);
		
		date.setText(date_time);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			rowID = extras.getLong(ListRequestActivity.ROW_ID);
			provider_name.setText(extras.getString(ChooseProviderActivity.PROVIDER_NAME));
			providerID = extras.getLong(ChooseProviderActivity.PROVIDER_ID);
			allCost = extras.getString("");
//			allCost_all.setText(allCost);
			productId = extras.getLongArray(ChooseProductActivity.PRODUCTS_ID);
		} else {
			rowID = 0;
		}

		table_request_products = (TableLayout) findViewById(R.id.table_request_products_label);
		
		provider_name.setOnClickListener(providerClicked);

		db = new DatabaseConnector(this);
		db.open();
		
		db.close();
	}

	private void addRequest() {
		
	}

	private TableLayout createTable() {
		TableLayout table = new TableLayout(this);
		TableLayout tableProducts = new TableLayout(this);

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
		title.setText("Create to request");

		title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
		title.setGravity(Gravity.CENTER);
		title.setTypeface(Typeface.SERIF, Typeface.BOLD);

		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.span = 2;

		rowTitleRequest.addView(title, params);

		TableRow.LayoutParams paramsProduct = new TableRow.LayoutParams();
		params.span = 5;

		TextView titleProducts = new TextView(this);
		titleProducts.setText("Products");

		titleProducts.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		titleProducts.setGravity(Gravity.CENTER);
		titleProducts.setTypeface(Typeface.SERIF, Typeface.BOLD);

		rowTitleProducts.addView(titleProducts, paramsProduct);

		rowTitleRequest.addView(title, params);

		addViewInRow(rowProvider, "Provider", Typeface.DEFAULT_BOLD,
				Gravity.CENTER);

		addViewInRow(rowDate, "Date", Typeface.DEFAULT_BOLD, Gravity.CENTER);

		addViewInRow(rowAllCost, "All cost", Typeface.DEFAULT_BOLD,
				Gravity.CENTER);

		addViewInRow(rowProductsLabel, "¹", null, Gravity.CENTER_HORIZONTAL);

		for (int i = 3; i < COUNTCOLOMNS + 1; i++) {
			addViewInRow(rowProductsLabel, DatabaseConnector.PRODUCT_FIELDS[i],
					null, Gravity.CENTER_HORIZONTAL);
		}
		addViewInRow(rowProductsLabel, "Amount", null,
				Gravity.CENTER_HORIZONTAL);

		if (rowID != 0) {
			request = db.getRow(DatabaseConnector.TABLE_NAME[2], "_id", rowID);

			if (request.moveToFirst()) {
				Cursor provider = db.getRow(DatabaseConnector.TABLE_NAME[1],
						"_id", Long.parseLong(request.getString(1)));

				if (provider.moveToFirst()) {
					addViewInRowEdit(rowProvider, provider.getString(2), null,
							Gravity.CENTER_HORIZONTAL);
				}

				addViewInRowEdit(rowDate, request.getString(2), null,
						Gravity.CENTER_HORIZONTAL);

				addViewInRowEdit(rowAllCost, request.getString(3), null,
						Gravity.CENTER_HORIZONTAL);

				table.addView(rowTitleRequest);
				table.addView(rowProvider);
				table.addView(rowDate);
				table.addView(rowAllCost);
				tableProducts.addView(rowTitleProducts);
				tableProducts.addView(rowProductsLabel);

				Cursor request_products = db.getRow(
						DatabaseConnector.TABLE_NAME[4], "request_id",
						Long.parseLong(request.getString(1)));

				while (request_products.moveToNext()) {
					Cursor product = db.getRow(DatabaseConnector.TABLE_NAME[0],
							"_id",
							Long.parseLong(request_products.getString(2)));
					int i = 1;

					if (product.moveToFirst()) {
						TableRow rowProduct = new TableRow(this);
						addViewInRow(rowProduct, getString(i), null,
								Gravity.CENTER_HORIZONTAL);
						addViewInRow(rowProduct, product.getString(3), null,
								Gravity.CENTER_HORIZONTAL);
						addViewInRow(rowProduct, product.getString(4), null,
								Gravity.CENTER_HORIZONTAL);
						addViewInRow(rowProduct, product.getString(5), null,
								Gravity.CENTER_HORIZONTAL);
						addViewInRow(rowProduct, request_products.getString(3),
								null, Gravity.CENTER_HORIZONTAL);

						tableProducts.addView(rowProduct);
					}

				}
			}
		} else {
			addViewInRowEdit(rowProvider, "", null, Gravity.CENTER_HORIZONTAL);

			addViewInRowEdit(rowDate, "", null, Gravity.CENTER_HORIZONTAL);

			addViewInRowEdit(rowAllCost, "", null, Gravity.CENTER_HORIZONTAL);

			table.addView(rowTitleRequest);
			table.addView(rowProvider);
			table.addView(rowDate);
			table.addView(rowAllCost);
			tableProducts.addView(rowTitleProducts);
			tableProducts.addView(rowProductsLabel);

		}

		return table;
	}

	private void addViewInRow(TableRow rowName, String text, Typeface tf,
			int gravity) {
		TextView label = new TextView(this);
		label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		label.setText(text);
		if (tf != null) {
			label.setTypeface(tf);
		}
		label.setGravity(gravity);

		rowName.addView(label);
	}

	private void addViewInRowEdit(TableRow rowName, String text, Typeface tf,
			int gravity) {
		EditText label = new EditText(this);
		label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
		label.setText(text);
		if (tf != null) {
			label.setTypeface(tf);
		}
		label.setGravity(gravity);

		rowName.addView(label);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.add_edit_request, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home: {
			finish();
			break;
		}
		default:
			return false;
		}

		return true;
	}

}
