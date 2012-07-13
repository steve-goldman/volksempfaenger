package net.x4a42.volksempfaenger.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import net.x4a42.volksempfaenger.Log;

public class DatabaseHelper extends SQLiteOpenHelper {


	private static final String DB_NAME = "podcast.db";
	private static final int DB_VERSION = 3;

	public static final String TABLE_PODCAST = "podcast";
	public static final String TABLE_EPISODE = "episode";
	public static final String TABLE_ENCLOSURE = "enclosure";

	Context context;

	private DatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		this.context = context;
	}

	private static DatabaseHelper instance;

	protected static DatabaseHelper getInstance(Context context) {
		if (instance == null) {
			makeInstance(context);
		}
		return instance;
	}

	private static synchronized void makeInstance(Context context) {
		if (instance == null) {
			instance = new DatabaseHelper(context);
		}
	}

	@Override
	public void onOpen(SQLiteDatabase db) {
		super.onOpen(db);
		if (!db.isReadOnly()) {
			db.execSQL("PRAGMA foreign_keys=ON;");
		}
		try {
			executeSqlFromAsset(db, "sql/open.sql");
		} catch (IOException e) {
			Log.wtf(this, "Error while execute post-open SQL", e);
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		Log.v(this, "creating new database");
		db.beginTransaction();
		try {
			executeSqlFromAsset(db, "sql/init.sql");
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.wtf(this, e);
		} finally {
			db.endTransaction();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.beginTransaction();
		try {
			for (int i = oldVersion; i < newVersion; i++) {
				Log.v(this, "upgrading database from version " + i
						+ " to version " + (i + 1));
				executeSqlFromAsset(db, "sql/upgrade-" + i + ".sql");
			}
			db.setTransactionSuccessful();
		} catch (Exception e) {
			Log.wtf(this, e);
		} finally {
			db.endTransaction();
		}
	}

	private void executeSqlFromAsset(SQLiteDatabase db, String file)
			throws IOException {
		InputStream in = context.getAssets().open(file,
				AssetManager.ACCESS_BUFFER);
		Writer wr = new StringWriter();
		int c;
		while ((c = in.read()) != -1) {
			if (c == ';') {
				String sql = wr.toString();
				Log.v(this, "Executing the following SQL:");
				Log.v(this, sql);
				db.execSQL(sql);
				wr = new StringWriter();
			} else {
				wr.write(c);
			}
		}

	}
}
