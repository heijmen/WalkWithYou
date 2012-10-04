package eu.uniek.wwy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;


public class WalkWithYou extends Activity {

	public static final String BROODKRUIMELS_LOCATION = "broodkruimels.txt";
	private LocationManager locationManager = null;  
	private LocationListener locationListener = null;
	private List<GPSLocation> locations;
	private GPSLocation huidigeLocatie;
	//	private boolean flag = false;
	private Handler updateHandler;
	private Toast toast;
	private GPSLocation cafetariaChao;  //27.4388 km vanaf gouda hemelsbreed  100m vanaf oudenoord 250 meter vanaf nijenoord allemaal hemelsbreed
	private boolean debug = true;
	private final int UPDATE_TIME = 500; //== half second
	private List<GPSLocation> broodKruimels = new ArrayList<GPSLocation>();
	private ThomasMagEenNaamVerzinnen dao = new ThomasMagEenNaamVerzinnen();
	private Context context = this;
	private GPSLocation lastDroppedLocation;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		try {
			broodKruimels = dao.getBroodkruimels(openFileInput(WalkWithYou.BROODKRUIMELS_LOCATION));
			String broodKruimelDebugText = "broodKruimels: \n";
			for(GPSLocation gpsLocation : broodKruimels) {
				broodKruimelDebugText += gpsLocation.getLatitude() + "," + gpsLocation.getLongitude() + "\n";
			}
			showToast(broodKruimelDebugText);
		} catch (Exception e) {
			showToast(e.getMessage());
			broodKruimels = new ArrayList<GPSLocation>();
		}
		setContentView(R.layout.activity_walk_with_you);
		if(debug) {
			cafetariaChao = new GPSLocation(52.10164, 5.10838);
		}
		locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		locationListener = new MyLocationListener();  
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, UPDATE_TIME, 1, locationListener);  
		locations = new ArrayList<GPSLocation>();
		locations.add(cafetariaChao);
		FrameLayout framelayout = (FrameLayout) findViewById(R.id.frameLayout1);
		GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {0xFF131313, 0xFF392399});
		gd.setCornerRadius(0f);
		gd.setShape(GradientDrawable.OVAL);
		gd.setSize(480,480);
		framelayout.setBackgroundDrawable(gd);
		framelayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(huidigeLocatie != null) {
					if(!GPSHandler.locationExistsInRange(10, huidigeLocatie, locations)) {
						locations.add(new GPSLocation(huidigeLocatie.getLatitude(), huidigeLocatie.getLongitude()));
						showToast("added location");;
						updateColorBackground();
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
		if(debug) {
			if(toast != null) {
				toast.cancel();
			}
			toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			toast.show();
			String broodKruimelDebugText = "broodKruimels \n";
			for(GPSLocation gpsLocation : broodKruimels) {
				broodKruimelDebugText += gpsLocation.getLatitude() + "," + gpsLocation.getLongitude() + ", " + gpsLocation.getTimeCreated() + "\n";
			}
			
			toast = Toast.makeText(this, broodKruimelDebugText, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	Runnable updateRunnable = new Runnable() {
		public void run() {
			updateColorBackground();
			
			if(huidigeLocatie != null) {
				if(broodKruimels == null) {
					broodKruimels = new ArrayList<GPSLocation>();
				}
				if(lastDroppedLocation != null) {
					if(GPSHandler.calculateDistanceBetweenCoordinatesInMethers(lastDroppedLocation, huidigeLocatie) > 50) {
						GPSLocation newLocation = new GPSLocation(huidigeLocatie.getLatitude(), huidigeLocatie.getLongitude());
						lastDroppedLocation = newLocation;
						broodKruimels.add(newLocation);
					}
				} else {
					GPSLocation newLocation = new GPSLocation(huidigeLocatie.getLatitude(), huidigeLocatie.getLongitude());
					lastDroppedLocation = newLocation;
					broodKruimels.add(newLocation);
					
				}
				try {
					dao.saveBroodKruimels(broodKruimels, openFileOutput(WalkWithYou.BROODKRUIMELS_LOCATION, MODE_PRIVATE));
				} catch (Exception e) {
					showToast(e.getMessage());
				} 
			}
			updateHandler.postDelayed(updateRunnable, UPDATE_TIME);
		}
	};

	private void updateColorBackground() {
		if(huidigeLocatie != null) {
			int shortestDistance = 0; 
			int i = 1;
			String toastText = "";
			toastText += "huidigeLocatie = " + huidigeLocatie.getLongitude() + ", " + huidigeLocatie.getLatitude() + "\n";
			for (GPSLocation iterativeLocation : locations) {
				toastText += "locatie" + i + " = " + iterativeLocation.getLongitude() + ", " + iterativeLocation.getLatitude() + "\n";
				int currentDistance = GPSHandler.calculateDistanceBetweenCoordinatesInMethers(iterativeLocation, huidigeLocatie);
				toastText += "Debug locatie " + i + " current distance: " + currentDistance + "\n";
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

			changeColor(shortestDistance);
			if(shortestDistance <= 10) {
				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(UPDATE_TIME);
			}
		} 
	}

	private void changeColor(int methers) {
		int[] shapecolor = new int[] {getColorOnScaleOf100(methers), getColorOnScaleOf100(methers)};
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
	@Deprecated
	public int getColorOnScaleOf100(int power) {
		power = 100 - power;
		power++;
		if(power > 100) {
			power = 100;
		}
		int H = (int) (power * 1.2);
		int S = 100;
		int V = 100;
		return Color.HSVToColor(new float[] {H, S, V});
	}
	@Deprecated
	public int getColorOnScaleOf50(int power) {
		power = 50 - power;
		power++;
		if(power > 50) {
			power = 50;
		}
		int H = (int) (power * 2.4);
		int S = 100;
		int V = 100;
		return Color.HSVToColor(new float[] {H, S, V});
	}
	
	public int getAndroidColorByMethers(int power, int maxRange) {
		power = maxRange - power;
		power++;
		if(power > maxRange) {
			power = maxRange;
		}
		int H = (int) (power * (120 / maxRange));
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
