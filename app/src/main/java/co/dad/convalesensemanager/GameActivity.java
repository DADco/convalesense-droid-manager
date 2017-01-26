package co.dad.convalesensemanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.Set;
import java.util.UUID;

import co.lujun.lmbluetoothsdk.BluetoothController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;

import static co.dad.convalesensemanager.GamesActivity.CONVALESENSE;

public abstract class GameActivity extends AppCompatActivity {

    //BluetoothMessagingService mChatService;
    private BluetoothController mBTController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        mBTController = BluetoothController.getInstance();

        mBTController.setBluetoothListener(new BluetoothListener() {
            @Override
            public void onReadData(BluetoothDevice device, byte[] data) {
                String dataString = new String(data);
                Log.d("tag", "on data received!!!");
                onNewData(1);
            }

            @Override
            public void onActionStateChanged(int preState, int state) {

            }

            @Override
            public void onActionDiscoveryStateChanged(String discoveryState) {

            }

            @Override
            public void onActionScanModeChanged(int preScanMode, int scanMode) {

            }

            @Override
            public void onBluetoothServiceStateChanged(int state) {

            }

            @Override
            public void onActionDeviceFound(BluetoothDevice device, short rssi) {
                if (CONVALESENSE.equals(device.getName())) {
                    //connectDevice(device.getAddress());
                }
            }
        });

        Log.d("tag", "connected device ? "+mBTController.getConnectedDevice() != null ? "Yes" : "No");
        mBTController.reConnect(mBTController.getConnectedDevice().getAddress());
        //pairDevices();
    }


    private void pairDevices() {

        Set<BluetoothDevice> devices = mBTController.getBondedDevices();

        for (BluetoothDevice bd :
                devices) {
            Log.d("tag", "bonded device : " + bd.getName());
            if (bd.getName().equals(CONVALESENSE)) {
                mBTController.connect(bd.getAddress());
                return;
            }
        }

        mBTController.startScan();
    }
    /*private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Log.d("handler", msg.toString());

            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothMessagingService.STATE_CONNECTED:
                            break;
                        case BluetoothMessagingService.STATE_CONNECTING:
                            Log.d("tag", "connecting");
                            break;
                        case BluetoothMessagingService.STATE_LISTEN:
                        case BluetoothMessagingService.STATE_NONE:
                            Log.d("tag", "Listening");
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.d("tag", "write : "+writeMessage);
                    Toast.makeText(GameActivity.this, writeMessage, Toast.LENGTH_LONG).show();
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    Log.d("tag", "read ;" + readMessage);
                    onNewData(1);
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(GameActivity.this, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };*/

    public abstract void onNewData(int count);
}
