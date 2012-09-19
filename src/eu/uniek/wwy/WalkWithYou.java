package eu.uniek.wwy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
//	private boolean flag = false;
	private Handler updateHandler;
	private GPSDistanceCalculator gps;
	private Toast toast;

	@Override
	public void onCreate(Bundle savedInstanceState) { 
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_walk_with_you);
		gps = new GPSDistanceCalculator();
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		locationListener = new MyLocationListener();  
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10,locationListener);  
		locations = new ArrayList<GPSLocation>();
		FrameLayout framelayout = (FrameLayout) findViewById(R.id.frameLayout1);
		GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {0xFF131313, 0xFF392399});
		gd.setCornerRadius(0f);
		gd.setShape(GradientDrawable.OVAL);
		gd.setSize(480,480);
		framelayout.setBackgroundDrawable(gd);
		framelayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(huidigeLocatie != null) {
					if(!gps.locatieExists(huidigeLocatie, locations)) {
						locations.add(new GPSLocation(huidigeLocatie.getLatitude(), huidigeLocatie.getLongitude()));
						showToast("added location");
						toast.show();
						update();
					} else {
						showToast("locatie already exists");
					}
				} else {
					showToast("huidgeLocatie was null gps off?");
				}
			}
		});
		updateHandler = new Handler();
		updateRunnable.run();
	}
	
	public void showToast(String message) {
		if(toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
		toast.show();
	}

	Runnable updateRunnable = new Runnable() {
		public void run() {
			update();
			updateHandler.postDelayed(updateRunnable, 5000);
		}
	};

	private void update() {
		if(huidigeLocatie != null) {
			int shortestDistance = 0; 
			int i = 1;
			String toastText = "";
			toastText += "huidigeLocatie = " + huidigeLocatie.getLongitude() + ", " + huidigeLocatie.getLatitude() + "\n";
			for (GPSLocation iterativeLocation : locations) {
				toastText += "locatie" + i+ " = " + iterativeLocation.getLongitude() + ", " + iterativeLocation.getLatitude() + "\n";
				int currentDistance = gps.calculateDistanceBetweenCoordinatesInMethers(iterativeLocation, huidigeLocatie);
				toastText += "Debug locatie 1 current distance: " + currentDistance;
				if (shortestDistance == 0) {
					shortestDistance = currentDistance;
				} else {
					if (currentDistance < shortestDistance ) {
						shortestDistance = currentDistance;
					}
				}
				i++;
			}
			toastText += "shortestdistance: " + shortestDistance; 
			showToast(toastText);

			changeColor(25);
		} else {
			//TODO basically the update comes too soon before a location has been found by the device
		}
	}

	private void changeColor(int methers) {
		int[] shapecolor = new int[] {getColorOnScaleOf50(methers), getColorOnScaleOf50(methers)};
		FrameLayout framelayout1 = (FrameLayout) findViewById(R.id.frameLayout1);
		GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, shapecolor);
		gd.setCornerRadius(GradientDrawable.RECTANGLE);
		gd.setShape(GradientDrawable.OVAL);
		gd.setSize(480,480);
		framelayout1.setBackgroundDrawable(gd);
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

	public int getColorOnScaleOf100(int power) {
		power++;
		if(power > 100) {
			power = 100;
		}
		int H = (int) (power * 1.2);
		int S = 100;
		int V = 100;
		return Color.HSVToColor(new float[] {H, S, V});
	}
	public int getColorOnScaleOf50(int power) {
		power++;
		if(power > 50) {
			power = 50;
		}
		int H = (int) (power * 2.4);
		int S = 100;
		int V = 100;
		return Color.HSVToColor(new float[] {H, S, V});
	}

	@Override
	protected void onPause() {
		super.onPause();
		updateHandler.removeCallbacks(updateRunnable);
	}
	@Override 
	protected void onResume() {
		super.onResume();
		updateRunnable.run();
	}
}  
