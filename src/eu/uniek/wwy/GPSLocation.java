package eu.uniek.wwy;

public class GPSLocation {
	private double latitude, longitude;

	public GPSLocation(double latitude, double longitude) {
		super();
		this.latitude = latitude;
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

}
