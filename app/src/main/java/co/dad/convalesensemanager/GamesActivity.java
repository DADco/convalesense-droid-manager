package co.dad.convalesensemanager;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class GamesActivity extends AppCompatActivity {

    private static final int REQUEST_PERM = 123;
    public static final String CONVALESENSE = "Convalesense Android";

    @BindView(R.id.games_recycler)
    RecyclerView gamesList;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;

    private int planId;

    private GameAdapter adapter;
    private Exercise selectedExercice;
    private BluetoothController mBTController;
    private ProgressDialog progressDialog;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games);

        planId = (int) getIntent().getLongExtra("planId", -1);

        ButterKnife.bind(this);

        setTitle(getString(R.string.main_title));

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        gamesList.setLayoutManager(manager);

        gamesList.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                selectedExercice = adapter.getExercises().get(position);

                if (selectedExercice.getName().equals("Arm Strength")
                        || selectedExercice.getName().equals("Power Reflex")) {

                    progressDialog = new ProgressDialog(GamesActivity.this);
                    progressDialog.setIndeterminate(true);
                    progressDialog.setMessage("Connecting to sensor");
                    progressDialog.show();

                    pairDevices();
                } else {
                    Toast.makeText(GamesActivity.this, "Not available yet", Toast.LENGTH_SHORT).show();
                }

            }
        }));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchExercises();
            }
        });
    }

    private void initBluetooth() {
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

                if (state != State.STATE_CONNECTING
                        && progressDialog != null
                        && progressDialog.isShowing()) {
                    progressDialog.dismiss();
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

        initBluetooth();

        fetchExercises();
    }

    private void fetchExercises() {

        refreshLayout.setRefreshing(true);

        new AsyncTask<Void, Void, Plan>() {

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
                refreshLayout.setRefreshing(false);
            }

        }.execute();
    }

    private void gotoGame() {

        int exerciceId = selectedExercice.getId();
        mBTController.write(selectedExercice.getName().getBytes());

        if (selectedExercice.getName().equals("Arm Strength")) {
            Intent intent = new Intent(this, ArmStrengthGameActivity.class);
            intent.putExtra("repetition", selectedExercice.getRepetitions());
            intent.putExtra("exerciceId", exerciceId);
            startActivity(intent);

        } else if (selectedExercice.getName().equals("Power Reflex")) {
            Intent intent = new Intent(this, PowerReflexGameActivity.class);
            intent.putExtra("repetition", selectedExercice.getRepetitions());
            intent.putExtra("exerciceId", exerciceId);
            startActivity(intent);
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
