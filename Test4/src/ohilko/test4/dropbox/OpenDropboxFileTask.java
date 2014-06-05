package ohilko.test4.dropbox;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ohilko.test4.R;
import ohilko.test4.db.DatabaseConnector;
import ohilko.test4.db.ParserXmlFile;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import android.widget.TextView;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;

public class OpenDropboxFileTask extends Dialog implements OnClickListener {

	private static final String FILE_KEY = "filename";
	private static final String IMAGE_KEY = "fileimage";

	private ListView view = null;
	private Context context;
	private TextView currentPath;
	private DropboxAPI<AndroidAuthSession> dropbox;
	private String path;
	private List<String> old_paths = new ArrayList<String>();
	private boolean isDownload;

	public OpenDropboxFileTask(final Context context, String dir,
			DropboxAPI<AndroidAuthSession> dropbox, final boolean isDownload) {
		super(context);
		this.dropbox = dropbox;
		this.path = dir;
		this.context = context;
		this.isDownload = isDownload;

		if (!isDownload) {
			setContentView(R.layout.ofd_layout_unload);
			Button choose = (Button) findViewById(R.id.of_choose);
			choose.setOnClickListener(this);
		} else {
			setContentView(R.layout.ofd_layout);
		}
		setTitle(R.string.ofd_title);

		currentPath = (TextView) findViewById(R.id.ofd_current_path);
		currentPath.setText(path);

		view = (ListView) findViewById(R.id.ofd_list);
		fillList(true);
		view.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("unchecked")
			public void onItemClick(AdapterView<?> a, View v, int position,
					long id) {
				Map<String, Object> listItem = (Map<String, Object>) a
						.getItemAtPosition(position);
				int idImageItem = Integer.parseInt(listItem.get(IMAGE_KEY)
						.toString());
				String name = listItem.get(FILE_KEY).toString();

				if (idImageItem == R.drawable.ic_osdialogs_dir) {
					old_paths.add(path);
					path += "/" + name;
					fillList(false);
				}
				String[] splitPath = name.split("\\.");
				if (idImageItem == R.drawable.ic_osdialogs_file
						&& splitPath[1].equals("xml") && isDownload) {
					GetDropboxFile task = new GetDropboxFile(getContext(), name);
					task.execute();
				}

			}
		});
		Button upButton = (Button) findViewById(R.id.ofd_go_up);
		upButton.setOnClickListener(this);
	}

	private void fillList(boolean isFirst) {
		ListDropboxFiles list = new ListDropboxFiles(path, context, isFirst);
		list.execute();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.of_choose: {
			PutDropboxFile task = new PutDropboxFile(getContext(), path);
			task.execute();
		}
			break;
		case R.id.ofd_go_up: {
			if (old_paths.size() != 0) {
				path = old_paths.get(old_paths.size() - 1);
				old_paths.remove(old_paths.size() - 1);
				fillList(false);
			}
		}
			break;

		default:
			break;
		}
	}

	class ListDropboxFiles extends AsyncTask<Void, Void, List<Map<String, ?>>> {

		private String path;
		private Context context;
		private boolean isFirst;
		private ProgressDialog statusDialog;

		public ListDropboxFiles(String path, Context context, boolean isFirst) {
			this.path = path;
			this.context = context;
			this.isFirst = isFirst;
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
			if (isFirst) {
				// statusDialog = new ProgressDialog(context);
				// statusDialog.setMessage("Подождите...");
				// statusDialog.setIndeterminate(false);
				// statusDialog.setCancelable(false);
				// statusDialog.show();
				Toast error = Toast.makeText(context, "Подождите..",
						Toast.LENGTH_SHORT);
				error.show();
			}

		}

		@Override
		protected void onPostExecute(List<Map<String, ?>> result) {
			SimpleAdapter adapter = new SimpleAdapter(context, result,
					R.layout.ofd_list_item,
					new String[] { FILE_KEY, IMAGE_KEY }, new int[] {
							R.id.ofd_item_text, R.id.ofd_item_image });
			view.setAdapter(adapter);
			currentPath.setText(path);

		}
	}

	private class GetDropboxFile extends AsyncTask<Void, Void, File> {

		private String fileName;
		private Context context;

		public GetDropboxFile(Context conext, String fileName) {
			this.fileName = fileName;
			this.context = conext;
		}

		@Override
		protected File doInBackground(Void... params) {
			FileOutputStream outputStream = null;
			File file = null;
			try {
				file = new File("/sdcard/tmp/" + fileName);
				outputStream = new FileOutputStream(file);
				@SuppressWarnings("unused")
				DropboxFileInfo info = dropbox.getFile(path + "/" + fileName,
						null, outputStream, null);
				DatabaseConnector db = new DatabaseConnector(context);
				try {
					db.open();
					ParserXmlFile parser = new ParserXmlFile(file, context, db);
					parser.parser();
					parser.addInTableProductChild();
				} finally {
					db.close();
				}

			} catch (Exception e) {
				return null;
			} finally {
				if (outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
					}
				}
			}
			return file;
		}

		protected void onPreExecute() {
			Toast error = Toast.makeText(context, "Идет обработка данных..",
					Toast.LENGTH_SHORT);
			error.show();
		}

		@Override
		protected void onPostExecute(File result) {
			dismiss();
		}

	}

	private class PutDropboxFile extends AsyncTask<Void, Void, File> {

		private String dirPath;
		private Context context;

		public PutDropboxFile(Context conext, String dirPath) {
			this.dirPath = dirPath;
			this.context = conext;
		}

		@Override
		protected File doInBackground(Void... params) {
			File file = new File("/sdcard/tmp/requests.xml");
			DatabaseConnector db = new DatabaseConnector(context);
			try {
				db.open();
				ParserXmlFile parser = new ParserXmlFile(file, context, db);
				parser.fillXmlFile();
			} finally {
				db.close();
			}

			FileInputStream inputStream = null;
			try {
				inputStream = new FileInputStream(file);
				dropbox.putFile(dirPath + "/" + file.getName(), inputStream,
						file.length(), null, null);

			} catch (Exception e) {
				return null;
			} finally {
				if (inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException e) {
					}
				}
			}
			return file;
		}

		protected void onPreExecute() {
			Toast error = Toast.makeText(context, "Идет обработка данных..",
					Toast.LENGTH_SHORT);
			error.show();
		}

		@Override
		protected void onPostExecute(File result) {
			dismiss();
		}

	}
}
