package eu.uniek.wwy;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class GPSLocation implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private double latitude, longitude;
	private Calendar calendar;

	public GPSLocation(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
		calendar = Calendar.getInstance();
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}
	//for debug purposes
	public String getTimeCreated() {
		return new SimpleDateFormat("dd-MM-yyyy:hh:mm:ss").format(calendar.getTime());
	}


}
