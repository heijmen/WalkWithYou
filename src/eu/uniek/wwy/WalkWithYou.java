package eu.uniek.wwy;

import java.util.ArrayList;
import java.util.List;

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

public class WalkWithYou extends Activity {
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
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10,locationListener);  
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
					locations.add(new GPSLocation(huidigeLocatie.getLatitude(), huidigeLocatie.getLongitude()));
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
			int i = 1;
			String whatever = "";
			whatever += "huidigeLocatie = " + huidigeLocatie.getLongitude() + ", " + huidigeLocatie.getLatitude() + "\n";
			for (GPSLocation iterativeLocation : locations) {
				whatever += "locatie" + i+ " = " + iterativeLocation.getLongitude() + ", " + iterativeLocation.getLatitude() + "\n";
				int currentDistance = gps.calculateDistanceBetweenCoordinatesInMethers(iterativeLocation, huidigeLocatie);
				whatever += "Debug locatie 1 current distance: " + currentDistance;
				if (shortestDistance == 0) {
					shortestDistance = currentDistance;
				} else {
					if (currentDistance < shortestDistance ) {
						shortestDistance = currentDistance;
					}
				}
				i++;
			}
			whatever += "shortestdistance: " + shortestDistance; 
			Toast.makeText(this, whatever, Toast.LENGTH_LONG).show();
			changeColor(shortestDistance);
		} else {
			//TODO basically the update comes too soon before a location has been found by the device
		}
	}

	private void changeColor(int methers) {
		int[] shapecolor = new int[] {Color.parseColor(getColor(methers)), Color.parseColor(getColor(methers))};
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
	
	public String getColor(int power)
	{
		String colorString = "#FF";
		int R=(255*power)/100;
		int G=(255*(100-power))/100; 
		int B=0;
		String redString = Integer.toHexString(R);
		if(redString.length() == 1) {
			colorString += "0";
		}
		colorString += redString;
		
		String greenString = Integer.toHexString(G);
		if(greenString.length() == 1) {
			colorString += "0";
		}
		colorString += greenString;
		colorString += "00";
		return colorString;
	}
}  
