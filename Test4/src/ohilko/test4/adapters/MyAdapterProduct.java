package ohilko.test4.adapters;

import java.util.ArrayList;

import ohilko.test4.R;
import ohilko.test4.R.id;
import ohilko.test4.R.layout;
import ohilko.test4.models.Product;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapterProduct extends BaseAdapter {

	private Context context;
	private ArrayList<Product> listProducts;
	private ArrayList<Product> originalListProducts;

	public MyAdapterProduct(Context context, ArrayList<Product> list) {
		this.context = context;
		this.listProducts = new ArrayList<Product>();
		this.listProducts.addAll(list);
		this.originalListProducts = new ArrayList<Product>();
		this.originalListProducts.addAll(list);
	}

	public void remove(int position) {
		listProducts.remove(position);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return listProducts.size();
	}

	@Override
	public Object getItem(int position) {
		Product product = listProducts.get(position);
		return product;
	}

	@Override
	public long getItemId(int position) {
		Product product = listProducts.get(position);
		return product.getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Product product = listProducts.get(position);

		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater
					.inflate(R.layout.row_add_product, null);
		}

		TextView name = (TextView) convertView.findViewById(R.id.textView_name);
		TextView um = (TextView) convertView.findViewById(R.id.textView_um);
		TextView price = (TextView) convertView
				.findViewById(R.id.textView_price);
		TextView amount = (TextView) convertView
				.findViewById(R.id.textView_amount);

		name.setText(product.getName());
		um.setText(product.getUm());
		price.setText(product.getPrice());
		amount.setText(product.getAmount());

		return convertView;
	}

	public void filterData(String query) {

		query = query.toLowerCase();
		listProducts.clear();

		if (query.isEmpty()) {
			listProducts.addAll(originalListProducts);
		} else {
			ArrayList<Product> newList = new ArrayList<Product>();
			for (Product product : originalListProducts) {
				if (product.getName().toLowerCase().contains(query)) {
					newList.add(product);
				}
			}
			if (newList.size() > 0) {
				listProducts.addAll(newList);
			}
		}
		notifyDataSetChanged();

	}

}
