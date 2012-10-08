package eu.uniek.wwy.database;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import eu.uniek.wwy.location.GPSLocation;

public class DatabaseHandler extends SQLiteOpenHelper {
	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "gyroscopeDatabase";
	private static final String TABLE_LOCATIONS = "gpslocation";
	private static final String KEY_ID = "id";
	private static final String KEY_LONGTITUDE = "longtitude";
	private static final String KEY_LATITUDE = "latitude";
	private static final String KEY_DATE = "date";

	public DatabaseHandler(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_LOCATIONS + "("
				+ KEY_ID + " INTEGER PRIMARY KEY," + KEY_LONGTITUDE + " REAL,"
				+ KEY_LATITUDE + " REAL," + KEY_DATE + " TEXT" + ")";
		database.execSQL(CREATE_CONTACTS_TABLE);
	}

	public void addGPSLocation(GPSLocation gpsLocation) {
		SQLiteDatabase database = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(KEY_LONGTITUDE, gpsLocation.getLongitude()); 
		values.put(KEY_LATITUDE, gpsLocation.getLatitude()); 
		values.put(KEY_DATE, gpsLocation.getTimeCreated()); 
		database.insert(TABLE_LOCATIONS, null, values);
		database.close();
	}
	
	public List<GPSLocation> getAllGPSLocation() {
		List<GPSLocation> locationList = new ArrayList<GPSLocation>();
		String selectQuery = "SELECT  * FROM " + TABLE_LOCATIONS;

		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.rawQuery(selectQuery, null);
		if (cursor.moveToFirst()) {
			do {
				String[] date = cursor.getString(3).split("[:-]");
				GregorianCalendar calendar = new GregorianCalendar();
				calendar.set(
						Integer.parseInt(date[2]), 
						Integer.parseInt(date[1])-1, 
						Integer.parseInt(date[0]),
						Integer.parseInt(date[3]),
						Integer.parseInt(date[4]),
						Integer.parseInt(date[5]));
				GPSLocation gpsLocation = new GPSLocation(
						cursor.getDouble(2),
						cursor.getDouble(1), 
						calendar);
				locationList.add(gpsLocation);
			} while (cursor.moveToNext());
		}

		return locationList;
	}
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + TABLE_LOCATIONS);
		onCreate(database);
	}
}