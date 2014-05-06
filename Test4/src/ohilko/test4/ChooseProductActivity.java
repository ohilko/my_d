package ohilko.test4;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ohilko.test4.R;
import ohilko.test4.adapters.MyAdapterProduct;
import ohilko.test4.db.DatabaseConnector;
import ohilko.test4.models.Product;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

public class ChooseProductActivity extends Activity implements
		SearchView.OnQueryTextListener, SearchView.OnCloseListener {

	public static final String PRODUCTS_ID = "products_id";
	public static final String PRODUCTS_AMOUNT = "amount_products";
	private ListView myListView;
	private TextView sum;
	private Button addProducts;
	private DatabaseConnector db;
	private ArrayList<Product> listproduct;
	private MyAdapterProduct adapter;
	private double count = 0;
	private List<Long> products_id = new ArrayList<Long>();
	private List<String> products_amount = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_product);

		listproduct = new ArrayList<Product>();

		loadData(listproduct);

		myListView = (ListView) findViewById(R.id.listView_products);
		adapter = new MyAdapterProduct(this, listproduct);
		myListView.setAdapter(adapter);
		myListView.setOnItemClickListener(setAmountListener);

		sum = (TextView) findViewById(R.id.textView_sum);
		sum.setText("0");
		
		addProducts = (Button) findViewById(R.id.button_add_products);
		addProducts.setOnClickListener(addProductsClicked);

	}

	OnClickListener addProductsClicked = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent answerInent = new Intent();
			long[] productsId = new long[products_id.size()];
			String[] productsAmount = new String[products_amount.size()];
			
			Iterator<String> products1 = products_amount.iterator();
			Iterator<Long> products2 = products_id.iterator();
			
			int i = 0;
			while (products2.hasNext()) {
				productsAmount[i] = products1.next();
				productsId[i] = products2.next();
				i++;
			}
			
			answerInent.putExtra(PRODUCTS_ID, productsId);
			answerInent.putExtra(PRODUCTS_AMOUNT, productsAmount);
			
			setResult(RESULT_OK, answerInent);
			finish();
		}
	};
	
	OnItemClickListener setAmountListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			Product product = listproduct.get(position);
			count = Integer.parseInt(product.getAmount());
			showDialog(product, position, arg1);

		}
	};

	private void showDialog(final Product product, final int positon,
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
		final double summa = count
				* Double.parseDouble(price.getText().toString());
		sum1.setText(Double.toString(summa));

		final EditText amount = (EditText) linearlayout
				.findViewById(R.id.editText_amount);

		if (!product.getAmount().equals("0")) {
			amount.setText(product.getAmount());
		}

		amount.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if (!s.toString().equals("")) {
					count = Double.parseDouble(s.toString());
					sum1.setText(Double.toString(count
							* Double.parseDouble(price.getText().toString())));
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start,
					int count1, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count1) {
			}
		});

		dialog.setPositiveButton("Готово",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						double oldAmount = Double.parseDouble(product
								.getAmount());
						double priceDouble = Double.parseDouble(price.getText()
								.toString());

						if (amount.getText().equals("")) {
							product.setAmount("0");
						} else {
							product.setAmount(amount.getText().toString());
						}
						listproduct.set(positon, product);

						if (!product.getAmount().equals("0")) {
							products_amount.add(product.getAmount());
							products_id.add(product.getId());
						} else {
							if (products_id.indexOf(product.getId()) != -1) {
								products_amount.remove(products_id.indexOf(product.getId()));
								products_id.remove(products_id.indexOf(product.getId()));
							}
						}
						
						TextView amount = (TextView) view
								.findViewById(R.id.textView_amount);						
						amount.setText(product.getAmount());
						
						if (sum.getText().equals("0")) {
							sum.setText(Double.toString(count * priceDouble));
						} else {
							double sumDouble = Double.parseDouble(sum.getText()
									.toString());
							sumDouble = (sumDouble - oldAmount * priceDouble)
									+ count * priceDouble;
							sum.setText(Double.toString(sumDouble));
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

	private void loadData(ArrayList<Product> listproduct) {
		db = new DatabaseConnector(this);
		db.open();
		Cursor products = db.getRow(DatabaseConnector.TABLE_NAME[0],
				DatabaseConnector.PRODUCT_FIELDS, "isDirectory", "false");

		String[] prs = new String[products.getCount()];
		int i = 0;

		while (products.moveToNext()) {
			Product product = new Product(products.getString(3),
					products.getString(4), products.getString(5), "0",
					products.getLong(0));
			listproduct.add(product);

			prs[i] = products.getString(3);
			i++;
		}

		db.close();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.choose_product, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(
				R.id.menu_item_search).getActionView();

		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		searchView.setSubmitButtonEnabled(true);
		searchView.setOnQueryTextListener(this);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onClose() {
		adapter.filterData("");
		return false;
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		adapter.filterData(newText);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		adapter.filterData(query);
		return false;
	}
}
