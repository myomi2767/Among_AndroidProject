package com.example.among.calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class EventRecyclerAdapter extends RecyclerView.Adapter<EventRecyclerAdapter.MyViewHolder> {
    Context context;
    ArrayList<Events> arrayList;
    DateHelper dateHelper;
    ImageButton setAlarm;
    public EventRecyclerAdapter(Context context,ArrayList<Events> arrayList){
        this.context = context;
        this.arrayList = arrayList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_rowlayout,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        final Events events = arrayList.get(position);
        holder.Event.setText(events.getEVENT());
        holder.DateTxt.setText(events.getDATE());
        holder.Time.setText(events.getTIME());
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCalendarEvent(events.getEVENT(),events.getDATE(),events.getTIME());
                arrayList.remove(position);
                notifyDataSetChanged();
            }
        });
    if(isAlarmed(events.getDATE(),events.getEVENT(),events.getTIME())){
        holder.setAlarm.setImageResource(R.drawable.alarm);

    }else{
        holder.setAlarm.setImageResource(R.drawable.disablealarm);

    }

    Calendar datecalendar = Calendar.getInstance();
    datecalendar.setTime(ConvertStringToDate(events.getDATE()));
    final int alarmYear = datecalendar.get(Calendar.YEAR);
    final int alarmMonth = datecalendar.get(Calendar.MONTH);
    final int alarmDay = datecalendar.get(Calendar.DAY_OF_MONTH);
    Calendar timecalendar = Calendar.getInstance();
    timecalendar.setTime(ConvertStringToTime(events.getTIME()));
    final int alarmHour = timecalendar.get(Calendar.HOUR_OF_DAY);
    final int alarmMinute = timecalendar.get(Calendar.MINUTE);



    holder.setAlarm.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(isAlarmed(events.getDATE(),events.getEVENT(),events.getTIME())){
                holder.setAlarm.setImageResource(R.drawable.disablealarm);
                cancelAlarm(getRequestCode(events.getDATE(),events.getEVENT(),events.getTIME()));
                updateEvent(events.getDATE(),events.getEVENT(),events.getTIME(),"off");
                notifyDataSetChanged();
            }else{
                holder.setAlarm.setImageResource(R.drawable.alarm);
                Calendar alarmCalendar = Calendar.getInstance();
                alarmCalendar.set(alarmYear,alarmMonth,alarmDay,alarmHour,alarmMinute);
                setAlarm(alarmCalendar,events.getEVENT(),events.getTIME(),getRequestCode(events.getDATE(),
                        events.getEVENT(),events.getTIME()));
                updateEvent(events.getDATE(),events.getEVENT(),events.getTIME(),"on");
                notifyDataSetChanged();
            }
        }
    });
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView DateTxt;
        TextView Event;
        TextView Time;
        Button delete;
        ImageButton setAlarm;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            DateTxt = itemView.findViewById(R.id.eventDate);
            Event = itemView.findViewById(R.id.eventName);
            Time = itemView.findViewById(R.id.eventTime);
            delete = itemView.findViewById(R.id.delete);
            setAlarm = itemView.findViewById(R.id.alarmBtn);

        }
    }
    private Date ConvertStringToDate(String eventDate){
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
        Date date = null;
        try{
            date = format.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    private Date ConvertStringToTime(String eventDate){
        SimpleDateFormat format = new SimpleDateFormat("kk:mm", Locale.KOREA);
        Date date = null;
        try{
            date = format.parse(eventDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
    private void deleteCalendarEvent(String event, String date, String time){
        dateHelper = new DateHelper(context);
        SQLiteDatabase database = dateHelper.getWritableDatabase();
        dateHelper.deleteEvent(event,date,time,database);
        dateHelper.close();
    }
    private boolean isAlarmed(String date,String event,String time){
        boolean alarmed = false;
        dateHelper = new DateHelper(context);
        SQLiteDatabase database = dateHelper.getReadableDatabase();
        Cursor cursor = dateHelper.ReadIDEvents(date,event,time,database);
        while(cursor.moveToNext()){
            String notify = cursor.getString(cursor.getColumnIndex(DBStructure.NOTIFY));
            if(notify.equals("on")){
                alarmed = true;
            }else{
                alarmed = false;
            }
        }
        cursor.close();
        dateHelper.close();
        return alarmed;
    }
    private  void  setAlarm(Calendar calendar, String event, String time, int RequestCode){
        Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
        intent.putExtra("event",event);
        intent.putExtra("time",time);
        intent.putExtra("id",RequestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCode,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }
    private  void  cancelAlarm(int RequestCode){
        Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCode,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
    private int getRequestCode(String date,String event,String time){
        int code = 0;
        dateHelper = new DateHelper(context);
        SQLiteDatabase database = dateHelper.getReadableDatabase();
        Cursor cursor = dateHelper.ReadIDEvents(date,event,time,database);
        while(cursor.moveToNext()){
            code = cursor.getInt(cursor.getColumnIndex(DBStructure.ID));
        }
        cursor.close();
        dateHelper.close();

        return code;
    }
    private void updateEvent(String date,String event,String time,String notify){
        dateHelper = new DateHelper(context);
        SQLiteDatabase database = dateHelper.getWritableDatabase();
        dateHelper.updateEvent(date,event,time,notify,database);
        dateHelper.close();
    }
}
