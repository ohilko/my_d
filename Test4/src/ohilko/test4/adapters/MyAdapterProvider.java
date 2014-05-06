package ohilko.test4.adapters;

import java.util.ArrayList;

import ohilko.test4.R;
import ohilko.test4.models.Provider;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MyAdapterProvider extends BaseAdapter {

	private ArrayList<Provider> listProviders;
	private ArrayList<Provider> originalListProviders;
	private Context context;

	public MyAdapterProvider(Context context, ArrayList<Provider> listProviders) {
		this.context = context;
		this.listProviders = new ArrayList<Provider>();
		this.listProviders.addAll(listProviders);
		this.originalListProviders = new ArrayList<Provider>();
		this.originalListProviders.addAll(listProviders);
	}

	@Override
	public int getCount() {
		return listProviders.size();
	}

	@Override
	public Object getItem(int position) {
		Provider provider = listProviders.get(position);
		return provider;
	}

	@Override
	public long getItemId(int position) {
		Provider provider = listProviders.get(position);
		return provider.getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Provider provider = listProviders.get(position);

		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater.inflate(R.layout.row_add_provider,
					null);
		}

		TextView name = (TextView) convertView.findViewById(R.id.textView_name);
		TextView address = (TextView) convertView
				.findViewById(R.id.textView_address);
		TextView phone = (TextView) convertView
				.findViewById(R.id.textView_phone);

		name.setText(provider.getName());
		address.setText(provider.getAddress());
		phone.setText(provider.getPhone());

		return convertView;
	}

	public void filterData(String query) {

		query = query.toLowerCase();
		listProviders.clear();

		if (query.isEmpty()) {
			listProviders.addAll(originalListProviders);
		} else {
			ArrayList<Provider> newList = new ArrayList<Provider>();
			for (Provider provider : originalListProviders) {
				if (provider.getName().toLowerCase().contains(query)) {
					newList.add(provider);
				}
			}
			if (newList.size() > 0) {
				listProviders.addAll(newList);
			}
		}
		notifyDataSetChanged();

	}

}
