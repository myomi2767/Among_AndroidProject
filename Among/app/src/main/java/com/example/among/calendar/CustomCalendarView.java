package com.example.among.calendar;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.among.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class CustomCalendarView extends LinearLayout {
    ImageButton NextButton;
    ImageButton PreviousButton;
    TextView CurrentDate;
    GridView gridView;
    private static final int MAX_CAlENDAR_DAYS =42;
    Calendar calendar = Calendar.getInstance(Locale.KOREA);
    Context context;
    SimpleDateFormat dateFormat = new SimpleDateFormat("YYYY MMMM",Locale.KOREA);
    SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM",Locale.KOREA);
    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy",Locale.KOREA);
    SimpleDateFormat eventDateFormat = new SimpleDateFormat("yyyy-MM-dd",Locale.KOREA);
    MyGridAdapter myGridAdapter;
    AlertDialog alertDialog;
    DateHelper DateHelper;
    List<Date> dates = new ArrayList<>();
    List<Events> eventsList = new ArrayList<>();
    int alarmYear, alarmMonth, alarmDay,alarmHour,alarmMinute;
    public CustomCalendarView(Context context) {
        super(context);
    }

    public CustomCalendarView(final Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        InitializeLayout();
        SetupCalendar();

        PreviousButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,-1);
                SetupCalendar();
            }
        });
        NextButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH,1);
                SetupCalendar();
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                final View addView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_newevent_layout,null);
                final EditText EventName = addView.findViewById(R.id.eventName);
                final TextView EventTime = addView.findViewById(R.id.eventTime);
                ImageButton setTime = addView.findViewById(R.id.setEventBtn);
                final CheckBox alarmSet = addView.findViewById(R.id.alarmSet);
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.setTime(dates.get(position));
                alarmYear =dateCalendar.get(Calendar.YEAR);
                alarmMonth = dateCalendar.get(Calendar.MONTH);
                alarmDay = dateCalendar.get(Calendar.DAY_OF_MONTH);

                Button AddEvent = addView.findViewById(R.id.addevent);
                setTime.setOnClickListener(new OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        int hours = calendar.get(Calendar.HOUR_OF_DAY);
                        int minutes = calendar.get(Calendar.MINUTE);
                        TimePickerDialog timePickerDialog = new TimePickerDialog(addView.getContext(), R.style.Theme_AppCompat_Dialog,
                                new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                        Calendar c = Calendar.getInstance();
                                        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                        c.set(Calendar.MINUTE,minute);
                                        c.setTimeZone(TimeZone.getDefault());
                                        SimpleDateFormat hformat = new SimpleDateFormat("HH:mm a",Locale.KOREA);
                                        String event_time = hformat.format(c.getTime());
                                        EventTime.setText(event_time);
                                        alarmHour = c.get(Calendar.HOUR_OF_DAY);
                                        alarmMinute = c.get(Calendar.MINUTE);

                                    }
                                },hours,minutes,false);
                        timePickerDialog.show();
                    }
                });
                final String date = eventDateFormat.format(dates.get(position));
                final String month = monthFormat.format(dates.get(position));
                final String year = yearFormat.format(dates.get(position));

                AddEvent.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(alarmSet.isChecked()){
                            SaveEvent(EventName.getText().toString(),EventTime.getText().toString(),date,month,year,"on");
                            SetupCalendar();
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(alarmYear,alarmMonth,alarmDay,alarmHour,alarmMinute);
                            setAlarm(calendar,EventName.getText().toString(),EventTime.getText().toString(),getRequestCode(date,
                                    EventName.getText().toString(),EventTime.getText().toString()));
                            alertDialog.dismiss();
                        }else {
                            SaveEvent(EventName.getText().toString(),EventTime.getText().toString(),date,month,year,"off");
                            SetupCalendar();
                            alertDialog.dismiss();
                        }

                    }
                });
                builder.setView(addView);
                alertDialog = builder.create();
                alertDialog.show();
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String date = eventDateFormat.format(dates.get(position));

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View showView = LayoutInflater.from(parent.getContext()).inflate(R.layout.show_events_layout,null);
                RecyclerView recyclerView = showView.findViewById(R.id.EventsList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(showView.getContext());
                recyclerView.setLayoutManager(layoutManager);
                recyclerView.setHasFixedSize(true);
                EventRecyclerAdapter eventRecyclerAdapter = new EventRecyclerAdapter(showView.getContext(),
                        CollectEventByDate(date));
                recyclerView.setAdapter(eventRecyclerAdapter);
                eventRecyclerAdapter.notifyDataSetChanged();

                builder.setView(showView);
                alertDialog = builder.create();
                alertDialog.show();
                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener(){

                    @Override
                    public void onCancel(DialogInterface dialog) {
                        SetupCalendar();
                    }
                } );
                return true;
            }
        });
    }
    private int getRequestCode(String date,String event,String time){
        int code = 0;
        DateHelper = new DateHelper(context);
        SQLiteDatabase database = DateHelper.getReadableDatabase();
        Cursor cursor = DateHelper.ReadIDEvents(date,event,time,database);
        while(cursor.moveToNext()){
            code = cursor.getInt(cursor.getColumnIndex(DBStructure.ID));
        }
        cursor.close();
        DateHelper.close();

        return code;
    }
    private  void  setAlarm(Calendar calendar,String event,String time,int RequestCode){
        Intent intent = new Intent(context.getApplicationContext(),AlarmReceiver.class);
        intent.putExtra("event",event);
        intent.putExtra("time",time);
        intent.putExtra("id",RequestCode);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, RequestCode,intent,PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager)context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(),pendingIntent);
    }
    private ArrayList<Events> CollectEventByDate(String date){
        ArrayList<Events> arrayList = new ArrayList<>();
        DateHelper = new DateHelper(context);
        SQLiteDatabase database = DateHelper.getReadableDatabase();
        Cursor cursor = DateHelper.ReadEvents(date,database);
        while(cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            String Date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            Events events = new Events(event,time,Date,month,Year);
            arrayList.add(events);
        }
        cursor.close();
        DateHelper.close();

        return arrayList;

    }
    public CustomCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    public void SaveEvent(String event,String time,String date, String month, String year,String notify){
        DateHelper = new DateHelper(context);
        SQLiteDatabase database = DateHelper.getWritableDatabase();
        DateHelper.SaveEvent(event,time,date,month,year,notify,database);
        DateHelper.close();
        Toast.makeText(context,"EVENT가 저장되었습니다 !",Toast.LENGTH_SHORT).show();
    }

    private void InitializeLayout(){
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.calendar_layout,this);
        NextButton = view.findViewById(R.id.nextBtn);
        PreviousButton = view.findViewById(R.id.previousBtn);
        CurrentDate = view.findViewById(R.id.currentDate);
        gridView = view.findViewById(R.id.gridView);
    }
    private void SetupCalendar(){
        String currentDate = dateFormat.format(calendar.getTime());
        CurrentDate.setText(currentDate);
        dates.clear();
        Calendar monthCalendar = (Calendar)calendar.clone();
        monthCalendar.set(Calendar.DAY_OF_MONTH,1);
        int FirstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK)-1;
        monthCalendar.add(Calendar.DAY_OF_MONTH,-FirstDayOfMonth);
        CollectEventsPerMonths(monthFormat.format(calendar.getTime()),yearFormat.format(calendar.getTime()));

        while (dates.size()<MAX_CAlENDAR_DAYS){
            dates.add(monthCalendar.getTime());
            monthCalendar.add(Calendar.DAY_OF_MONTH,1);
        }
        myGridAdapter = new MyGridAdapter(context,dates,calendar,eventsList);
        gridView.setAdapter(myGridAdapter);
    }
    private void CollectEventsPerMonths(String Month,String year){
        eventsList.clear();
        DateHelper = new DateHelper(context);
        SQLiteDatabase database = DateHelper.getReadableDatabase();
        Cursor cursor = DateHelper.ReadEventsPerMonth(Month,year,database);
        while(cursor.moveToNext()){
            String event = cursor.getString(cursor.getColumnIndex(DBStructure.EVENT));
            String time = cursor.getString(cursor.getColumnIndex(DBStructure.TIME));
            String date = cursor.getString(cursor.getColumnIndex(DBStructure.DATE));
            String month = cursor.getString(cursor.getColumnIndex(DBStructure.MONTH));
            String Year = cursor.getString(cursor.getColumnIndex(DBStructure.YEAR));
            Events events = new Events(event,time,date,month,Year);
            eventsList.add(events);
        }
        cursor.close();
        DateHelper.close();
    }
}
