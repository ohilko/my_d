package ohilko.test4.dropbox;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ohilko.test4.R;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.OnItemClickListener;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

public class OpenDropboxFileTask extends Dialog implements OnClickListener {

	private static final String FILE_KEY = "filename";
	private static final String IMAGE_KEY = "fileimage";

	private Entry currentDir = null;
	private ListView view = null;
	private FilenameFilter filter = null;
	private DropboxAPI<AndroidAuthSession> dropbox;
	private String path;
	private boolean isDirectory = false;

	public OpenDropboxFileTask(Context context, String dir,
			DropboxAPI<AndroidAuthSession> dropbox) {
		super(context);
		this.dropbox = dropbox;
		this.path = dir;

		setContentView(R.layout.ofd_layout);
		setTitle(R.string.ofd_title);

		view = (ListView) findViewById(R.id.ofd_list);
		fillList();
		view.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Map<String, Object> listItem = (Map<String, Object>) a
						.getItemAtPosition(position);
				String name = listItem.get(FILE_KEY).toString();
//				GetDropboxFile task = new GetDropboxFile(getContext(), name);
//				task.execute();
				isDirectory = true;
				if (isDirectory) {
					ListDropboxFiles list = new ListDropboxFiles("/"+name, getContext());
					list.execute();
				}
			}
		});
		Button upButton = (Button) findViewById(R.id.ofd_go_up);
		upButton.setOnClickListener(this);
	}

	private void fillList() {
		ListDropboxFiles list = new ListDropboxFiles(path, getContext());
		list.execute();
	}

	public void onClick(View v) {
	}

	class ListDropboxFiles extends AsyncTask<Void, Void, List<Map<String, ?>>> {

		private String path;
		private Context context;
		private ProgressDialog statusDialog;

		public ListDropboxFiles(String path, Context context) {
			this.path = path;
			this.context = context;
		}

		@Override
		protected List<Map<String, ?>> doInBackground(Void... params) {
			List<Map<String, ?>> listFiles = new ArrayList<Map<String, ?>>();
			try {
				Entry directory = dropbox
						.metadata(path, 1000, null, true, null);
				for (Entry entry : directory.contents) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put(FILE_KEY, entry.fileName());

					int imageId = R.drawable.ic_osdialogs_file;
					if (entry.isDir) {
						imageId = R.drawable.ic_osdialogs_dir;
					}
					map.put(IMAGE_KEY, imageId);
					listFiles.add(map);
				}
			} catch (DropboxException e) {
				e.printStackTrace();
			}

			return listFiles;
		}

		protected void onPreExecute() {
//			statusDialog = new ProgressDialog(context);
//			statusDialog.setMessage("Подождите...");
//			statusDialog.setIndeterminate(false);
//			statusDialog.setCancelable(false);
//			statusDialog.show();
		}

		@Override
		protected void onPostExecute(List<Map<String, ?>> result) {
			SimpleAdapter adapter = new SimpleAdapter(context, result,
					R.layout.ofd_list_item,
					new String[] { FILE_KEY, IMAGE_KEY }, new int[] {
							R.id.ofd_item_text, R.id.ofd_item_image });
			view.setAdapter(adapter);

		}
	}

	private class GetDropboxFile extends AsyncTask<Void, Void, Boolean> {

		private String fileName;
		private ProgressDialog statusDialog;
		private Context context;

		public GetDropboxFile(Context conext, String fileName) {
			this.fileName = fileName;
			this.context = conext;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			FileOutputStream outputStream = null;
			try {
				File sdCard = Environment.getExternalStorageDirectory();
				File file = new File(sdCard.getAbsolutePath() + "/temp" + fileName);
				outputStream = new FileOutputStream(file);
				@SuppressWarnings("unused")
				DropboxFileInfo info = dropbox.getFile(fileName, null,
						outputStream, null);
				
			} catch (Exception e) {
				return false;
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
					}
				}
			}
			return true;
		}
		
		protected void onPreExecute() {
//			statusDialog = new ProgressDialog(context);
//			statusDialog.setMessage("Подождите...");
//			statusDialog.setIndeterminate(false);
//			statusDialog.setCancelable(false);
//			statusDialog.show();
		}

		@Override
		protected void onPostExecute(Boolean result) {
			if (!result) {
				isDirectory = true;
			}
		}

	}
}
