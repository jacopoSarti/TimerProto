package p290810.example.jack.timerproto;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import p290810.example.jack.timerproto.model.Timer;
import p290810.example.jack.timerproto.services.TimerService;

public class TimerActivity extends AppCompatActivity {

    private TimerService mService;
    private boolean mBound;

    private TextView value;
    private Button startButton;
    private Button pauseButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        Intent intent = new Intent(this, TimerService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


        value = (TextView)findViewById(R.id.timerValue);
        startButton = (Button)findViewById(R.id.startButton);
        pauseButton = (Button)findViewById(R.id.pauseButton);

        int position = getIntent().getExtras().getInt("position");
        Timer timer = mService.getTimersList().get(position);
        timer.timerValue = value;

        /*if(timer.isRunning == true){
            timer.startTime = SystemClock.uptimeMillis();
            timer.handler.postDelayed(timer.updateTimerThread, 0);
            timer.isRunning = true;
            startButton.setEnabled(false);
            pauseButton.setEnabled(true);
        }*/


        //TODO to be finished but better to wait for the stopwatch to be running as a service first
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //we bound to TimerService, cast the IBinder and get TimerService instance
            TimerService.TimerBinder binder = (TimerService.TimerBinder)service;
            mService = binder.getService();
            mBound = true;
            // Ensure the service is not in the foreground when bound
            mService.background();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };
}
