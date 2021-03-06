package ohilko.test4.db;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.*;

public class DatabaseConnector {
	private static final String DATABASE_NAME = "db_diplom";
	public static final String[] PRODUCT_FIELDS = { "_id", "ID",
			"ID_directory", "name", "unitMeasurement", "price", "isDirectory" };
	public static final String[] CLIENT_FIELDS = { "_id", "ID", "name",
			"address", "phone" };
	public static final String[] REQUEST_FIELDS = { "_id", "provider_id",
			"date", "allCost", "isUnloaded" };
	public static final String[] REQUEST_PRODUCT_FIELDS = { "_id",
			"request_id", "product_id", "amount" };
	public static final String[] PRODUCT_CHILD_FIELDS = { "_id", "product_id",
			"product_child_id", "isChildDirectory" };
	public static final String[] TABLE_NAME = { "Product", "Client",
			"Request", "Product_child", "Request_product", "Mail_Data", "SD_Data"};
	public static final String[] MAIL_DATA_FIELDS = {"_id", "login" , "password", "smtp", "pop3", "subject"};
	
	public static final String[] SD_DATA_FIELDS = {"_id", "path"};

	private SQLiteDatabase database;
	private DatabaseOpenHelper databaseOpenHelper;

	public DatabaseConnector(Context context) {
		databaseOpenHelper = new DatabaseOpenHelper(context, DATABASE_NAME,
				null, 1);
	}

	public void open() throws SQLException {
		database = databaseOpenHelper.getWritableDatabase();
	}

	public void close() {
		if (database != null)
			database.close();
	}

	public long insertRow(String table_name, String[] table_fields,
			String[] data) {
		long id = -1;
		ContentValues row = new ContentValues();
		for (int i = 1; i < table_fields.length; i++) {
			row.put(table_fields[i], data[i - 1]);
		}
		try {
			open();
			id = database.insert(table_name, null, row);
		} finally {
			close();
		}
		return id;
	}

	public void updateRow(long id, String table_name, String[] table_fields,
			String[] data) {
		ContentValues row = new ContentValues();
		for (int i = 1; i < table_fields.length; i++) {
			row.put(table_fields[i], data[i - 1]);
		}
		try {
			open();
			database.update(table_name, row, "_id=" + id, null);
		} finally {
			close();
		}
	}

	public void deleteRow(String table_name, long id) {
		try {
			open();
			database.delete(table_name, "_id=" + id, null);
		} finally {
			close();
		}
	}

	public void deleteAllRows(String table_name) {
		try {
			open();
			database.delete(table_name, null, null);
		} finally {
			close();
		}
	}

	public Cursor getAllRows(String table_name, String[] table_fields,
			String sort_field, String selection) {
		Cursor res = null;

		res = database.query(table_name, table_fields, null, null, null, null,
				sort_field);

		return res;

	}

	public Cursor getRowById(String table_name, long id) {
		Cursor res = null;

		res = database.query(table_name, null, "_id=" + id, null, null, null,
				null);

		return res;
	}

	public Cursor getRow(String table_name, String[] table_fields,
			String[] field_name_for_select, String[] data) {
		Cursor res = null;
		String select = "";
		for (int i = 0; i < field_name_for_select.length - 1; i++) {
			select += field_name_for_select[i] + "='" + data[i] + "' and ";

		}
		select += field_name_for_select[field_name_for_select.length - 1]
				+ "='" + data[field_name_for_select.length - 1] + "'";

		res = database.query(table_name, table_fields, select, null, null,
				null, null);

		return res;
	}

	private static class DatabaseOpenHelper extends SQLiteOpenHelper {

		public DatabaseOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(createTable(PRODUCT_FIELDS, TABLE_NAME[0]));
			db.execSQL(createTable(CLIENT_FIELDS, TABLE_NAME[1]));
			db.execSQL(createTable(REQUEST_FIELDS, TABLE_NAME[2]));
			db.execSQL(createTable(PRODUCT_CHILD_FIELDS, TABLE_NAME[3]));
			db.execSQL(createTable(REQUEST_PRODUCT_FIELDS, TABLE_NAME[4]));
			db.execSQL(createTable(MAIL_DATA_FIELDS, TABLE_NAME[5]));
			db.execSQL(createTable(SD_DATA_FIELDS, TABLE_NAME[6]));
		}

		private String createTable(String[] table, String table_name) {
			String createQuery = "";
			String text = " TEXT";
			String end = "); ";

			createQuery += "CREATE TABLE " + table_name
					+ " (_id integer primary key autoincrement, ";
			for (int j = 1; j < table.length; j++) {
				if (j == table.length - 1) {
					createQuery += table[j] + text;
				} else {
					createQuery += table[j] + text + ", ";
				}
			}
			createQuery += end;

			return createQuery;
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
