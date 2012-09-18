package eu.uniek.wwy;

import java.util.List;

public class GPSDistanceCalculator {
	
	public int calculateDistanceBetweenCoordinatesInMethers(Locatie firstLocation, Locatie secondLocation) {	
		double radius = 6371;
		double latencyDifference = Math.toRadians(secondLocation.getLatitude() - firstLocation.getLatitude());
		double longitudeDifference = Math.toRadians(secondLocation.getLongitude() - firstLocation.getLongitude());
		double firstLatitudeInRadians = Math.toRadians(firstLocation.getLatitude());
		double secondLatitudeInRadians = Math.toRadians(secondLocation.getLatitude());
		double weetnietwaaravoorstaat = Math.sin(latencyDifference/2) * Math.sin(latencyDifference/2) +
		        Math.sin(longitudeDifference/2) * Math.sin(longitudeDifference/2) * Math.cos(firstLatitudeInRadians) * Math.cos(secondLatitudeInRadians); 
		double ofwaarcvoorstaat = 2 * Math.atan2(Math.sqrt(weetnietwaaravoorstaat), Math.sqrt(1-weetnietwaaravoorstaat)); 
		double distanceInMethers = radius * ofwaarcvoorstaat;
		return ((int) distanceInMethers) * 100;
	}
	public boolean locatieExists(Locatie newLocation, List<Locatie> locaties) {
		for(Locatie oldLocation : locaties) {
			if(calculateDistanceBetweenCoordinatesInMethers(newLocation, oldLocation) >= 10) {
				return true;
			}
		}
		return false;
	}

}
