package eu.uniek.wwy;

import java.util.List;

public class GPSDistanceCalculator {
	
	public int calculate(Locatie first, Locatie second) {	
		double R = 6371;
		double dlat = Math.toRadians(second.getLatitude() - first.getLatitude());
		double dlon = Math.toRadians(second.getLongitude() - first.getLongitude());
		double lat1 = Math.toRadians(first.getLatitude());
		double lat2 = Math.toRadians(second.getLatitude());
		double a = Math.sin(dlat/2) * Math.sin(dlat/2) +
		        Math.sin(dlon/2) * Math.sin(dlon/2) * Math.cos(lat1) * Math.cos(lat2); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = R * c;
		return ((int) d) * 100;
	}
	public boolean booleanLocatieExists2(Locatie newLocation, List<Locatie> locaties) {
		for(Locatie oldLocation : locaties) {
			if(calculate(newLocation, oldLocation) >= 10) {
				return true;
			}
		}
		return false;
	}

}
