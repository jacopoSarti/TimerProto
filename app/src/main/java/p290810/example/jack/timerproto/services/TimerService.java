package p290810.example.jack.timerproto.services;

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
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.example.jack.timerproto.R;
import com.example.jack.timerproto.model.Timer;
import com.example.jack.timerproto.utils.TimerDBHelper;

import java.util.List;

public class TimerService extends Service {

    // Foreground notification id
    private static final int NOTIFICATION_ID = 1;

    private TimerDBHelper dbHelper;
    private List<Timer> mTimersList;
    private final IBinder mBinder = new TimerBinder();
    private ServiceCallbacks mCallbacks;

    public class TimerBinder extends Binder{
        public TimerService getService(){
            return TimerService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        readAllFromDatabase();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return Service.START_STICKY;

    }


    @Override
    public IBinder onBind(Intent intent){
        return mBinder;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    public void background(){
        stopForeground(true);
    }

    public void foreground(){
        startForeground(NOTIFICATION_ID, createNotification());
    }

    /**
     * Creates a notification for placing the service into the foreground
     *
     * @return a notification for interacting with the service when in the foreground
     */
    private Notification createNotification() {
        NotificationManager mNotifyManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel(mNotifyManager);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, "FileDownload")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setColor
                        (ContextCompat.getColor(this, p290810.example.jack.timerproto.R.color.colorPrimary))
                .setContentTitle("Hello")
                .setContentText("World");
        Notification notification = mBuilder.build();
        mNotifyManager.notify(NOTIFICATION_ID, notification);

        return notification;
    }

    @TargetApi(26)
    private void createChannel(NotificationManager notificationManager) {
        String name = "FileDownload";
        String description = "Notifications for download status";
        int importance = NotificationManager.IMPORTANCE_DEFAULT;

        NotificationChannel mChannel = new NotificationChannel(name, name, importance);
        mChannel.setDescription(description);
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.BLUE);
        notificationManager.createNotificationChannel(mChannel);
    }

        public boolean runTimer(int position){
        Timer timer = mTimersList.get(position);
        if(timer.isRunning == false) {
            timer.startTime = SystemClock.uptimeMillis();
            timer.handler.postDelayed(timer.updateTimerThread, 0);
            timer.isRunning = true;
            mTimersList.set(position, timer);

            return true;
        }

        return false;
    }

    public boolean pauseTimer(int position){
        Timer timer = mTimersList.get(position);
        if(timer.isRunning == true){
            timer.timeSwapBuff += timer.timeInMilliseconds;
            timer.handler.removeCallbacks(timer.updateTimerThread);
            timer.isRunning = false;
            mTimersList.set(position, timer);
            saveTimerToDatabase(timer);

            return true;
        }

        return false;
    }

    public void setCallbacks(ServiceCallbacks callbacks){
        mCallbacks = callbacks;
    }

    public void readAllFromDatabase(){
        dbHelper = new TimerDBHelper(this);
        mTimersList = dbHelper.readAll();
    }

    public Timer addTimer(){
        Timer timer = new Timer();
        dbHelper = new TimerDBHelper(getApplicationContext());
        timer.id = dbHelper.createTimer(timer);
        mTimersList.add(timer);

        return timer;
    }

    public void deleteTimer(int position){
        dbHelper.deleteTimer(mTimersList.get(position));
        mTimersList.remove(position);
    }

    public void saveTimerToDatabase(Timer timer){
        dbHelper.updateTimer(timer);
    }

    public List<Timer> getTimersList(){
        return mTimersList;
    }
}
