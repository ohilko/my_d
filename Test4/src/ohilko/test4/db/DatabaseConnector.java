package ohilko.test4.db;

import android.content.*;
import android.database.*;
import android.database.sqlite.*;
import android.database.sqlite.SQLiteDatabase.*;

public class DatabaseConnector {
	private static final String DATABASE_NAME = "db_diplom";
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

	public void insertClient(String name, String address, String phone) {
		ContentValues newClient = new ContentValues();
		newClient.put(DatabaseOpenHelper.KEY_NAME, name);
		newClient.put(DatabaseOpenHelper.KEY_ADDRESS, address);
		newClient.put(DatabaseOpenHelper.KEY_PHONE, phone);
		try {
			open();
			database.insert(DatabaseOpenHelper.TABLE_NAME, null, newClient);
		} finally {
			close();
		}
	}

	public void updateClient(long id, String name, String address, String phone) {
		ContentValues editClient = new ContentValues();
		editClient.put(DatabaseOpenHelper.KEY_NAME, name);
		editClient.put(DatabaseOpenHelper.KEY_ADDRESS, address);
		editClient.put(DatabaseOpenHelper.KEY_PHONE, phone);
		try {
			open();
			database.update(DatabaseOpenHelper.TABLE_NAME, editClient, "_id="
					+ id, null);
		} finally {
			close();
		}
	}

	public Cursor getAllClients() {
		return database.query(DatabaseOpenHelper.TABLE_NAME, new String[] {
				"_id", DatabaseOpenHelper.KEY_NAME,
				DatabaseOpenHelper.KEY_ADDRESS, DatabaseOpenHelper.KEY_PHONE },
				null, null, null, null, "name");
	}

	public Cursor getAllClients(String str) {
		return database.query(DatabaseOpenHelper.TABLE_NAME, new String[] {
				"_id", DatabaseOpenHelper.KEY_NAME,
				DatabaseOpenHelper.KEY_ADDRESS, DatabaseOpenHelper.KEY_PHONE },
				"name LIKE '%" + str + "%'", null, null, null, null);

	}

	public Cursor getOneClient(long id) {
		return database.query(DatabaseOpenHelper.TABLE_NAME, null, "_id=" + id,
				null, null, null, null);
	}

	public void deleteClient(long id) {
		open();
		database.delete(DatabaseOpenHelper.TABLE_NAME, "_id=" + id, null);
		close();
	}

	public void deleteAllRows() {
		try {
			open();
			database.delete(DatabaseOpenHelper.TABLE_NAME, null, null);
		} finally {
			close();
		}
	}

	private class DatabaseOpenHelper extends SQLiteOpenHelper {
		private static final String TABLE_NAME = "Client";
		private static final String KEY_NAME = "name";
		private static final String KEY_ADDRESS = "address";
		private static final String KEY_PHONE = "phone";

		public DatabaseOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			String createQuery = "CREATE TABLE " + TABLE_NAME
					+ "(_id integer primary key autoincrement, " + KEY_NAME
					+ " TEXT, " + KEY_ADDRESS + " TEXT, " + KEY_PHONE
					+ " TEXT);";

			db.execSQL(createQuery);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		}
	}
}
