package ohilko.test4;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ohilko.test4.R;
import ohilko.test4.db.DatabaseConnector;
import ohilko.test4.db.ParserXmlFile;

import android.os.Bundle;
import android.os.Environment;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.*;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class SDActivity extends Activity {

	private EditText path;
	private Button browse;
	private Button open;
	private DatabaseConnector db;
	private boolean isDownload = true;

	OnClickListener browseClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {
			openDialog();
		}
	};

	OnClickListener openClicked = new OnClickListener() {

		@Override
		public void onClick(View v) {

			if (path.getText().toString().equals("")) {
				AlertDialog.Builder builder = new AlertDialog.Builder(
						SDActivity.this);

				builder.setTitle(R.string.errorTitleEmptyField);
				builder.setMessage(R.string.errorMessageEmptyField);
				builder.setPositiveButton(R.string.errorButton, null);
				builder.show();
			} else {
				if (isDownload) {
					String[] splitPath = path.getText().toString().split("\\.");
					if (splitPath[1].equals("xml")) {
						File f = new File(path.getText().toString());
						if (f.exists()) {
							ParserXmlFile parser = new ParserXmlFile(f,
									SDActivity.this, db);
							parser.parser();
							parser.addInTableProductChild();

							startNewActivity(ListRequestActivity.class);

						} else {
							AlertDialog.Builder builder = new AlertDialog.Builder(
									SDActivity.this);

							builder.setTitle(R.string.errorTitleFileNotExist);
							builder.setMessage(R.string.errorMessageFileNotExist);
							builder.setPositiveButton(R.string.errorButton,
									null);
							builder.show();
						}
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								SDActivity.this);

						builder.setTitle(R.string.errorTitleIsNotFile);
						builder.setMessage(R.string.errorMessageIsNotFile);
						builder.setPositiveButton(R.string.errorButton, null);
						builder.show();
					}
				} else {
					File f_dir = new File(path.getText().toString());
					if (f_dir.isDirectory()) {
						File f = new File(f_dir.getPath() + "/requests.xml");
						ParserXmlFile parser = new ParserXmlFile(f,
								SDActivity.this, db);
						parser.fillXmlFile();
						
						startNewActivity(ListRequestActivity.class);
					} else {
						AlertDialog.Builder builder = new AlertDialog.Builder(
								SDActivity.this);

						builder.setTitle("Не директория");
						builder.setMessage("Вы должны указать папку, в которую хотите выгрузить данные");
						builder.setPositiveButton(R.string.errorButton, null);
						builder.show();
					}
				}
			}
		}
	};

	private void openDialog() {
		String[] fileFor = {};
		OpenFileDialog ofd = new OpenFileDialog(this, Environment
				.getExternalStorageDirectory().toString(), fileFor);
		ofd.show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sd);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		path = (EditText) findViewById(R.id.editText_path);
		browse = (Button) findViewById(R.id.button_browse);
		open = (Button) findViewById(R.id.button_open_sd_file);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			isDownload = extras.getBoolean(MainActivity.ISDOWNLOAD);
		}
		if (!isDownload) {
			open.setText("Выгрузить");
		}
		db = new DatabaseConnector(SDActivity.this);

		browse.setOnClickListener(browseClicked);
		open.setOnClickListener(openClicked);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.sd, menu);

		SubMenu sm = menu.addSubMenu(Menu.FIRST, 1, 1, "SubMenu").setIcon(
				android.R.drawable.ic_dialog_dialer);
		menu.findItem(1).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
		sm.add(Menu.FIRST, 100, 100, "О программе");
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
			startNewActivity(AboutActivity.class);
			break;
		}
		default:
			return false;
		}

		return true;
	}

	private void startNewActivity(Class l) {
		Intent intent = new Intent(this, l);
		startActivity(intent);
	}

	private class OpenFileDialog extends Dialog implements OnClickListener {
		private static final String FILE_KEY = "filename";
		private static final String IMAGE_KEY = "fileimage";

		private File currentDir = new File("/");
		private ListView view = null;
		private FilenameFilter filter = null;

		public OpenFileDialog(Context context, String dir, String[] fileExt) {
			super(context);
			init(dir, fileExt);
		}

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.of_choose: {
				path.setText(currentDir.getPath());
				dismiss();
			}
				break;
			case R.id.ofd_go_up: {
				browseUp();
			}
				break;

			default:
				break;
			}

		}

		private void init(String dir, String[] fileExt) {

			if (dir != null && new File(dir).exists())
				currentDir = new File(dir);

			prepareFileFilter(fileExt);

			if (!isDownload) {
				setContentView(R.layout.ofd_layout_unload);
				Button choose = (Button) findViewById(R.id.of_choose);
				choose.setOnClickListener(this);
			} else {
				setContentView(R.layout.ofd_layout);
			}
			setTitle(R.string.ofd_title);

			view = (ListView) findViewById(R.id.ofd_list);
			browseTo(currentDir);

			view.setOnItemClickListener(new OnItemClickListener() {
				@SuppressWarnings("unchecked")
				public void onItemClick(AdapterView<?> a, View v, int position,
						long id) {
					Map<String, String> listitem = (Map<String, String>) a
							.getItemAtPosition(position);
					String text = listitem.get(FILE_KEY);
					File file = new File(currentDir.getAbsolutePath()
							+ File.separator + text);
					if (!browseTo(file) && isDownload) {
						path.setText(file.getPath());
						dismiss();
					}
				}
			});

			Button upButton = (Button) findViewById(R.id.ofd_go_up);
			upButton.setOnClickListener(this);
		}

		private void prepareFileFilter(final String[] ext) {
			if (ext == null)
				return;

			filter = new FilenameFilter() {
				public boolean accept(File dir, String filename) {
					if (new File(dir + File.separator + filename).isDirectory())
						return true;
					for (String e : ext) {
						if (filename.endsWith(e))
							return true;
					}
					return false;
				}
			};
		}

		private boolean browseTo(File dir) {
			if (!dir.isDirectory()) {
				return false;
			} else {
				fillListView(dir);
				currentDir = dir;
				TextView pathView = (TextView) findViewById(R.id.ofd_current_path);
				pathView.setText(currentDir.getAbsolutePath());
				return true;
			}
		}

		private void browseUp() {
			if (currentDir.getParentFile() != null)
				browseTo(currentDir.getParentFile());
		}

		private void fillListView(File dir) {
			List<Map<String, ?>> list = new ArrayList<Map<String, ?>>();
			String[] files = filter != null ? dir.list(filter) : dir.list();
			for (String file : files) {
				Map<String, Object> item = new HashMap<String, Object>();
				item.put(FILE_KEY, file);
				int imageid = new File(dir.getAbsolutePath() + File.separator
						+ file).isDirectory() ? R.drawable.ic_osdialogs_dir
						: R.drawable.ic_osdialogs_file;
				item.put(IMAGE_KEY, imageid);
				list.add(item);
			}

			SimpleAdapter adapter = new SimpleAdapter(getContext(), list,
					R.layout.ofd_list_item,
					new String[] { FILE_KEY, IMAGE_KEY }, new int[] {
							R.id.ofd_item_text, R.id.ofd_item_image });
			view.setAdapter(adapter);
		}
	}
}
