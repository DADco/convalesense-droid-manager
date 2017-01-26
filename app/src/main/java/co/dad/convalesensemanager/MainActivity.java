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
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.dad.convalesensemanager.model.Plan;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.plan_list)
    RecyclerView planList;
    private PlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        planList.setLayoutManager(manager);

        planList.addOnItemTouchListener(new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                long planId = adapter.getItemId(position);

                Intent intent = new Intent(MainActivity.this, GamesActivity.class);
                intent.putExtra("planId", planId);

                startActivity(intent);
            }
        }));


        // TODO REMOVE
        Intent intent = new Intent(MainActivity.this, GamesActivity.class);
        intent.putExtra("planId", 3);

        startActivity(intent);
        // TODO END REMOVE

    }

    @Override
    protected void onResume() {
        super.onResume();

        /*new AsyncTask<Void, Void, List<Plan>>() {

            @Override
            protected List<Plan> doInBackground(Void... voids) {
                try {
                    return ConvalesenseAPI.getInstance().getServices().getPlans().execute().body();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(List<Plan> plans) {
                adapter = new PlanAdapter();
                adapter.setPlans(plans);
                planList.setAdapter(adapter);
            }
        }.execute();*/
    }
}
