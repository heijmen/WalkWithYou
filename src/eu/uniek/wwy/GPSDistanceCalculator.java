package eu.uniek.wwy;

import java.util.List;

public class GPSDistanceCalculator {
	
	public int calculateDistanceBetweenCoordinatesInMethers(GPSLocation firstLocation, GPSLocation secondLocation) {	
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
	public boolean locatieExists(GPSLocation newLocation, List<GPSLocation> locaties) {
		for(GPSLocation oldLocation : locaties) {
			if(calculateDistanceBetweenCoordinatesInMethers(newLocation, oldLocation) <= 10) {
				return true;
			}
		}
		return false;
	}

}
