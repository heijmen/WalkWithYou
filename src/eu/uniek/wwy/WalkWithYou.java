package eu.uniek.wwy;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

public class WalkWithYou extends Activity  {
	private LocationManager locationManager = null;  
	private LocationListener locationListener = null;
	private List<GPSLocation> locations;
	private GPSLocation huidigeLocatie;
	private boolean flag = false;
	private Handler updatethingy;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_walk_with_you);
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		flag = displayGpsStatus();  
		if (flag) {  			
			//waiting  
			locationListener = new MyLocationListener();  
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10,locationListener);  
		} else {  
			//mislukt  
		}  
		locations = new ArrayList<GPSLocation>();
	//	locations.add(new GPSLocation(52.011790,4.723240));
		FrameLayout framelayout = (FrameLayout) findViewById(R.id.frameLayout1);
		GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {0xFF131313, 0xFF392399});
		gd.setCornerRadius(0f);
		gd.setShape(GradientDrawable.OVAL);
		gd.setSize(480,480);
		framelayout.setBackgroundDrawable(gd);

		final Context context = this;
		framelayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(huidigeLocatie != null) {
					locations.add(huidigeLocatie);
					Toast.makeText(context, "added location", Toast.LENGTH_LONG).show();
					update();
				} else {
					Toast.makeText(context, "huidgeLocatie was null gps off?", Toast.LENGTH_SHORT).show();
				}
			}
		});
		updatethingy = new Handler();
		doThing.run();
	}

	Runnable doThing = new Runnable() {
		public void run() {
			update();
			updatethingy.postDelayed(doThing, 5000);
		}
	};

	private void update() {
		if(huidigeLocatie != null) {
			GPSDistanceCalculator gps = new GPSDistanceCalculator();
			int shortestDistance = 0; 
			for (GPSLocation iterativeLocation : locations) {
				int currentDistance = gps.calculateDistanceBetweenCoordinatesInMethers(iterativeLocation, huidigeLocatie);
				if (shortestDistance == 0) {
					shortestDistance = currentDistance;
				} else {
					if (currentDistance < shortestDistance ) {
						shortestDistance = currentDistance;
					}
				}
				changeColor(51);
			}
		} else {
			//TODO basically the update comes too soon before a location has been found by the device
		}
	}

	private void changeColor(int methers) {
		String theColorie = "#FF";

		if (methers > 100) {
			methers = 100;
		}
		//greenish
		if(methers <= 50) {
			if(methers > 45) {
				methers = 6;
			} else {
				methers = 50 - methers;
			}
			int scaledNumber = (int) ((methers  * 5.1));
			String greenday = Integer.toHexString(scaledNumber);
			theColorie += "00";
			System.out.println(greenday.length());
			if(greenday.length() == 1) {
				theColorie += "0";
			}
			theColorie += greenday + "00";
		}
		//reddish
		else  {
			methers = 100 - (methers - 50);
			int scaledNumber = (int) (methers / 2 * 5.1);
			String blueballs = Integer.toHexString(scaledNumber);
			if(blueballs.length() == 1) {
				theColorie += "0";
			}
			theColorie += blueballs + "0000";
		}
		
		


		//		String red = Integer.toHexString(methers);
		//		if (red.length() == 1) {
		//			red = "0" + red;
		//		}
		//		String green = Integer.toHexString(255 - methers);
		//		if (green.length() == 1) {
		//			green = "0" + green;
		//		}
		//		int afstandKleur = (int) Long.parseLong("FF" +  red.toUpperCase() + "" + green.toUpperCase() + "00",16);
		System.out.println(theColorie);
		int[] shapecolor = new int[] {Color.parseColor(theColorie), Color.parseColor(theColorie)};
		FrameLayout framelayout1 = (FrameLayout) findViewById(R.id.frameLayout1);
		GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, shapecolor);
		gd.setCornerRadius(GradientDrawable.RECTANGLE);
		gd.setShape(GradientDrawable.OVAL);
		gd.setSize(480,480);
		framelayout1.setBackgroundDrawable(gd);
	}

	private Boolean displayGpsStatus() {  
		ContentResolver contentResolver = getBaseContext().getContentResolver();  
		boolean gpsStatus = Settings.Secure.isLocationProviderEnabled(contentResolver,	LocationManager.GPS_PROVIDER);  
		if (gpsStatus) {  
			return true;  
		} else {  
			return false;  
		}  
	}  

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_walk_with_you, menu);

		return true;
	}

	private class MyLocationListener implements LocationListener {  
		public void onLocationChanged(Location loc) {  
			huidigeLocatie = new GPSLocation(loc.getLatitude(), loc.getLongitude());
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}  
