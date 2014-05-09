package ohilko.test4;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import ohilko.test4.R;
import ohilko.test4.adapters.MyAdapterProduct;
import ohilko.test4.db.DatabaseConnector;
import ohilko.test4.models.Product;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class AddEditRequestActivity extends Activity {

	protected static final int ADD_PRODUCT = 27;
	protected static final int ADD_PROVIDER = 28;
	private long rowID;
	private long providerID;
	private DatabaseConnector db;
	private static int COUNTCOLOMNS = 5;
	private Cursor request;
	private ListView list_request_products;
	private TextView allCost_all;
	private TextView provider_name;
	private long[] productsId = null;
	private String[] productsAmount = null;
	private ArrayList<Product> listproduct = new ArrayList<Product>();;
	private MyAdapterProduct adapter;
	private double count = 0;
	private EditText amount_dialog;
	private TextView date;

	private OnClickListener providerClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent request = new Intent(AddEditRequestActivity.this,
					ChooseProviderActivity.class);
			startActivityForResult(request, ADD_PROVIDER);
		}
	};

	private OnClickListener addProductClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent request = new Intent(AddEditRequestActivity.this,
					ChooseProductActivity.class);
			if (listproduct.size() != 0) {
				productsId = new long[listproduct.size()];
				productsAmount = new String[listproduct.size()];

				Iterator<Product> iter = listproduct.iterator();
				int i = 0;
				while (iter.hasNext()) {
					Product product = iter.next();
					productsId[i] = product.getId();
					productsAmount[i] = product.getAmount();
					i++;
				}

				request.putExtra(ChooseProductActivity.PRODUCTS_ID, productsId);
				request.putExtra(ChooseProductActivity.PRODUCTS_AMOUNT,
						productsAmount);
				request.putExtra(ChooseProductActivity.ALL_COST, allCost_all
						.getText().toString());
			}
			startActivityForResult(request, ADD_PRODUCT);
		}
	};

	private OnClickListener saveClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (date.getText().toString().equals("")
					|| provider_name.getText().toString().equals("")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						AddEditRequestActivity.this);

				builder.setTitle(R.string.errorTitleEmptyField);
				builder.setMessage(R.string.errorMessageEmptyFields);
				builder.setPositiveButton(R.string.errorButton, null);
				builder.show();
			} else {
				String[] data = new String[4];
				data[0] = Long.toString(providerID);
				data[1] = date.getText().toString();
				data[2] = allCost_all.getText().toString();
				data[3] = "false";

				if (rowID == 0) {
					db.insertRow(DatabaseConnector.TABLE_NAME[2],
							DatabaseConnector.REQUEST_FIELDS, data);
				} else {
					db.updateRow(rowID, DatabaseConnector.TABLE_NAME[2],
							DatabaseConnector.REQUEST_FIELDS, data);
				}
				Intent request = new Intent(AddEditRequestActivity.this,
						ListRequestActivity.class);
				startActivity(request);
			}
		}
	};

	private OnItemClickListener changeAmountClicked = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			Product product = listproduct.get(position);
			showDialog(product, position, arg1);

		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_edit_request);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		provider_name = (TextView) findViewById(R.id.textView_provider_name);
		date = (TextView) findViewById(R.id.editText_date);
		allCost_all = (TextView) findViewById(R.id.textView_allCost);
		allCost_all.setText("0");

		Button buttonAddProduct = (Button) findViewById(R.id.button_add_product);
		buttonAddProduct.setOnClickListener(addProductClicked);
		Button buttonSave = (Button) findViewById(R.id.button_save);
		buttonSave.setOnClickListener(saveClicked);

		Date date1 = new Date();

		SimpleDateFormat formatter1 = new SimpleDateFormat("dd.MM.yyyy");
		String date_time = formatter1.format(date1);

		date.setText(date_time);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			rowID = extras.getLong(ListRequestActivity.ROW_ID);
			provider_name.setText(extras
					.getString(ChooseProviderActivity.PROVIDER_NAME));

		} else {
			rowID = 0;
		}

		list_request_products = (ListView) findViewById(R.id.list_request_products);

		provider_name.setOnClickListener(providerClicked);

		db = new DatabaseConnector(this);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == ADD_PRODUCT) {
			if (resultCode == RESULT_OK) {
				productsId = data.getExtras().getLongArray(
						ChooseProductActivity.PRODUCTS_ID);
				productsAmount = data.getExtras().getStringArray(
						ChooseProductActivity.PRODUCTS_AMOUNT);
				allCost_all.setText(data.getExtras().getString(
						ChooseProductActivity.ALL_COST));

				db.open();
				getProductsForRequest();
				db.close();
			}
		}

		if (requestCode == ADD_PROVIDER) {
			if (resultCode == RESULT_OK) {
				providerID = data.getExtras().getLong(
						ChooseProviderActivity.PROVIDER_ID);
				provider_name.setText(data.getExtras().getString(
						ChooseProviderActivity.PROVIDER_NAME));

			}
		}
	}

	private void showDialog(final Product product, final int position,
			final View view) {
		final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

		dialog.setTitle("Set amount");

		View linearlayout = getLayoutInflater().inflate(R.layout.dialog, null);
		dialog.setView(linearlayout);

		final TextView price = (TextView) linearlayout
				.findViewById(R.id.textView_price_text);
		price.setText(product.getPrice());

		final TextView sum1 = (TextView) linearlayout
				.findViewById(R.id.textView_sum_text);
		final double summa = Double.parseDouble(product.getAmount())
				* Double.parseDouble(price.getText().toString());
		sum1.setText(Double.toString(summa));

		amount_dialog = (EditText) linearlayout
				.findViewById(R.id.editText_amount);

		if (!product.getAmount().equals("0")) {
			amount_dialog.setText(product.getAmount());
		}

		amount_dialog.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count1, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count1) {
				if (!s.toString().equals("")) {
					count = Double.parseDouble(s.toString());
					sum1.setText(Double.toString(count
							* Double.parseDouble(price.getText().toString())));
				} else {
					count = 0;
					sum1.setText("0");
				}
			}
		});

		dialog.setPositiveButton("Готово",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						double oldAmount = Double.parseDouble(product
								.getAmount());
						double priceDouble = Double.parseDouble(price.getText()
								.toString());
						double sumDouble = Double.parseDouble(allCost_all
								.getText().toString());

						if (amount_dialog.getText().toString().equals("")
								|| amount_dialog.getText().toString()
										.equals("0")) {
							sumDouble = sumDouble - oldAmount * priceDouble;
							allCost_all.setText(Double.toString(sumDouble));
							adapter.remove(position);
							listproduct.remove(position);

						} else {
							product.setAmount(amount_dialog.getText()
									.toString());
							listproduct.set(position, product);

							TextView amount = (TextView) view
									.findViewById(R.id.textView_amount);
							amount.setText(product.getAmount());

							sumDouble = (sumDouble - oldAmount * priceDouble)
									+ count * priceDouble;
							allCost_all.setText(Double.toString(sumDouble));
						}
						dialog.dismiss();
					}
				})

		.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});

		dialog.create();
		dialog.show();

	}

	private void getProductsForRequest() {
		if (productsId != null) {
			for (int i = 0; i < productsId.length; i++) {
				Cursor product = db.getRowById(DatabaseConnector.TABLE_NAME[0],
						productsId[i]);

				if (product.moveToFirst()) {
					Product newProduct = new Product(product.getString(3),
							product.getString(4), product.getString(5),
							productsAmount[i], Long.parseLong(product
									.getString(0)));
					listproduct.add(newProduct);
				}
			}
			adapter = new MyAdapterProduct(this, listproduct);
			adapter.notifyDataSetChanged();
			list_request_products.setAdapter(adapter);
			list_request_products.setOnItemClickListener(changeAmountClicked);
		}
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

		addViewInRow(rowProductsLabel, "№", null, Gravity.CENTER_HORIZONTAL);

		for (int i = 3; i < COUNTCOLOMNS + 1; i++) {
			addViewInRow(rowProductsLabel, DatabaseConnector.PRODUCT_FIELDS[i],
					null, Gravity.CENTER_HORIZONTAL);
		}
		addViewInRow(rowProductsLabel, "Amount", null,
				Gravity.CENTER_HORIZONTAL);

		if (rowID != 0) {
			request = db.getRowById(DatabaseConnector.TABLE_NAME[2], rowID);

			if (request.moveToFirst()) {
				Cursor provider = db.getRowById(
						DatabaseConnector.TABLE_NAME[1],
						Long.parseLong(request.getString(1)));

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
						DatabaseConnector.TABLE_NAME[4], null,
						new String[] { "request_id" },
						new String[] { request.getString(1) });

				while (request_products.moveToNext()) {
					Cursor product = db.getRowById(
							DatabaseConnector.TABLE_NAME[0],
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
