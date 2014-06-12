package ohilko.test4.db;

import java.io.*;

import org.xmlpull.v1.*;

import android.content.Context;
import android.database.Cursor;
import android.os.Environment;

public class ParserXmlFile {
	private File file;
	private Context context;
	private DatabaseConnector db;

	public ParserXmlFile(File f, Context c, DatabaseConnector db1) {
		file = f;
		context = c;
		db = db1;
	}

	public int fillXmlFile() {
		String request = "request";
		String product = "product";
		String amount = "amount";

		try {
			db.open();
//			Cursor requests = db.getAllRows(DatabaseConnector.TABLE_NAME[2],
//					DatabaseConnector.REQUEST_FIELDS, "date", null);
			
			Cursor requests = db.getRow(DatabaseConnector.TABLE_NAME[2],
					DatabaseConnector.REQUEST_FIELDS, new String[] { "isDownload" }, new String[] { "false" });
			
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			bw.write("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
			bw.write("<requests>");

			while (requests.moveToNext()) {
				Cursor provider = db.getRowById(
						DatabaseConnector.TABLE_NAME[1],
						Long.parseLong(requests.getString(1)));

				Cursor products = db.getRow(DatabaseConnector.TABLE_NAME[4],
						DatabaseConnector.REQUEST_PRODUCT_FIELDS,
						new String[] { "request_id" },
						new String[] { Long.toString(requests.getLong(0)) });
				bw.write("<" + request + ">");
				if (provider.moveToFirst()) {
					for (int i = 1; i < 3; i++) {
						bw.write("<" + DatabaseConnector.CLIENT_FIELDS[i]
								+ ">");
						bw.write(provider.getString(i));
						bw.write("</" + DatabaseConnector.CLIENT_FIELDS[i]
								+ ">");
					}
				}
				for (int i = 2; i < DatabaseConnector.REQUEST_FIELDS.length - 1; i++) {
					bw.write("<" + DatabaseConnector.REQUEST_FIELDS[i] + ">");
					bw.write(requests.getString(i));
					bw.write("</" + DatabaseConnector.REQUEST_FIELDS[i] + ">");
				}
				bw.write("<products>");
				while (products.moveToNext()) {
					Cursor one_product = db.getRowById(
							DatabaseConnector.TABLE_NAME[0],
							Long.parseLong(products.getString(2)));

					if (one_product.moveToFirst()) {
						bw.write("<" + product + ">");
						for (int i = 1; i < DatabaseConnector.PRODUCT_FIELDS.length - 1; i++) {
							bw.write("<" + DatabaseConnector.PRODUCT_FIELDS[i]
									+ ">");
							bw.write(one_product.getString(i));
							bw.write("</" + DatabaseConnector.PRODUCT_FIELDS[i]
									+ ">");
						}

						bw.write("<" + amount + ">");
						bw.write(products.getString(3));
						bw.write("</" + amount + ">");

						bw.write("</" + product + ">");
					}
				}
				bw.write("</products>");
				bw.write("</" + request + ">");

				/** Update request */
				String[] data = new String[4];
				for (int i = 1; i < 4; i++) {
					data[i - 1] = requests.getString(i);
				}
				data[3] = "true";
				db.updateRow(requests.getLong(0),
						DatabaseConnector.TABLE_NAME[2],
						DatabaseConnector.REQUEST_FIELDS, data);
			}
			bw.write("</requests>");
			bw.close();
			return 1;
		} catch (IOException e) {
			return 3;
		}
	}

	public void parser() {
		XmlPullParserFactory mXmlPullParserFactory;
		try {
			mXmlPullParserFactory = XmlPullParserFactory.newInstance();

			mXmlPullParserFactory.setNamespaceAware(true);
			XmlPullParser parser = mXmlPullParserFactory.newPullParser();

			FileInputStream fis = new FileInputStream(file);
			parser.setInput(new InputStreamReader(fis));

			/** Подключились к базе данных и очистили ее */
			// final DatabaseConnector db = new DatabaseConnector(context);

			for (int i = 0; i < DatabaseConnector.TABLE_NAME.length; i++) {
				db.deleteAllRows(DatabaseConnector.TABLE_NAME[i]);
			}

			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				if (parser.getEventType() == XmlPullParser.START_TAG
						&& parser.getName().equals(
								DatabaseConnector.TABLE_NAME[0])) {
					parser.next();
					parsePart(parser, db, DatabaseConnector.PRODUCT_FIELDS,
							DatabaseConnector.TABLE_NAME[0]);

				}
				if (parser.getEventType() == XmlPullParser.START_TAG
						&& parser.getName().equals(
								DatabaseConnector.TABLE_NAME[1])) {
					parser.next();
					parsePart(parser, db, DatabaseConnector.CLIENT_FIELDS,
							DatabaseConnector.TABLE_NAME[1]);

				}
				parser.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addInTableProductChild() {

		db.open();
		Cursor directories = db.getRow(DatabaseConnector.TABLE_NAME[0],
				DatabaseConnector.PRODUCT_FIELDS,
				new String[] { "isDirectory" }, new String[] { "true" });

		while (directories.moveToNext()) {
			db.open();
			Cursor products_child = db.getRow(DatabaseConnector.TABLE_NAME[0],
					DatabaseConnector.PRODUCT_FIELDS,
					new String[] { "ID_directory" },
					new String[] { directories.getString(1) });

			while (products_child.moveToNext()) {
				db.insertRow(
						DatabaseConnector.TABLE_NAME[3],
						DatabaseConnector.PRODUCT_CHILD_FIELDS,
						new String[] { directories.getString(0),
								products_child.getString(0),
								products_child.getString(6) });
			}
		}
	}

	private void parsePart(XmlPullParser parser, DatabaseConnector db,
			String[] fields, String table_name) {
		try {
			String field_name = "";
			int j = 1;
			String[] data = new String[fields.length - 1];

			for (int i = 0; i < data.length; i++) {
				data[i] = "";
			}

			if (table_name.equals("Product")) {
				data[data.length - 1] = "false";
			}

			while (true) {
				if (parser.getName() != null
						&& parser.getName().equals(table_name)) {
					if (table_name.equals("Product") && j == 4) {
						data[data.length - 1] = "true";
					}
					break;
				}

				if (j == fields.length) {
					break;
				}

				if (parser.getEventType() == XmlPullParser.START_TAG) {
					field_name = parser.getName();
				}

				if (parser.getEventType() == XmlPullParser.TEXT
						&& field_name.equals(fields[j])) {
					data[j - 1] = parser.getText();
					j++;
				}

				parser.next();
			}
			db.insertRow(table_name, fields, data);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
