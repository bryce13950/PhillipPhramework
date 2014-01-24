package org.thepeoplesassociation.phillipphramework.communication;

import java.util.ArrayList;

import org.thepeoplesassociation.phillipphramework.PhrameworkApplication;
import org.thepeoplesassociation.phillipphramework.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;

public class PhrameworkBluetooth {
	
	private BluetoothAdapter mAdapter;
	
	private BluetoothSocket mSocket;
	
	private ProgressDialog mProgressDialog;

	private ArrayList<BluetoothDevice> devices;
	
	private PhrameworkApplication application;
	
	public PhrameworkBluetooth(){
		mAdapter = BluetoothAdapter.getDefaultAdapter();
		application = PhrameworkApplication.instance;
	}
	
	public BluetoothSocket getSocket(){
		return mSocket;
	}
	
	private void askToEnable( int message){
		Activity activity = application.getRecentActivity();
		if(activity != null && activity.hasWindowFocus()){
			new AlertDialog.Builder(activity)
			.setMessage(message)
			.setPositiveButton(R.string.enable,new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mAdapter.enable();
				}
			})
			.setNegativeButton(R.string.cancel,null)
			.show();
		}
	}
	
	private void unknownError(){
		Activity activity = application.getRecentActivity();
		if(activity != null && activity.hasWindowFocus()){
			new AlertDialog.Builder(activity)
			.setMessage(R.string.bluetooth_unknown)
			.setPositiveButton(R.string.ok, null)
			.show();
		}
	}
	
	public boolean bluetoothSupported(){
		if(mAdapter==null)return false;
		return true;
	}
	
	public BluetoothAdapter getAdapter(){
		return mAdapter;
	}
	
	public class DiscoverBluetoothDevices extends PhrameworkAsyncTask<Void,BluetoothDevice,Boolean>{
		
		public static final int SEARCH_UNABLE = 0;
		
		private BluetoothDevice newDevice = null;
		
		private final long startTime = System.currentTimeMillis();
		
		public DiscoverBluetoothDevices() {
			super("DiscoverBluetoothDevices");
			devices = new ArrayList<BluetoothDevice>();
			PhrameworkApplication.instance.searchingForBluetooth = true;
			IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
			application.registerReceiver(mReceiver, filter); 
			executeOnExecutor(THREAD_POOL_EXECUTOR);
		}
	
		@Override
		protected Boolean doWork(Void... params) {
			boolean started = mAdapter.startDiscovery();
			if(!started) {
				mAdapter.cancelDiscovery();
				return started;
			}
			if(!mAdapter.isDiscovering()){
				while(!mAdapter.isDiscovering()){
				}
			}
			while(mAdapter.isDiscovering() || startTime + 4000 > System.currentTimeMillis()){
				if(newDevice != null){
					super.publishProgress(new BluetoothDevice[]{newDevice});
					newDevice = null;
				}
			}
			mAdapter.cancelDiscovery();
			return started;
		}
		@Override
		protected void onProgressUpdate(BluetoothDevice... device){
			application.onBluetoothDeviceDiscovered(device[0]);
		}
	
		@Override
		public void handlePostExecute(Boolean success) {
			if(mProgressDialog!=null)mProgressDialog.dismiss();
			application.unregisterReceiver(mReceiver);
			if(success)
				application.doneFetchingBluetooth(devices);
			else if(!mAdapter.isEnabled())
				askToEnable(R.string.bluetooth_search_unable);
			else
				unknownError();
			PhrameworkApplication.instance.searchingForBluetooth = false;
		}
	
		private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
		    public void onReceive(Context context, Intent intent) {
		        String action = intent.getAction();
		        if (BluetoothDevice.ACTION_FOUND.equals(action)) {
		            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		        	for(BluetoothDevice previouslyFound:devices){
		        		if(previouslyFound.getAddress().equals(device.getAddress()))return;
		        	}
		        	newDevice = device;
		            devices.add(device);
		        }
		    }
		};
	}
}
