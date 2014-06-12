package ohilko.test4;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;

public class AddEditRequestActivity extends Activity {

	protected static final int ADD_PRODUCT = 27;
	protected static final int ADD_PROVIDER = 28;
	private long rowID;
	private long providerID;
	private DatabaseConnector db;
	private ListView list_request_products;
	private ListView list_lable_request_products;
	private TextView allCost_all;
	private TextView provider_name;
	private long[] productsId = null;
	private String[] productsAmount = null;
	private ArrayList<Product> listproduct = new ArrayList<Product>();
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
					long id = db.insertRow(DatabaseConnector.TABLE_NAME[2],
							DatabaseConnector.REQUEST_FIELDS, data);
					if (listproduct.size() != 0) {
						Iterator<Product> iter = listproduct.iterator();
						while (iter.hasNext()) {
							Product product = iter.next();

							String[] dataProduct = new String[3];
							dataProduct[0] = Long.toString(id);
							dataProduct[1] = Long.toString(product.getId());
							dataProduct[2] = product.getAmount();
							db.insertRow(DatabaseConnector.TABLE_NAME[4],
									DatabaseConnector.REQUEST_PRODUCT_FIELDS,
									dataProduct);
						}
					}
				} else {
					db.open();
					Cursor products_request = db.getRow(
							DatabaseConnector.TABLE_NAME[4],
							DatabaseConnector.REQUEST_PRODUCT_FIELDS,
							new String[] { "request_id" },
							new String[] { Long.toString(rowID) });
					while (products_request.moveToNext()) {
						db.deleteRow(DatabaseConnector.TABLE_NAME[4],
								products_request.getLong(0));
					}

					db.updateRow(rowID, DatabaseConnector.TABLE_NAME[2],
							DatabaseConnector.REQUEST_FIELDS, data);

					if (listproduct.size() != 0) {
						Iterator<Product> iter = listproduct.iterator();
						while (iter.hasNext()) {
							Product product = iter.next();

							String[] dataProduct = new String[3];
							dataProduct[0] = Long.toString(rowID);
							dataProduct[1] = Long.toString(product.getId());
							dataProduct[2] = product.getAmount();
							db.insertRow(DatabaseConnector.TABLE_NAME[4],
									DatabaseConnector.REQUEST_PRODUCT_FIELDS,
									dataProduct);
						}
					}
				}
				db.close();
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

		db = new DatabaseConnector(this);
		list_request_products = (ListView) findViewById(R.id.list_request_products);
//		list_lable_request_products = (ListView) findViewById(R.id.list_lable_request_products);
//		
//		ArrayList<Product> lable_list = new ArrayList<Product>();
//		lable_list.add(new Product("Название", "Ед.Из.", "Цена", "Кол-во", 1));
//		list_lable_request_products.setAdapter(new MyAdapterProduct(this, lable_list));
		
		provider_name.setOnClickListener(providerClicked);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			rowID = extras.getLong(ListRequestActivity.ROW_ID);
			db.open();
			Cursor provider = db.getRowById(DatabaseConnector.TABLE_NAME[2], rowID);
			if (provider.moveToFirst()) {
				providerID = Long.parseLong(provider.getString(1));
			}
			if (extras.getString(ChooseProviderActivity.PROVIDER_NAME) != null) {
				provider_name.setText(extras
						.getString(ChooseProviderActivity.PROVIDER_NAME));
			}
			if (extras.getString(ListRequestActivity.DATE) != null) {
				date.setText(extras.getString(ListRequestActivity.DATE));
			}
			if (extras.getString(ListRequestActivity.ALL_COST) != null) {
				allCost_all.setText(extras
						.getString(ListRequestActivity.ALL_COST));
			}
			if (extras.getLongArray(ChooseProductActivity.PRODUCTS_ID) != null) {
				productsId = extras
						.getLongArray(ChooseProductActivity.PRODUCTS_ID);
				productsAmount = extras
						.getStringArray(ChooseProductActivity.PRODUCTS_AMOUNT);
				db.open();
				getProductsForRequest();
				db.close();
			}
		} else {
			rowID = 0;
		}
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

		dialog.setTitle("Установите количество товара");

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
