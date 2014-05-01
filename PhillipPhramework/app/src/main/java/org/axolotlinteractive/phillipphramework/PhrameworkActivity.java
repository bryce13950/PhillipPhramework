package org.axolotlinteractive.phillipphramework;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

public abstract class PhrameworkActivity extends Activity{

	protected static final int NOTHING = 0;
	/**
	 * pass this into the onCreate if you want to force the bluetooth radio on
	 */
	protected static final int BLUETOOTH_FORCE = 1;
	/**
	 * pass this into the onCreate if you want to ask the user to enable bluetooth
	 */
	protected static final int BLUETOOTH_ASK = 2;
	
	private static final int REQUEST_ENABLE_BT = 1000;
	
	protected PhrameworkApplication application;
	
	private int bluetoothRequirement = 0;
	
	protected boolean bluetoothSupported;
	
	@Deprecated
	@Override
	protected void onCreate(Bundle savedInstanceState){
		throw new RuntimeException("Please call the super method specific to this framework in order to take advantage of this framework");
	}
	/**
	 * call this instead of the standard onCreate
	 * @param savedInstanceState the bundle instance state
	 * @param layout the layout that this activity will use no need to set the content view if you call this
	 * @param initiate a sum of all the available features you want to initiate
	 */
	protected void onCreate(Bundle savedInstanceState, int layout, int initiate){
		super.onCreate(savedInstanceState);
		application = (PhrameworkApplication) getApplication();
		application.addActivity(this);
		setContentView(layout);
		if(initiate - BLUETOOTH_ASK >= 0){
			initiate -= BLUETOOTH_ASK;
			bluetoothRequirement = BLUETOOTH_ASK;
			initiateBluetooth();
		}
		if(initiate - BLUETOOTH_FORCE >= 0){
			initiate -= BLUETOOTH_FORCE;
			bluetoothRequirement = BLUETOOTH_FORCE;
			initiateBluetooth();
		}
	}
	
	protected final void initiateBluetooth(){
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		if(adapter == null)
			notifyBluetoothRequired();
		else if(!adapter.isEnabled()){
			if(bluetoothRequirement == BLUETOOTH_FORCE)
				adapter.enable();
			else{
				requestBluetoothEnable();
			}
		}
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