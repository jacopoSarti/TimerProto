package p290810.example.jack.timerproto.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jack.timerproto.model.Timer;

import java.util.LinkedList;
import java.util.List;

public class TimerDBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "timerproto.db";
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "Timers";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_STARTTIME = "start_time";
    public static final String COLUMN_ELAPSEDTIME = "elapsed_time";

    public TimerDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(" CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_STARTTIME + " INTEGER, " +
                COLUMN_ELAPSEDTIME + " INTEGER);"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //you can implement here migration process
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    public List<Timer> readAll(){
        List<Timer> timerLinkedList = new LinkedList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        Timer timer;

        if(cursor.moveToFirst()){
            do{
                timer = new Timer(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
                timer.name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                timer.timeSwapBuff = cursor.getInt(cursor.getColumnIndex(COLUMN_ELAPSEDTIME));
                timerLinkedList.add(timer);
            }while(cursor.moveToNext());
        }

        return timerLinkedList;
    }

    public int createTimer(Timer timer){
        int id = 10000;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, timer.name);
        values.put(COLUMN_ELAPSEDTIME, timer.timeSwapBuff);

        // insert
        db.insert(TABLE_NAME,null, values);

        String query = "SELECT " + COLUMN_ID + " FROM " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToLast()){
            id = cursor.getInt(cursor.getColumnIndex(COLUMN_ID));
            db.close();
            return id;
        }

        db.close();
        return id++;

    }

    public void deleteTimer(Timer timer){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=" + timer.id + ";";
        db.execSQL(query);
        db.close();
    }

    public void updateTimer(Timer timer){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + TABLE_NAME +
                " SET " + COLUMN_ELAPSEDTIME + "=" + timer.timeSwapBuff +
                " WHERE " + COLUMN_ID + "=" + timer.id + ";";
        db.execSQL(query);
        db.close();
    }
}
