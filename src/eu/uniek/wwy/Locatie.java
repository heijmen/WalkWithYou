package eu.uniek.wwy;

public class Locatie {
	private double latitude, longitude;

	public Locatie(double latitude, double longitude) {
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
