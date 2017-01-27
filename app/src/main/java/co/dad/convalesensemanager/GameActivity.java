package co.dad.convalesensemanager;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static co.dad.convalesensemanager.GamesActivity.CONVALESENSE;

public abstract class GameActivity extends AppCompatActivity {

    protected BluetoothController mBTController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBTController = BluetoothController.getInstance();

        mBTController.setBluetoothListener(new BluetoothListener() {
            @Override
            public void onReadData(BluetoothDevice device, byte[] data) {
                String dataString = new String(data);
                Log.d("tag", "on data received");
                onNewData(Integer.valueOf(dataString.trim()));
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

            }
        });

        mBTController.reConnect(mBTController.getConnectedDevice().getAddress());
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public abstract void onNewData(int count);
}
