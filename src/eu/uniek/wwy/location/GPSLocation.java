package eu.uniek.wwy.location;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GPSLocation  {
	private double latitude, longitude;
	private Calendar calendar;

	public GPSLocation(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		calendar = Calendar.getInstance();
	}
	public GPSLocation(double latitude, double longitude, Calendar calendar) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		this.calendar = calendar;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	public String getTimeCreated() {
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy:hh:mm:ss");
		String formattedDate = dateFormat.format(calendar.getTime());
		return formattedDate;
	}
}