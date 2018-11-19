package p290810.example.jack.timerproto.model;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;

import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.TextView;

import com.example.jack.timerproto.MainActivity;
import com.example.jack.timerproto.R;

public class Timer{

    public int id;
    public String name;
    public long startTime;
    public long timeInMilliseconds;
    public long timeSwapBuff;
    public long updatedTime;

    public boolean isRunning;

    public Handler handler = new Handler();

    public TextView timerValue;

    public Runnable updateTimerThread = new Runnable(){
        public void run(){
            setTimerValue();
            handler.postDelayed(this, 0);
        }
    };

    public Timer(){
        name = "New Timer";
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;

        isRunning = false;
    }

    public Timer(int timerId){
        id = timerId;
        name = "New Timer";
        startTime = 0L;
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;

        isRunning = false;
    }

    public Timer(boolean isRunning){

        this.isRunning = isRunning;
    }

    public void setTimerValue(){
        timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
        updatedTime = timeSwapBuff + timeInMilliseconds;

        int secs = (int)(updatedTime / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        int milliseconds = (int)(updatedTime%1000);
        String time = "" + mins + ":" + String.format("%02d", secs) +
                ":"  + String.format("%03d", milliseconds);
        timerValue.setText(time);
    }
}