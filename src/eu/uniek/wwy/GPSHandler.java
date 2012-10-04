package eu.uniek.wwy;

import java.util.List;

/**
 * There is no need instantiating this class so all methods are static which gives 0.3 nano seconds performance winst per method invocation
 */
public final class GPSHandler {
	
	private GPSHandler(){}
	
	public static int calculateDistanceBetweenCoordinatesInMethers(GPSLocation firstLocation, GPSLocation secondLocation) {	
		double radius = 6371;
		double latencyDifference = Math.toRadians(secondLocation.getLatitude() - firstLocation.getLatitude());
		double longitudeDifference = Math.toRadians(secondLocation.getLongitude() - firstLocation.getLongitude());
		double firstLatitudeInRadians = Math.toRadians(firstLocation.getLatitude());
		double secondLatitudeInRadians = Math.toRadians(secondLocation.getLatitude());
		double weetnietwaaravoorstaat = Math.sin(latencyDifference/2) * Math.sin(latencyDifference/2) +
		        Math.sin(longitudeDifference/2) * Math.sin(longitudeDifference/2) * Math.cos(firstLatitudeInRadians) * Math.cos(secondLatitudeInRadians); 
		double ofwaarcvoorstaat = 2 * Math.atan2(Math.sqrt(weetnietwaaravoorstaat), Math.sqrt(1-weetnietwaaravoorstaat)); 
		double distanceInMethers = radius * ofwaarcvoorstaat;
		distanceInMethers *= 1000;
		return (int) distanceInMethers;
	}
	public static boolean locationExistsInRange(int range, GPSLocation newLocation, List<GPSLocation> locaties) {
		for(GPSLocation oldLocation : locaties) {
			if(calculateDistanceBetweenCoordinatesInMethers(newLocation, oldLocation) <= range) {
				return true;
			}
		}
		return false;
	}
	public static int calculateHowManyBreadcrumbsAreInRange(int mether, GPSLocation locatie, List<GPSLocation> locations) {
		int i = 0;
		for(GPSLocation breadcum : locations) {
			if(calculateDistanceBetweenCoordinatesInMethers(locatie, breadcum) <= mether) {
				i++;
			}
		}
		return i;
	}

}
