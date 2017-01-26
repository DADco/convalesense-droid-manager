package co.dad.convalesensemanager;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.dad.convalesensemanager.model.Exercise;
import co.dad.convalesensemanager.model.Plan;
import co.lujun.lmbluetoothsdk.BluetoothController;
import co.lujun.lmbluetoothsdk.base.BluetoothListener;
import co.lujun.lmbluetoothsdk.base.State;


public class GamesActivity extends AppCompatActivity {

    private static final int REQUEST_PERM = 123;
    public static final String CONVALESENSE = "Convalesense Android";

    @BindView(R.id.games_recycler)
    RecyclerView gamesList;

    private int planId;

    private GameAdapter adapter;
    private Exercise selectedExercice;
    private BluetoothController mBTController;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        planId = (int) getIntent().getLongExtra("planId", -1);

        ButterKnife.bind(this);


        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        gamesList.setLayoutManager(manager);

        gamesList.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedExercice = adapter.getExercises().get(position);
                pairDevices();
            }
        }));

        //BluetoothAdapter.getDefaultAdapter().setName(BT_NAME);

        mBTController = BluetoothController.getInstance().build(this);
        mBTController.setBluetoothListener(new BluetoothListener() {
            @Override
            public void onReadData(BluetoothDevice device, byte[] data) {

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
                Log.d("tag", "onBluetoothServiceStateChanged "+state);
                if (state == State.STATE_CONNECTED) {
                    gotoGame();
                }
            }

            @Override
            public void onActionDeviceFound(BluetoothDevice device, short rssi) {

                Log.d("tag", "discovered "+device.getName());

                if (CONVALESENSE.equals(device.getName())) {
                    device.createBond();
                    mBTController.connect(device.getAddress());
                }
            }
        });

        pairDevices();
    }

    @Override
    protected void onStart() {
        super.onStart();

        int fineLocPerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int fineCoarsePerm = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        if (fineCoarsePerm != PackageManager.PERMISSION_GRANTED
                || fineLocPerm != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_PERM);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        selectedExercice = null;

        /*new AsyncTask<Void, Void, Plan>() {

            @Override
            protected Plan doInBackground(Void... voids) {
                try {
                    return ConvalesenseAPI.getInstance().getServices().getPlan(planId).execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Plan plan) {
                adapter = new GameAdapter();
                adapter.setExercises(plan.getExercises());
                gamesList.setAdapter(adapter);
            }

        }.execute();*/
    }

    private void gotoGame() {



        //int exerciceId = selectedExercice.getId();
        int exerciceId = 1;
        mBTController.write(String.valueOf(exerciceId).getBytes());
        switch (exerciceId) {
            case 1:
                Intent intent = new Intent(this, ArmStrengthGameActivity.class);
                intent.putExtra("repetition", 20);
                intent.putExtra("gameId", exerciceId);
                startActivity(intent);
                break;
            case 2:

                break;
            case 3:

                break;
        }
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



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERM
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            pairDevices();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
