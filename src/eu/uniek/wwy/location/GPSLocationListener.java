package eu.uniek.wwy.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class GPSLocationListener implements LocationListener {  
	private GPSLocation currentLocation;

	public void onLocationChanged(Location loc) {  
		setCurrentLocation(new GPSLocation(loc.getLatitude(), loc.getLongitude()));
	}
	public void onProviderDisabled(String provider) {}
	public void onProviderEnabled(String provider) {}
	public void onStatusChanged(String provider, int status, Bundle extras) {}

	public GPSLocation getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(GPSLocation currentLocation) {
		this.currentLocation = currentLocation;
	}
}
