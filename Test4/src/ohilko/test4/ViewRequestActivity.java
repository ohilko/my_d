package ohilko.test4;

import java.util.ArrayList;
import java.util.Iterator;

import ohilko.test4.R;
import ohilko.test4.adapters.MyAdapterProduct;
import ohilko.test4.db.DatabaseConnector;
import ohilko.test4.models.Product;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface;
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
	private Cursor request;
	private TextView provider_name;
	private TextView date;
	private TextView allCost;
	private ListView list_request_products;
	private ArrayList<Product> listproduct = new ArrayList<Product>();
	private MyAdapterProduct adapter;
	private Cursor request_products;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_request);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		provider_name = (TextView) findViewById(R.id.textView_provider_name);
		date = (TextView) findViewById(R.id.editText_date);
		allCost = (TextView) findViewById(R.id.textView_allCost);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			rowID = extras.getLong(ListRequestActivity.ROW_ID);
			provider_name.setText(extras
					.getString(ListRequestActivity.PROVIDER_NAME));
			date.setText(extras.getString(ListRequestActivity.DATE));
			allCost.setText(extras.getString(ListRequestActivity.ALL_COST));
		}

		db = new DatabaseConnector(this);
		db.open();

		request = db.getRowById(DatabaseConnector.TABLE_NAME[2], rowID);
		if (request.moveToFirst()) {

		}
		list_request_products = (ListView) findViewById(R.id.list_request_products);

		getProductsForRequest();

		// TableLayout[] tables = createTable();
		//
		// setContentView(tables[0]);
		// setContentView(tables[1]);
		db.close();
	}

	private void getProductsForRequest() {
		if (request.moveToFirst()) {
			request_products = db.getRow(DatabaseConnector.TABLE_NAME[4], null,
					new String[] { "request_id" },
					new String[] { Long.toString(request.getLong(0)) });

			while (request_products.moveToNext()) {
				Cursor product = db.getRowById(DatabaseConnector.TABLE_NAME[0],
						Long.parseLong(request_products.getString(2)));

				if (product.moveToFirst()) {
					Product newProduct = new Product(product.getString(3),
							product.getString(4), product.getString(5),
							request_products.getString(3),
							Long.parseLong(product.getString(0)));
					listproduct.add(newProduct);

				}
			}
		}

		adapter = new MyAdapterProduct(this, listproduct);
		adapter.notifyDataSetChanged();
		list_request_products.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sd, menu);

		SubMenu sm = menu.addSubMenu(Menu.FIRST, 1, 1, "SubMenu").setIcon(
				android.R.drawable.ic_dialog_dialer);
		menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		sm.add(Menu.FIRST, 300, 300, "Delete").setIcon(
				android.R.drawable.ic_menu_delete);
		if (request.moveToFirst() && request.getString(4).equals("false")) {
			sm.add(Menu.FIRST, 400, 400, "Edit requests").setIcon(
					android.R.drawable.ic_menu_edit);
		}

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
					AboutActivity.class);
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
			AlertDialog.Builder builder = new AlertDialog.Builder(
					ViewRequestActivity.this);

			builder.setTitle(R.string.errorTitleDelete);
			builder.setMessage(R.string.errorMessageDeleteRequest);
			OnClickListener listenerYes = new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					while (request_products.moveToFirst()) {
						db.deleteRow(DatabaseConnector.TABLE_NAME[4],
								Long.parseLong(request_products.getString(0)));
					}
					db.deleteRow(DatabaseConnector.TABLE_NAME[2], rowID);
					startActivity(new Intent(ViewRequestActivity.this,
							ListRequestActivity.class));
				}
			};
			builder.setPositiveButton(R.string.errorButtonYes, listenerYes);
			builder.setNegativeButton(R.string.errorButtonNo, null);
			builder.show();
			break;
		}
		case 400: {
			Intent intent = new Intent(ViewRequestActivity.this,
					AddEditRequestActivity.class);

			intent.putExtra(ChooseProviderActivity.PROVIDER_NAME,
					provider_name.getText());
			intent.putExtra(ListRequestActivity.DATE, date.getText());
			intent.putExtra(ListRequestActivity.ALL_COST, allCost.getText());
			intent.putExtra(ListRequestActivity.ROW_ID, rowID);

			Iterator<Product> iter = listproduct.iterator();
			long[] productId = new long[listproduct.size()];
			String[] productAmount = new String[listproduct.size()];
			int i = 0;

			while (iter.hasNext()) {
				Product product = iter.next();
				productId[i] = product.getId();
				productAmount[i] = product.getAmount();
				i++;
			}
			intent.putExtra(ChooseProductActivity.PRODUCTS_ID, productId);
			intent.putExtra(ChooseProductActivity.PRODUCTS_AMOUNT,
					productAmount);

			startActivity(intent);
			break;
		}
		default:
			return false;
		}

		return true;
	}
}
