package ohilko.test4;

import java.io.*;

import ohilko.test4.db.DatabaseConnector;

import org.xmlpull.v1.*;

import android.content.Context;

public class ParserXmlFile {
	private File file;
	private Context context;
	private DatabaseConnector db;

	public ParserXmlFile(File f, Context c, DatabaseConnector db1) {
		file = f;
		context = c;
		db = db1;
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
					parsePart(parser, db, DatabaseConnector.PROVIDER_FIELDS,
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

	private void parsePart(XmlPullParser parser, DatabaseConnector db,
			String[] fields, String table_name) {
		try {
			String field_name = "";
			int j = 1;
			String[] data = new String[fields.length - 1];

			for (int i = 0; i < data.length; i++) {
				data[i] = "";
			}

			while (true) {
				if (parser.getName() != null
						&& parser.getName().equals(table_name)) {
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
