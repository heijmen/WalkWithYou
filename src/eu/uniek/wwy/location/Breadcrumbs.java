package eu.uniek.wwy.location;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.location.LocationManager;
import android.util.Log;
import eu.uniek.wwy.FileMapper;
import eu.uniek.wwy.gui.WalkWithYou;

public class Breadcrumbs {
	private final GPSLocation cafetariaChaoLocation = new GPSLocation(52.10164, 5.10838);
	private LocationManager locationManager = null;
	private GPSLocation lastDroppedLocation;
	private List<GPSLocation> breadcrumbs = new ArrayList<GPSLocation>();
	private FileMapper fileMapper = new FileMapper();
	private WalkWithYou context;
	private final String breadcrumbsFileName = "broodkruimels";
	private GPSLocationListener gpsLocationListener = null;
	private List<GPSLocation> locations;
	
	public Breadcrumbs(WalkWithYou walkWithYou) {
		context = walkWithYou;
	}

	public void setLocations() {
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		gpsLocationListener = new GPSLocationListener();
		context.setGPSLocationListener(gpsLocationListener);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, WalkWithYou.updateTime, 1, gpsLocationListener);
		locations = new ArrayList<GPSLocation>();
		locations.add(cafetariaChaoLocation);
	}
	
	public void readBreadcrumbsFromFile() {
		try {
			breadcrumbs = fileMapper.getAllBreadcrumbsFromFile(context.openFileInput(breadcrumbsFileName));
		} catch (Exception e) {
			Log.e("Broodkruimelbestand is corrupt", e.getMessage());
			context.showToast("Broodkruimelbestand is corrupt \n" + e.getMessage());
			resetBreadcrumbs();
		}
	}
	public String getBreadcrumbsInfo() {
		StringBuilder breadcrumbDebugText = new StringBuilder();
		for (GPSLocation gpsLocation : breadcrumbs) {
			breadcrumbDebugText.append(context.getBreadCrumbInfo(gpsLocation));
		}
		return breadcrumbDebugText.toString();
	}
	private void resetBreadcrumbs() {
		breadcrumbs = new ArrayList<GPSLocation>();
	}

	public void update() {
		if (breadcrumbs == null) {
			resetBreadcrumbs();
		}
		addBreadcrumb();
		saveBreadcrumb();
	}
	private void saveBreadcrumb() {
		try {
			fileMapper.saveBreadCrumbsToFile(breadcrumbs, context.openFileOutput(breadcrumbsFileName, WalkWithYou.MODE_PRIVATE));
		} catch (Exception e) {
			context.showToast(e.getMessage());
		}
	}

	public void resetBreadCrumbsInFile() throws FileNotFoundException, IOException {
		fileMapper.resetBreadCrumbsInFile(context.openFileOutput(breadcrumbsFileName, WalkWithYou.MODE_PRIVATE));
	}

	public List<GPSLocation> getBroodKruimels() {
		return breadcrumbs;
	}

	public void setBroodKruimels(List<GPSLocation> broodKruimels) {
		this.breadcrumbs = broodKruimels;
	}

	private void addBreadcrumb() {
		GPSLocation currentLocation = gpsLocationListener.getCurrentLocation();
		if (lastDroppedLocation == null || GPSHandler.distanceBetween(lastDroppedLocation, currentLocation) > 50) {
			lastDroppedLocation = currentLocation;
			breadcrumbs.add(lastDroppedLocation);
		}
	}

	public LocationManager getLocationManager() {
		return locationManager;
	}

	public void setLocationManager(LocationManager locationManager) {
		this.locationManager = locationManager;
	}

	public GPSLocation getLastDroppedLocation() {
		return lastDroppedLocation;
	}

	public void setLastDroppedLocation(GPSLocation lastDroppedLocation) {
		this.lastDroppedLocation = lastDroppedLocation;
	}

	public List<GPSLocation> getAllLocations() {
		return locations;
	}

	public void addLocation(GPSLocation currentLocation) {
		locations.add(currentLocation);
	}

}
