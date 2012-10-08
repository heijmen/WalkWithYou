package eu.uniek.wwy.location;

import java.util.List;

import android.location.Location;

public class GPSHandler {

	public static float distanceBetween(GPSLocation firstLocation, GPSLocation secondLocation) {
		float[] results = new float[3];
		Location.distanceBetween(
				firstLocation.getLatitude(), firstLocation.getLongitude(),secondLocation.getLatitude(),
				 secondLocation.getLongitude(),
				results);
		return results[0];
	}

	public static boolean locationExistsInRange(int range, GPSLocation newLocation, List<GPSLocation> allLocations) {
		for (GPSLocation oldLocation : allLocations) {
			if (distanceBetween(newLocation, oldLocation) <= range) {
				return true;
			}
		}
		return false;
	}

	public static int calculateHowManyBreadcrumbsAreInRange(int meter, GPSLocation locatie, List<GPSLocation> locations) {
		int iterator = 0;
		for (GPSLocation breadcrumb : locations) {
			if (distanceBetween(locatie, breadcrumb) <= meter) {
				iterator++;
			}
		}
		return iterator;
	}

}
