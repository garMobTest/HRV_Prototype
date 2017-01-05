package org.marco45.polarheartmonitor;

import java.util.List;
import java.util.UUID;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.util.Log;

/**
 * This thread to the connection with the bluetooth device
 * @author Marco
 *
 */
@SuppressLint("NewApi")
public class H7ConnectThread  extends Thread{
	
	MainActivity ac;	
	private BluetoothGatt gat; //gat server
	private final String HRUUID = "0000180D-0000-1000-8000-00805F9B34FB";
	static BluetoothGattDescriptor descriptor;
	static BluetoothGattCharacteristic cc;
	private static String TAG = "BEAT_BY_BEAT_TEST";
	
	public H7ConnectThread(BluetoothDevice device, MainActivity ac) {
		Log.i("H7ConnectThread", "Starting H7 reader BTLE");
		this.ac=ac;
		gat = device.connectGatt(ac, false, btleGattCallback); // Connect to the device and store the server (gatt)
	}

	
	/** Will cancel an in-progress connection, and close the socket */
	public void cancel() {
		gat.setCharacteristicNotification(cc,false);
		descriptor.setValue( BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
	    gat.writeDescriptor(descriptor);
		
		gat.disconnect();
		gat.close();
		Log.i("H7ConnectThread", "Closing HRsensor");
	}

	
	//Callback from the bluetooth 
	private final BluetoothGattCallback btleGattCallback = new BluetoothGattCallback() {
		 
		//Called everytime sensor send data
		@Override
	    public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
	    	byte[] data = characteristic.getValue();
            ac.addToBeatToBeat(extractBeatToBeatInterval(characteristic));
	    	int bmp = data[1] & 0xFF; // To unsign the value
	    	DataHandler.getInstance().cleanInput(bmp);
			Log.v("H7ConnectThread", "Data received from HR "+bmp);
	    }
	 
		//called on the successful connection
	    @Override
	    public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) { 
	    	if (newState ==  BluetoothGatt.STATE_DISCONNECTED)
	    	{
				Log.e("H7ConnectThread", "device Disconnected");
				ac.connectionError();
	    	}
	    	else{
				gatt.discoverServices();
				Log.d("H7ConnectThread", "Connected and discovering services");
	    	}
	    }
	 
	    //Called when services are discovered.
	    @Override
	    public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
	    	BluetoothGattService service = gatt.getService(UUID.fromString(HRUUID)); // Return the HR service
			//BluetoothGattCharacteristic characteristic = service.getCharacteristic(UUID.fromString("00002A37-0000-1000-8000-00805F9B34FB"));
			List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics(); //Get the hart rate value
			for (BluetoothGattCharacteristic cc : characteristics)
				{
					for (BluetoothGattDescriptor descriptor : cc.getDescriptors()) {
					    //find descriptor UUID that matches Client Characteristic Configuration (0x2902)
					    // and then call setValue on that descriptor
						
						//Those two line set the value for the disconnection
						H7ConnectThread.descriptor=descriptor;
						H7ConnectThread.cc=cc;
												
						gatt.setCharacteristicNotification(cc,true);//Register to updates
						descriptor.setValue( BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
					    gatt.writeDescriptor(descriptor);
						Log.d("H7ConnectThread", "Connected and regisering to info");
					}
				}
	    }
	};

	private static Integer[] extractBeatToBeatInterval(
			BluetoothGattCharacteristic characteristic) {

		int flag = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		int format = -1;
		int energy = -1;
		int offset = 1; // This depends on hear rate value format and if there is energy data
		int rr_count = 0;

		if ((flag & 0x01) != 0) {
			format = BluetoothGattCharacteristic.FORMAT_UINT16;
			Log.d(TAG, "Heart rate format UINT16.");
			offset = 3;
		} else {
			format = BluetoothGattCharacteristic.FORMAT_UINT8;
			Log.d(TAG, "Heart rate format UINT8.");
			offset = 2;
		}
		if ((flag & 0x08) != 0) {
			// calories present
			energy = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, offset);
			offset += 2;
			Log.d(TAG, "Received energy: {}"+ energy);
		}
		if ((flag & 0x16) != 0){
			// RR stuff.
			Log.d(TAG, "RR stuff found at offset: "+ offset);
			Log.d(TAG, "RR length: "+ (characteristic.getValue()).length);
			rr_count = ((characteristic.getValue()).length - offset) / 2;
			Log.d(TAG, "RR length: "+ (characteristic.getValue()).length);
			Log.d(TAG, "rr_count: "+ rr_count);
			if (rr_count > 0) {
				Integer[] mRr_values = new Integer[rr_count];
				for (int i = 0; i < rr_count; i++) {
					mRr_values[i] = characteristic.getIntValue(
							BluetoothGattCharacteristic.FORMAT_UINT16, offset);
					offset += 2;
					Log.d(TAG, "Received RR: " + mRr_values[i]);
				}
				return mRr_values;
			}
		}
		Log.d(TAG, "No RR data on this update: ");
		return null;
	}



}