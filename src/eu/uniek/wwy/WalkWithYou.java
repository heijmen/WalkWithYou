package eu.uniek.wwy;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;

public class WalkWithYou extends Activity  {
	private LocationManager locationMangaer=null;  
	private LocationListener locationListener=null;
	private List<Locatie> locaties;
	private Locatie huidigeLocatie;
	private boolean flag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_walk_with_you);
		locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		flag = displayGpsStatus();  
		if (flag) {  			
			//waiting  
			locationListener = new MyLocationListener();  
			locationMangaer.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 10,locationListener);  
		} else {  
			//mislukt  
		}  
		locaties = new ArrayList<Locatie>();

		FrameLayout framelayout = (FrameLayout) findViewById(R.id.frameLayout1);
		GradientDrawable gd = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, new int[] {0xFF131313, 0xFF392399});
		gd.setCornerRadius(0f);
		gd.setShape(GradientDrawable.OVAL);
		gd.setSize(480,480);
		framelayout.setBackgroundDrawable(gd);

		final Context context = this;
		framelayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				locaties.add(huidigeLocatie);
				Toast.makeText(context, "added location", Toast.LENGTH_LONG).show();
				update();
			}
		});
	}

	private void update() {
		GPSDistanceCalculator gps = new GPSDistanceCalculator();
		int shortestDistance = 0; 
		for (Locatie iterativeLocation : locaties) {
			int currentDistance = gps.calculate(iterativeLocation, huidigeLocatie);
			if (shortestDistance != 0) {
				shortestDistance = currentDistance;
			} else {
				if (currentDistance < shortestDistance ) {
					shortestDistance = currentDistance;
				}
			}
			changeColor(shortestDistance);
		}
	}

	private void changeColor(int methers) {
		if (methers > 100) {
			methers = 100;
		}
		String red = Integer.toHexString(methers);
		if (red.length() == 1) {
			red = "0" + red;
		}
		String green = Integer.toHexString(255 - methers);
		if (green.length() == 1) {
			green = "0" + green;
		}
		int afstandKleur = (int) Long.parseLong("FF" +  red.toUpperCase() + "" + green.toUpperCase() + "00",16);
		int[] shapecolor = new int[] {afstandKleur, 0xFFFF0000};
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
			huidigeLocatie = new Locatie(loc.getLatitude(), loc.getLongitude());
		}

		public void onProviderDisabled(String provider) {
		}

		public void onProviderEnabled(String provider) {
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	}
}  
