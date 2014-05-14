package ohilko.test4.adapters;

import java.util.ArrayList;

import ohilko.test4.R;
import ohilko.test4.models.Request;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapterRequest extends BaseAdapter {
	private Context context;
	private ArrayList<Request> listRequest;

//	public MyAdapterRequest(Context context,
//			ArrayList<HashMap<String, Object>> data, int resource, String[] from,
//			int[] to) {
//		super(context, data, resource, from, to);
//		this.context = context;
//		this.resource = resource;
//		this.from = new String[from.length];
//		this.from = from.clone();
//		this.to = new int[to.length];
//		this.to = to.clone();
//		this.myRequests = new ArrayList<HashMap<String,Object>>();
//		this.myRequests.addAll(data);
//		
//	}

	 public MyAdapterRequest(Context context, ArrayList<Request> list) {
	 this.context = context;
	 this.listRequest = new ArrayList<Request>();
	 this.listRequest.addAll(list);
	 }

	public void remove(int position) {
		listRequest.remove(position);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return listRequest.size();
	}

	@Override
	public Object getItem(int position) {
		Request request = listRequest.get(position);
		return request;
	}

	@Override
	public long getItemId(int position) {
		Request request = listRequest.get(position);
		return request.getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Request request = listRequest.get(position);

		if (convertView == null) {
			LayoutInflater layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = layoutInflater
					.inflate(R.layout.row, null);
		}

		TextView providerName = (TextView) convertView
				.findViewById(R.id.textview_provider);
		TextView date = (TextView) convertView.findViewById(R.id.textview_date);
		TextView allCost = (TextView) convertView
				.findViewById(R.id.textview_allcost);
		ImageView image = (ImageView) convertView.findViewById(R.id.imageView_list);
		

		providerName.setText(request.getProviderName());
		date.setText(request.getDate());
		allCost.setText(request.getAllCost());
		image.setImageResource(request.getImage());

		return convertView;
	}

}
