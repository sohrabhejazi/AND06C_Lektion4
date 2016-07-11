package and06c.lektion4;

//import and06c.lektion1.R;
import android.support.v7.app.ActionBarActivity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NewGatherActivity extends ActionBarActivity {

	private Intent serviceIntent;
	private static final String TAG = NewGatherActivity.class.getSimpleName();
	private LocationService service;
	private TextView tvOutput;
	private EditText etSubject;
	private EditText etNote;
	private String newLine =System.getProperty("line.separator");
	private LocationService.LocationServiceBinder binder;
	//private String newLine =System.getProperty("line.separator");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.newmain);
		serviceIntent = new Intent(this, LocationService.class);
		//this.startService(serviceIntent);
		this.bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
		final Button btQuit = (Button)this.findViewById(R.id.bt_quit);
		tvOutput = (TextView) this.findViewById(R.id.tv_output);
		etSubject= (EditText) this.findViewById(R.id.et_subject);
		etNote= (EditText) this.findViewById(R.id.et_note);
		final Button btSave = (Button) findViewById(R.id.bt_save);
		btSave.setOnClickListener(new OnClickListener()
		{
		@Override
		public void onClick(View arg0)
		{
		tvOutput.setText("Sie wollen speichern? Danke für den Hinweis, er wird alsbald mit AND07C erledigt!");
		}
		});
		Button btLocation = (Button)
				this.findViewById(R.id.bt_location);
				btLocation.setOnClickListener(new OnClickListener()
				{
				@Override
				public void onClick(View v) {
					if(binder == null)
					{
					tvOutput.setText("Der GPS-Dienst wurde beendet, bitte zunächst neu starten.");
					return;
					}
				//service= new LocationService();
				//NoteLocation lastLocation = service.getLastLocation();
				//NoteLocation	lastLocation = new NoteLocation(37.422006,-122.084895, 200);
				NoteLocation location = binder.getLastLocation();
				if (location == null)
				{
				tvOutput.setText("Es liegen noch keine GPS-Daten vor - bitte später erneut versuchen.");
				return;
				}
				tvOutput.setText("lat/lon = "
				+ location.geoPoint.latitude + "/"
				+ location.geoPoint.longitude);
				Intent intent = new Intent(NewGatherActivity.this,
						NewNoteMapActivity.class);
						if(location != null) {
						intent.putExtra("latitude",
						location.geoPoint.latitude);
						intent.putExtra("longitude",
						location.geoPoint.longitude);
						intent.putExtra("subject", etSubject.getText().toString());
						intent.putExtra("note", etNote.getText().toString());
						}
						try {
						startActivity(intent);
						} catch(Exception e) {
						Log.e(TAG, e.toString());
						}
				}
				});
		btQuit.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// Fall "Beenden"
						if(btQuit.getText().equals(
						getResources().getString(R.string.bt_quit)))
						{
						binder.stopTimer();
						endService("ButtonQuit.onClick");
						long diff = LocationService.Duration;
						int Seconds = (int)diff % 60;  
						int Hours = (int)diff / 3600;
						int Minutes = (int)(diff-Hours*3600) /60;         
						tvOutput.setText("GPS-Dienst beendet");
						tvOutput.append(newLine +LocationService.Counter + " GPS-Abrufe, Laufzeit " 
						+Hours+":"+Minutes+":"+Seconds);
						binder = null;
						btQuit.setText(getResources()
						.getString(R.string.bt_restart));
						btQuit.setTextColor(getResources()
						.getColor(R.color.warning));
						// Fall "Neustart"
						} else {
						bindService(serviceIntent, serviceConnection, BIND_AUTO_CREATE);
						tvOutput.setText("GPS-Dienst läuft wieder");
						btQuit.setText(getResources()
						.getString(R.string.bt_quit));
						btQuit.setTextColor(getResources()
						.getColor(R.color.text));
						}
						}
				});
	}
	private ServiceConnection serviceConnection =
			new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name,
		IBinder service) {
		binder =
		(LocationService.LocationServiceBinder)service;
		}
			@Override
		public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				
				Log.d(TAG,
						"ServiceConnection.onServiceDisconnected von "
						+ name.getClassName() + " aufgerufen.");
				
			}
			};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.endService("onDestroy");
	}
/*	private void endService(String caller) {
		Log.d(TAG, "service=" + service);
		if(service != null) {
		this.unbindService(serviceConnection);
		this.stopService(serviceIntent);
		service = null;
		Log.d(TAG, "Service durch " + caller + " beendet");
		} else
		Log.d(TAG, "endService: Service (IBinder) ist null");
	} */
	
	private void endService(String caller) {
		Log.d(TAG, "service=" + binder);
		if(binder != null) {
		this.unbindService(serviceConnection);
		this.stopService(serviceIntent);
		binder = null;
		Log.d(TAG, "Service durch " + caller + " beendet");
		} else
		Log.d(TAG, "endService: Binder ist null");
		}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.new_gather, menu);
		menu.findItem(R.id.del).setIcon(getResources().getDrawable(android.R.drawable.ic_menu_delete ));
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		
		int itemId = item.getItemId();
		switch(itemId)
		{
		case R.id.del:
		return false;
		case R.id.action_settings:
		return false;
		default:
		return super.onOptionsItemSelected(item);
		}
		
	}
}
