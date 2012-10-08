package eu.uniek.wwy.gui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.Toast;
import eu.uniek.wwy.R;
import eu.uniek.wwy.location.Breadcrumbs;
import eu.uniek.wwy.location.GPSHandler;
import eu.uniek.wwy.location.GPSLocation;
import eu.uniek.wwy.location.GPSLocationListener;

public class WalkWithYou extends Activity {
	public static final int updateTime = 500;
	private final int[] standardColorShape = new int[] {0xFF131313, 0xFF392399};
	private Breadcrumbs breadcrumbs = new Breadcrumbs(this);
	private boolean debug = true;
	private Handler updateHandler;
	private Toast toast;
	private FrameLayout framelayout;
	private GPSLocationListener gpsLocationListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		breadcrumbs.readBreadcrumbsFromFile();
		showToast(getAllBreadCrumbsInfo());
		setContentView(R.layout.activity_walk_with_you);
		breadcrumbs.setLocations();
		setFrameLayout();
		setUpdateHandler();
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

	private void setUpdateHandler() {
		updateHandler = new Handler();
		updateRunnable.run();
	}


	private String getAllBreadCrumbsInfo() {
		StringBuilder breadCrumbDebugText = new StringBuilder("broodKruimels: \n");
		breadCrumbDebugText.append(breadcrumbs.getBreadcrumbsInfo());
		return breadCrumbDebugText.toString();
	}
	
	public StringBuilder getBreadCrumbInfo(GPSLocation gpsLocation) {
		StringBuilder stringbuilder = new StringBuilder();
		stringbuilder.append(gpsLocation.getLatitude());
		stringbuilder.append(", ");
		stringbuilder.append(gpsLocation.getLongitude());
		stringbuilder.append(", ");
		stringbuilder.append(gpsLocation.getTimeCreated());
		stringbuilder.append('\n');
		return stringbuilder;
	}

	protected void addLocation() {
		GPSLocation currentLocation = gpsLocationListener.getCurrentLocation();
		if (currentLocation == null) {
			showToast("Unable to find location");
		} else if (!GPSHandler.locationExistsInRange(10, currentLocation, breadcrumbs.getAllLocations())) {
			breadcrumbs.addLocation(currentLocation);
			showToast("Added location");;
			updateColorBackground();
		} else {
			showToast("Location already exists");
		}
	}

	private GradientDrawable getGradientDrawable(int[] shapecolor) {
		GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, shapecolor);
		gradientDrawable.setCornerRadius(0f);
		gradientDrawable.setShape(GradientDrawable.OVAL);
		gradientDrawable.setSize(480,480);
		return gradientDrawable;
	}

	public void showToast(String message) {
		if (debug) {
			cancelCurrentToasts();
			toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
			toast.show();
			toast = Toast.makeText(this, getAllBreadCrumbsInfo(), Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	private void cancelCurrentToasts() {
		if (toast != null) {
			toast.cancel();
		}
	}

	private Runnable updateRunnable = new Runnable() {
		public void run() {
			update();
			updateHandler.postDelayed(updateRunnable, updateTime);
		}
	};
	public void update() {
		updateColorBackground();
		if (gpsLocationListener.getCurrentLocation() != null) {
			breadcrumbs.update();
		}
	}
	
	private void updateColorBackground() {
		GPSLocation currentLocation = gpsLocationListener.getCurrentLocation();
		if (currentLocation != null) {
			float shortestDistance = 0;
			int locationNumber = 1;
			StringBuilder toastText = new StringBuilder("currentLocation = ");
			toastText.append(getBreadCrumbInfo(currentLocation).toString());
			for (GPSLocation location : breadcrumbs.getAllLocations()) {
				float currentDistance = GPSHandler.distanceBetween(location, currentLocation);
				showToast(makeToastText(location,locationNumber,currentDistance));
				if (shortestDistance == 0) {
					shortestDistance = currentDistance;
				} else if (currentDistance < shortestDistance) {
					shortestDistance = currentDistance;
				}
				locationNumber++;
			}
			toastText.append("shortestdistance: ");
			toastText.append(shortestDistance);
			giveFeedback(shortestDistance);
		}
	}

	private String makeToastText(GPSLocation location, int locationNumber, float currentDistance) {
		StringBuilder toastText = new StringBuilder();
		toastText.append("Location");
		toastText.append(locationNumber);
		toastText.append(" = ");
		toastText.append(getBreadCrumbInfo(location));
		toastText.append("Location ");
		toastText.append(locationNumber);
		toastText.append(" current distance: ");
		toastText.append(currentDistance);
		toastText.append("\n");
		return toastText.toString();
	}

	private void giveFeedback(float shortestDistance) {
		changeColor(shortestDistance);
		if (shortestDistance <= 10) {
			Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(updateTime);
		}
	}

	private void changeColor(float meters) {
		int[] shapecolor = new int[] {getColor(meters,100), getColor(meters, 100)};
		framelayout.setBackgroundDrawable(getGradientDrawable(shapecolor));
	}

	private void setFrameLayout() {
		framelayout = (FrameLayout) findViewById(R.id.frameLayout);
		framelayout.setBackgroundDrawable(getGradientDrawable(standardColorShape));
		framelayout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				addLocation();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_walk_with_you, menu);
		return true;
	}

	public int getColor(float power, int scale) {
		int hue = getHue(power, scale);
		int saturation = 1;
		int value = 1;
		return Color.HSVToColor(new float[] {hue, saturation, value});
	}

	private int getHue(float power, int scale) {
		int hue = (int) (scale - ++power);
		if(hue > scale) {
			hue = scale;
		}
		hue = hue * (120 / scale);
		return hue;
	}
	
	public void setGPSLocationListener(GPSLocationListener gpsLocationListener) {
		this.gpsLocationListener = gpsLocationListener;		
	}
	
	public Breadcrumbs getBreadcrumbs() {
		return breadcrumbs;
	}

	public void setBreadcrumbs(Breadcrumbs breadcrumbs) {
		this.breadcrumbs = breadcrumbs;
	}

}  