package org.thepeoplesassociation.phillipphramework;

import org.thepeoplesassociation.phillipphramework.communication.PhrameworkBluetooth;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public abstract class PhrameworkActivity extends Activity{

	protected static final int NOTHING = 0;
	protected static final int BLUETOOTH = 1;
	protected static final int BLUETOOTH_NOTIFY = 2;
	protected static final int LOCATION_FINE = 4;
	protected static final int LOCATION_COARSE = 8;
	
	private static final int REQUEST_ENABLE_BT = 1000;
	
	protected PhrameworkApplication application;
	
	protected PhrameworkBluetooth bluetooth;
	
	protected boolean bluetoothSupported;
	
	protected boolean notifyBluetooth = false;
	/**
	 * call this instead of the standard onCreate
	 * @param savedInstanceState the bundle instance state
	 * @param layout the layout that this activity will use no need to set the content view if you call this
	 * @param initiate a sum of all the available features you can initiate
	 */
	protected void onCreate(Bundle savedInstanceState, int layout, int initiate){
		super.onCreate(savedInstanceState);
		application = (PhrameworkApplication) getApplication();
		application.addActivity(this);
		setContentView(layout);
		if(initiate - LOCATION_COARSE >= 0){
			initiate -= LOCATION_COARSE;
		}
		if(initiate - LOCATION_FINE >= 0){
			initiate -= LOCATION_FINE;
		}
		if(initiate - BLUETOOTH_NOTIFY >= 0){
			initiate -= BLUETOOTH_NOTIFY;
			notifyBluetooth = true;
			initiateBluetooth(true);
		}
		if(initiate - BLUETOOTH >= 0){
			initiate -= BLUETOOTH;
			initiateBluetooth(false);
		}
	}
	
	protected final void initiateBluetooth(boolean notify){
		if(application.bluetooth == null) application.bluetooth = new PhrameworkBluetooth();
		bluetooth = application.bluetooth;
		bluetoothSupported = application.bluetooth.bluetoothSupported();
		if(!bluetoothSupported && notify)
			notifyBluetoothRequired();
		else
			application.checkIfBluetoothEnabled();
	}
	
	private final void notifyBluetoothRequired(){
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setTitle(R.string.device_unsupported);
		dialog.setMessage(R.string.bluetooth_required);
		dialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				bluetoothUnsupported();
			}
		});
		dialog.show();
	}
	public void requestBluetoothEnable(){
		Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
	}
	/**
	 * If the activity is unable to find or initiate blue tooth this will be called after the use accepts the dialog <br>
	 * by default this will close the activity if you wish it to do something else you must override this method
	 */
	public void bluetoothUnsupported(){
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode){
		case REQUEST_ENABLE_BT:
			if(resultCode == RESULT_CANCELED){
				if(notifyBluetooth)
					notifyBluetoothRequired();
			}
		}
	}
	
	@Override
	public void finish(){
		super.finish();
		application.removeActivity(this);
	}
	
	public void onBluetoothConnectionEstablished(){}
}