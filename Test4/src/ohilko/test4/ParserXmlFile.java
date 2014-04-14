package ohilko.test4;

import java.io.*;

import ohilko.test4.db.DatabaseConnector;

import org.xmlpull.v1.*;

import android.content.Context;

public class ParserXmlFile {
	private File file;
	private Context context;

	public ParserXmlFile(File f, Context c) {
		file = f;
		context = c;
	}

	public void parser() {
		XmlPullParserFactory mXmlPullParserFactory;
		try {
			mXmlPullParserFactory = XmlPullParserFactory.newInstance();

			mXmlPullParserFactory.setNamespaceAware(true);
			XmlPullParser parser = mXmlPullParserFactory.newPullParser();
			
			FileInputStream fis = new FileInputStream(file);
			parser.setInput(new InputStreamReader(fis));
			
			/**Подключились к базе данных и очистили ее*/
			final DatabaseConnector db = new DatabaseConnector(context);
			
			db.open();
//			for (int i = 0; i < DatabaseConnector.TABLE_NAME.length; i++) {
//				db.deleteAllRows(DatabaseConnector.TABLE_NAME[i]);
//			}

			while (parser.getEventType() != XmlPullParser.END_DOCUMENT) {
				if (parser.getEventType() == XmlPullParser.START_TAG
						&& parser.getName().equals(DatabaseConnector.TABLE_NAME[0])) {
					parser.next();
					parsePart(parser, db, DatabaseConnector.PROVIDER_FIELDS, DatabaseConnector.TABLE_NAME[1]);

				}
				if (parser.getEventType() == XmlPullParser.START_TAG
						&& parser.getName().equals(DatabaseConnector.TABLE_NAME[1])) {
					parser.next();
					parsePart(parser, db, DatabaseConnector.PRODUCT_FIELDS, DatabaseConnector.TABLE_NAME[1]);

				}
				parser.next();
			}
			db.close();
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void parsePart(XmlPullParser parser, DatabaseConnector db, String[] fields, String table_name) {
		try {
			String[] data = new String[fields.length-1];
			if (parser.getEventType() == XmlPullParser.START_TAG) {
				for (int i = 1; i < fields.length; i++) {
					if (parser.getEventType() == XmlPullParser.START_TAG && parser.getName().equals(fields[i])) {
						data[i-1] = parser.getText();
					} else {
						data[i-1] = "";
					}
					db.insertRow(table_name, fields, data);	
					parser.next();
					parser.next();
				}

			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}
