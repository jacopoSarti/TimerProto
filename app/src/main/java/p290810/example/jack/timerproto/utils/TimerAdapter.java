package p290810.example.jack.timerproto.utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.jack.timerproto.MainActivity;
import com.example.jack.timerproto.R;
import com.example.jack.timerproto.TimerActivity;
import com.example.jack.timerproto.model.Timer;
import com.example.jack.timerproto.services.TimerService;

import java.util.List;

import p290810.example.jack.timerproto.services.TimerService;

public class TimerAdapter extends RecyclerView.Adapter<TimerAdapter.ViewHolder> {

    private AdapterCallbacks mCallbacks;
    private Context mContext;
    private RecyclerView mRecyclerView;
    TimerDBHelper dbHelper;

    private TimerService mService;

    /*
    provide a reference to the view for each data item
    complex data item may need more than one view per item
     */
    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageButton optionsButton;
        public TextView timerName;
        public TextView timerValue;
        public Button startButton;
        public Button pauseButton;

        public View layout;

        public ViewHolder(View v){
            super(v);
            layout = v;
            optionsButton = (ImageButton)v.findViewById(p290810.example.jack.timerproto.R.id.options_menu_icon);
            timerName = (TextView)v.findViewById(p290810.example.jack.timerproto.R.id.timerName);
            timerValue = (TextView)v.findViewById(p290810.example.jack.timerproto.R.id.timerValue);
            startButton = (Button)v.findViewById(p290810.example.jack.timerproto.R.id.startButton);
            pauseButton = (Button)v.findViewById(p290810.example.jack.timerproto.R.id.pauseButton);
        }
    }

    public TimerAdapter(Context context, RecyclerView recyclerView,
                        AdapterCallbacks callbacks){
        mContext = context;
        mRecyclerView = recyclerView;
        dbHelper = new TimerDBHelper(mContext);
        mCallbacks = callbacks;
        mService = mCallbacks.getService();
    }

    //create new views (invoked by the layout manager)
    @Override
    public TimerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){

        //create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(p290810.example.jack.timerproto.R.layout.timer, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    //replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position){
        // - get element from dataset at this position
        // - replace the contents of the view with that element

        View v = holder.layout;
        mService.getTimersList().get(position).timerValue = (TextView)v.findViewById(p290810.example.jack.timerproto.R.id.timerValue);
        holder.timerName.setText(mService.getTimersList().get(position).name);

        long updatedTime = mService.getTimersList().get(position).timeSwapBuff;
        int secs = (int)(updatedTime / 1000);
        int mins = secs / 60;
        secs = secs % 60;
        int milliseconds = (int)(updatedTime%1000);
        String time = "" + mins + ":" + String.format("%02d", secs) +
                ":"  + String.format("%03d", milliseconds);
        mService.getTimersList().get(position).timerValue.setText(time);

        holder.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mService.runTimer(position)){
                    holder.startButton.setEnabled(false);
                    holder.pauseButton.setEnabled(true);
                }
            }
        });

        holder.pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mService.pauseTimer(position)){
                    holder.startButton.setEnabled(true);
                    holder.pauseButton.setEnabled(false);
                }
            }
        });

        holder.optionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(mContext, v);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case p290810.example.jack.timerproto.R.id.edit:
                                Intent intent = new Intent(mContext, TimerActivity.class);
                                intent.putExtra("position", position);
                                mContext.startActivity(intent);
                                return true;
                            case p290810.example.jack.timerproto.R.id.delete:
                                mService.deleteTimer(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, mService.getTimersList().size());
                                return true;
                            case p290810.example.jack.timerproto.R.id.stopwatchHistory:
                                return false;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = popupMenu.getMenuInflater();
                inflater.inflate(p290810.example.jack.timerproto.R.menu.timer_menu, popupMenu.getMenu());
                popupMenu.show();
            }
        });
    }

    @Override
    public int getItemCount(){
        return mService.getTimersList().size();
    }
}