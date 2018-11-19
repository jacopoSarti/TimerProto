package p290810.example.jack.timerproto;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.support.design.widget.FloatingActionButton;
import android.widget.Toast;

import com.example.jack.timerproto.model.Timer;
import com.example.jack.timerproto.services.ServiceCallbacks;
import com.example.jack.timerproto.services.TimerService;
import com.example.jack.timerproto.services.TimerService.TimerBinder;
import com.example.jack.timerproto.utils.AdapterCallbacks;
import com.example.jack.timerproto.utils.TimerAdapter;
import com.example.jack.timerproto.utils.TimerDBHelper;

import java.util.List;

import p290810.example.jack.timerproto.model.Timer;
import p290810.example.jack.timerproto.services.TimerService;
import p290810.example.jack.timerproto.utils.TimerAdapter;
import p290810.example.jack.timerproto.utils.TimerDBHelper;

public class MainActivity extends AppCompatActivity implements ServiceCallbacks, AdapterCallbacks {

    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private TimerAdapter mAdapter;
    private TimerDBHelper dbHelper;
    private FloatingActionButton fab;
    private boolean mBound;
    TimerService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //bind to service
        Intent intent = new Intent(this, TimerService.class);
        getApplicationContext().startService(intent);
        getApplicationContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(timersAreRunning()){
            mService.foreground();
        }
        if(mBound){
            mService.setCallbacks(null);
            getApplicationContext().unbindService(mConnection);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        //return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.addStopwatch:
                addTimer();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * defines service callbacks that will be passed to bindService and will give the service
     * a way of interacting with the UI
     */
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //we bound to TimerService, cast the IBinder and get TimerService instance
            TimerService.TimerBinder binder = (TimerService.TimerBinder)service;
            mService = binder.getService();
            mBound = true;
            // Ensure the service is not in the foreground when bound
            mService.background();
            mService.setCallbacks(MainActivity.this);
            initializeUI();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    private void initializeUI(){
        //initialize the variables
        mRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        //use a grid layout manager
        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);

        //populate recyclerview
        populateRecyclerView();

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTimer();
                mRecyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
            }
        });
    }

    private void populateRecyclerView(){
        mAdapter = new TimerAdapter(MainActivity.this, mRecyclerView, MainActivity.this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void addTimer(){
        Timer timer = mService.addTimer();
        mAdapter.notifyItemInserted(mService.getTimersList().size() - 1);
    }

    /**
     * Callbacks for TimerAdapter
     */
    @Override
    public TimerService getService(){
        return mService;
    }

    public boolean timersAreRunning(){
        for(Timer timer : mService.getTimersList()){
            if(timer.isRunning){
                return true;
            }

        }
        return false;
    }
}
