package and06c.lektion4;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import com.google.android.gms.location.LocationListener;





import android.annotation.SuppressLint;
//import and06c.lektion1.NoteLocation;
//import and06c.lektion1.GatherActivity.NoteLocationListener;
import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;


public class LocationService extends Service {
	
	static int Counter=0;  
	static long Duration=0;
	public Date start, stop;
	//static long diffInSeconds=0,diffInMinutes=0,diffInHours=0;
	
	//private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	private static final String TAG =
			LocationService.class.getSimpleName();
			private static NoteLocation lastLocation;
			private LocationManager locationManager;
			private NoteLocationListener locationListener;
			private long startTime; // --> ESA
			private int minTime = 5000; // Millisekunden
			private int minDistance = 0;
			private IBinder binder = new LocationServiceBinder();
			
			
	@Override
	public IBinder onBind(Intent intent) {
				Counter++;
				this.start = new Date();
		        locationManager.requestLocationUpdates(
		        LocationManager.GPS_PROVIDER, minTime,
				minDistance, locationListener);
				Log.d(TAG, "Geodatenerfassung gestartet (onBind)");
				return binder;
			}
	// IBinder-Implementierung
	/*class LocationServiceBinder extends Binder
			{
			public LocationService getService() {
				return LocationService.this;
			}
			}*/
	class LocationServiceBinder extends Binder {
		public NoteLocation getLastLocation() {
		return lastLocation;
		}
		
	    public void stopTimer() {
			
			stop = new Date();
			long TimeSpent  = stop.getTime() - start.getTime();
			Duration += TimeUnit.MILLISECONDS.toSeconds(TimeSpent);

		}
	}
	/*public NoteLocation getLastLocation()
	{
		
		return lastLocation;
	}*/
	public LocationService() {
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		locationManager = (LocationManager)
		this.getSystemService(LOCATION_SERVICE);
		locationListener = new NoteLocationListener();
		startTime = System.currentTimeMillis();
		
	}

@Override
public int onStartCommand(Intent intent, int flags, int startId) {
	// TODO Auto-generated method stub
	        
	        //lastLocation = new NoteLocation(37.422006,-122.084895, 200);
	        /*locationManager.requestLocationUpdates(
			LocationManager.GPS_PROVIDER,
			minTime, minDistance, locationListener);*/
			return START_STICKY; 
	
	// Implementierung nach "onBind" verlagert
	// Methode kann wegfallen
}


@Override
public void onDestroy() {
	// TODO Auto-generated method stub
	super.onDestroy();
	
	locationManager.removeUpdates((android.location.LocationListener) locationListener);
	Log.d(TAG, "Erfassung von Geodaten beendet(onDestroy)");
	 
}

	
class NoteLocationListener implements   android.location.LocationListener
	{
			@Override
			public void onLocationChanged(Location location)
			{
			lastLocation = new NoteLocation(
			location.getLatitude(),
			location.getLongitude(),
			(int)location.getAltitude());
			// TODO: "lastLocation" später in DB speichern! *)
			// Vorerst nur loggen:
			Log.d(TAG, lastLocation.toString());
			}
			public void onProviderDisabled(String provider) { }
			public void onProviderEnabled(String provider) { }
			public void onStatusChanged(String provider,
			int status, Bundle extras) { }
	}
}
