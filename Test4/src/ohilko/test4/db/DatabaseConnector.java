package ohilko.test4.db;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.*;

public class DatabaseConnector {
	private static final String DATABASE_NAME = "db_diplom";
	public static final String[] PRODUCT_FIELDS = { "_id", "ID",
			"ID_directory", "name", "unitMeasurement", "price" };
	public static final String[] PROVIDER_FIELDS = { "_id", "ID", "name",
			"address", "phone" };
	public static final String[] REQUEST_FIELDS = { "_id", "provider_id",
			"date", "allCost" };
	public static final String[] REQUEST_PRODUCT_FIELDS = { "_id",
			"request_id", "product_id", "amount" };
	public static final String[] PRODUCT_CHILD_FIELDS = { "_id", "product_id",
			"product_child_id" };
	public static final String[] TABLE_NAME = { "Product", "Provider",
			"Request", "Product_child", "Request_product" };

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

	public void insertRow(String table_name, String[] table_fields,
			String[] data) {
		ContentValues row = new ContentValues();
		for (int i = 1; i < table_fields.length; i++) {
			row.put(table_fields[i], data[i - 1]);
		}
		try {
			open();
			database.insert(table_name, null, row);
		} finally {
			close();
		}
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
		open();
		database.delete(table_name, "_id=" + id, null);
		close();
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
			String sort_field) {
		Cursor res = database.query(table_name, table_fields, null, null, null,
				null, sort_field);
		return res;

	}

	public Cursor getRow(String table_name, String field_name, long id) {
		return database.query(table_name, null, field_name + "=" + id, null, null, null,
				null);
	}

	private static class DatabaseOpenHelper extends SQLiteOpenHelper {

		public DatabaseOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(createTable(PRODUCT_FIELDS, TABLE_NAME[0]));
			db.execSQL(createTable(PROVIDER_FIELDS, TABLE_NAME[1]));
			db.execSQL(createTable(REQUEST_FIELDS, TABLE_NAME[2]));
			db.execSQL(createTable(PRODUCT_CHILD_FIELDS, TABLE_NAME[3]));
			db.execSQL(createTable(REQUEST_PRODUCT_FIELDS, TABLE_NAME[4]));
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
