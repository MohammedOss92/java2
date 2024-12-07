package com.sarrawi.myapplication;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DB {

	// database definition
	private static final String DATABASE_NAME = "translator.db";
	private static final String DATABASE_TABLE = "result";
	private static final int VERSION = 4;

	// define column names
	public static final String ID = "`id`";
	public static final String FROM = "`from`";
	public static final String TO = "`to`";
	public static final String FROM_TEXT = "`from_text`";
	public static final String TO_TEXT = "`to_text`";
	public static final String FROM_CODE = "`from_code`";
	public static final String TO_CODE = "`to_code`";

	private static SQLiteDatabase db;
	private DBOpenHelper helper;

	public DB(Context context) {
		this.helper = new DBOpenHelper(context, DATABASE_NAME, null, VERSION);
	}

	public void open() throws SQLiteException {
		try {
			DB.db = this.helper.getWritableDatabase();
		} catch (SQLiteException e) {
			DB.db = this.helper.getReadableDatabase();
		}
	}

	public void close() {
		DB.db.close();
	}

	
	public void clear() {
		db.execSQL("delete from "+DATABASE_TABLE+";");
	}

	public void insertResult(int id, String from, String to, String from_text,
			String to_text, String from_code, String to_code) {
		ContentValues newRow = new ContentValues();
		newRow.put(ID, id);
		newRow.put(FROM, from);
		newRow.put(TO, to);
		newRow.put(FROM_TEXT, from_text);
		newRow.put(TO_TEXT, to_text);
		newRow.put(FROM_CODE, from_code);
		newRow.put(TO_CODE, to_code);
		DB.db.insert(DATABASE_TABLE, null, newRow);
	}

	public void deleteResult(int id) {
		db.execSQL("delete from "+DATABASE_TABLE+" where id=" + id);
	}

	public ArrayList<ResultRow> getResults () {
		ArrayList<ResultRow> results = new ArrayList<>();
		Cursor result = null;

		try {
			result = DB.db.query(true, DATABASE_TABLE, new String[] {
							ID, FROM, TO, FROM_TEXT, TO_TEXT, FROM_CODE, TO_CODE},
					null, null, null, null, null, null);

			if (result != null && result.moveToFirst()) {
				do {
					// التحقق من فهرس الأعمدة
					int idIndex = result.getColumnIndex(ID);
					int fromIndex = result.getColumnIndex(FROM);
					int toIndex = result.getColumnIndex(TO);
					int fromTextIndex = result.getColumnIndex(FROM_TEXT);
					int toTextIndex = result.getColumnIndex(TO_TEXT);
					int fromCodeIndex = result.getColumnIndex(FROM_CODE);
					int toCodeIndex = result.getColumnIndex(TO_CODE);

					// التأكد من أن الأعمدة موجودة (فهرس العمود لا يكون -1)
					if (idIndex != -1 && fromIndex != -1 && toIndex != -1 &&
							fromTextIndex != -1 && toTextIndex != -1 &&
							fromCodeIndex != -1 && toCodeIndex != -1) {

						ResultRow res = new ResultRow(
								result.getInt(idIndex),
								result.getString(fromIndex),
								result.getString(toIndex),
								result.getString(fromTextIndex),
								result.getString(toTextIndex),
								result.getString(fromCodeIndex),
								result.getString(toCodeIndex)
						);
						results.add(res);
					}
				} while (result.moveToNext());
			}
		} catch (Exception e) {
			Log.e("DB Error", "Error retrieving data: " + e.toString());
		} finally {
			if (result != null) {
				result.close();
			}
		}
		return results;
	}


	private static class DBOpenHelper extends SQLiteOpenHelper {
		public DBOpenHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
		}

		// SQL Statement to create a new database.
		private static final String DATABASE_CREATE = "create table "
				+ DATABASE_TABLE + " (" + ID + " INT, " + FROM + " TEXT, " + TO
				+ " TEXT," + FROM_TEXT + " TEXT, " + TO_TEXT + " TEXT, "
				+ FROM_CODE + " TEXT, " + TO_CODE + " TEXT);";

		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(DATABASE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion,
				int _newVersion) {
			// Drop the old table.
			_db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
			// Create a new one.
			onCreate(_db);
		}
	}

}
