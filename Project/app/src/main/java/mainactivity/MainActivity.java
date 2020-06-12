package mainactivity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jakshim.R;
//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import database.DbOpenHelper;
import mypageactivity.MyPageActivity;
import settingactivty.settingActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private DbOpenHelper mDbOpenHelper;

    private static Context mContext;

    ArrayList<alarmsItem> alarms_list;
    alarmsItem alarms;
    ImageView mainActivity_plusBtn;
    ImageView mainActivity_mypage;

    private static final int PERMISSION_REQUEST_CODE = 1;


    RecyclerView alarmsItem_recyclerView;
    alarms_adapter alarmItems_adapter;

    Cursor iCursor;
    Calendar calendar;


    String tempHour;
    String tempMinute;

    int pos;

    double longitude;
    double latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("DBtest","==========Enter in MainAtivity=======");
        setContentView(R.layout.main_activity);
        mainActivity_plusBtn = (ImageView)findViewById(R.id.mainActivity_plusBtn);
        mainActivity_mypage = (ImageView)findViewById(R.id.mainActivity_mypage);

        mainActivity_plusBtn.setOnClickListener(this);
        mainActivity_mypage.setOnClickListener(this);

        alarms_list = new ArrayList<alarmsItem>();//리싸이클러뷰 아이템 여기서 저장하고 옮김

        mContext = this;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

            if (checkSelfPermission(Manifest.permission.SEND_SMS)
                    == PackageManager.PERMISSION_DENIED) {

                Log.d("permission", "permission denied to SEND_SMS - requesting it");
                String[] permissions = {Manifest.permission.SEND_SMS};

                requestPermissions(permissions, PERMISSION_REQUEST_CODE);

            }
        }

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        /*



                <activity
            android:name="com.google.android.gms.ads.AdActivity"
            android:configChanges="keyboard|keyboardHidden|orientation|screenLayout|uiMode|screenSize|smallestScreenSize"
            android:theme="@android:style/Theme.Translucent" />



        */




/*
        나중에 참고하면 좋을듯 일단 남겨둠
        if(tempAlarmID.equals("database youwan")){
            String Result = tempAlarmID + "," +temptime + "," + tempDayOfWeek;
            Log.i("TEST",Result);
        }
        */

    }

    final LocationListener gpsLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {

            String provider = location.getProvider();
            longitude = location.getLongitude();
            latitude = location.getLatitude();


            Log.i("destTest","latitude : " + latitude +" longitude : " + longitude);

            alarms_list.clear();//매번 삭제 해줘야 새로운 데이터를 가지고 올수있다. 아니면 안가져와짐,,,,,시발거
            mDbOpenHelper = new DbOpenHelper(MainActivity.this);
            mDbOpenHelper.open();
            //mDbOpenHelper.deleteAllColumns();//테스트할때 계속 쌓여서 삭제하고 진행
            iCursor = mDbOpenHelper.selectColumns();

        /* 열 알아내는 코드
        String[] columnNames = iCursor.getColumnNames();
        if(columnNames!=null){
            for( int i=0;i<columnNames.length;++i){
                Log.i("columnNames", "columnNames" + columnNames[i]);
            }
        }
        */

            final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            while(iCursor.moveToNext()){
                //substring을 이용해서 자를수있다. 매개변수부터 자름
                //지금 드는 생각은 먼저 총 데이터를 0번째와 나머지로 잘라서 하나는 dayofWeek에 하나는 nextdayofweek에 저장하고 관린한다.
                alarms = new alarmsItem();//매번 새롭게 할당해야지 덮어쓰기가 안됨. 이거없으면 계속 덮어써짐
                Log.i("DBtest","Dbtest : enter in icursor while sentence");

                String tempAlarmID = iCursor.getString(iCursor.getColumnIndex("alarmId"));
                tempHour = iCursor.getString(iCursor.getColumnIndex("hours"));
                tempMinute= iCursor.getString(iCursor.getColumnIndex("minute"));
                //String tempDayOfWeek = iCursor.getString(iCursor.getColumnIndex("dayOfWeek"));
                String tempDayOfAmPm = iCursor.getString(iCursor.getColumnIndex("dayofampm"));
                String tempNextDayOfWeek = iCursor.getString(iCursor.getColumnIndex("nextdayofweek"));
                Log.i("DBtest","iCursor : " + iCursor.getInt(iCursor.getColumnIndex("_id")) + tempAlarmID);//이걸로 하면 가능  iCursor.getInt(iCursor.getColumnIndex("_id"))

                String temptime = tempHour +"시 " +tempMinute +"분";
                //int position = iCursor.getPosition();
                Log.i("Time_test","nextdayofweek : " + tempNextDayOfWeek);
                int remain_time = compareToRealTime(Integer.parseInt(tempHour),Integer.parseInt(tempMinute),tempDayOfAmPm,tempNextDayOfWeek) * 1000;
                Log.i("Time_test : ","remain_time  : "  + remain_time);
                String onOff = iCursor.getString(iCursor.getColumnIndex("onoff"));
                boolean real_onoff;
                if(onOff.compareTo("ON")==0){
                    real_onoff = true;
                }
                else{
                    real_onoff = false;
                }
                String place = iCursor.getString(iCursor.getColumnIndex("place"));


                alarms.setAmPm(tempDayOfAmPm);
                alarms.setHours(Integer.parseInt(tempHour));
                alarms.setMinutes(Integer.parseInt(tempMinute));
                alarms.setAlarms_names(tempAlarmID);
                alarms.setCountDown(remain_time);
                alarms.setTime(temptime);
                alarms.setAlarms_OnOff(real_onoff);
                alarms.setNextDayOfWeek(tempNextDayOfWeek);
                alarms.setDataBase_id(iCursor.getPosition());

                alarms_list.add(alarms);


            /*나중에 참고하면 좋을듯 일단 남겨둠
            if(tempAlarmID.equals("database youwan")){
                String Result = tempAlarmID + "," +temptime + "," + tempDayOfWeek;
                Log.i("TEST",Result);
            }
            */

            }

            alarmsItem_recyclerView = findViewById(R.id.alarmsItem_recyclerView) ;
            alarmsItem_recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this)) ;
            alarmItems_adapter = new alarms_adapter(alarms_list, MainActivity.this,latitude,longitude);
            alarmsItem_recyclerView.setAdapter(alarmItems_adapter);


        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };



    //년도 날짜 비교해서 숫자 출력해주는 함수 커운트 해주는 거랑 연동해서 진행 예정
    public int compareToRealTime(int User_hours, int User_minutes, String User_ampm, String total_dayOfWeek){
        calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int date = calendar.get(Calendar.DATE);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int am_pm = calendar.get(Calendar.AM_PM);

        //Weeklist에 모두 담는걸 성공함
        List<DayofWeek_container> Weeklist = new ArrayList<DayofWeek_container>();
        Log.i("test","total" + total_dayOfWeek);
        for(int i=0;i<total_dayOfWeek.length();++i){
            DayofWeek_container dayofWeek_container = new DayofWeek_container(total_dayOfWeek.substring(i,i+1));
            Weeklist.add(dayofWeek_container);
            //Log.i("test","java substring test11" + total_dayOfWeek.substring(i,i));
            //Log.i("test","java substring test" + Weeklist.get(i));
            Log.i("test","java substring test22 " + Weeklist.get(i).getString_dqyOfWeek());
        }


        Log.i("timecheck","Hour : " + hour + " minute : " + minute + " second : " + second);

        char korDayOfWeek = ' ';
        char realDayofWeek = ' ';

        switch (dayOfWeek){
            case 1:
                korDayOfWeek = 'g';//일
                realDayofWeek = '일';
                break;
            case 2:
                korDayOfWeek = 'a';//월
                realDayofWeek = '월';
                break;
            case 3:
                korDayOfWeek = 'b';//화
                realDayofWeek = '화';
                break;
            case 4:
                korDayOfWeek = 'c';//수
                realDayofWeek = '수';
                break;
            case 5:
                korDayOfWeek = 'd';//목
                realDayofWeek = '목';
                break;
            case 6:
                korDayOfWeek = 'e';//금
                realDayofWeek = '금';
                break;
            case 7:
                korDayOfWeek = 'f';//토
                realDayofWeek = '토';
                break;
        }
        //--------여기까지 핸드폰 날짜 시간 계산---------------------//

        char user_day = ' ';
        int[] dayOfWeek_list = new int[7];
        Log.i("bugcheck","youwan" + Weeklist.size());

        int w=0;
        while(w < Weeklist.size()) {
            switch (Weeklist.get(w).getString_dqyOfWeek()) {
                case "일":
                    Weeklist.get(w).setChar_dayOfWeek('g');//일
                    break;
                case "월":
                    Weeklist.get(w).setChar_dayOfWeek('a');//월
                    break;
                case "화":
                    Weeklist.get(w).setChar_dayOfWeek('b');//화
                    break;
                case "수":
                    Weeklist.get(w).setChar_dayOfWeek('c');//수
                    break;
                case "목":
                    Weeklist.get(w).setChar_dayOfWeek('d');//목
                    break;
                case "금":
                    Weeklist.get(w).setChar_dayOfWeek('e');//금
                    break;
                case "토":
                    Weeklist.get(w).setChar_dayOfWeek('f');//토
                    break;
            }
            w++;
        }

        //받은 순서대로 conven 시킨거 입력 받았고...

        //유저가 정한 요일이 기준이 아닌 핸드폰 요일을 기준으로 다시 설정 해야한다.
        //핸드폰 요일이 가장 먼저 오겠끔 만든다....유완아 집중해서 바로 끝내자 제발
        char temp_handphone = korDayOfWeek;//핸드폰 기준 요일임
        char sunday = 'g';
        char monday = 'a';
        //핸드폰이 수요일이면 수 목 금 토 일 월 화 순으로 멀리 배치한다.
        int i;
        for(i = 0; i <7 ; ++i) {
            dayOfWeek_list[i] = temp_handphone;
            if(temp_handphone == sunday)break;
            temp_handphone++;

        }
        //수요일 기준으로 일 토 금
        for(int j = i+1; j < 7 ; ++j) {
            Log.i("TimeTest","what");
            dayOfWeek_list[j] = monday++;
        }
        for(int m = 0;m<dayOfWeek_list.length;++m){
            Log.i("DayTest",dayOfWeek_list[m] + " ");
        }
        //현재 요일을 기준으로 요일을 재배치한다. 제대로 했고
        //지금 테스트 시기가 일요일이니깐 일요일 기준으로 일 월 화 수 목 금 토 일 순으로 만들었다.

        int user_time=0;
        int now_time=0;
        int remain_time = 0;
        int input_time = 1000000000;
        int now=0;
        int x = 0;
        int min;
        for(int t=0;t<dayOfWeek_list.length;++t){
            if(dayOfWeek_list[t] == korDayOfWeek) {
                now = t;
                break;
            }

        }
        Log.i("DayTest","now : "+now + User_ampm);
        //now는 현재 요일 인데 굳이 상관없지않나????
        while(x<Weeklist.size()) {
            int m;
            Log.i("DayTest","Weeklist.get(x).getChar_dayOfWeek()"+Weeklist.get(x).getChar_dayOfWeek());
            for(m=0;m<7 && dayOfWeek_list[m] != Weeklist.get(x).getChar_dayOfWeek();++m);
            Log.i("DayTest","check : "+ m );
            if(User_ampm.compareTo("PM")==0) {
                user_time = m * 24 * 60 * 60 + (User_hours + 12) * 60 * 60 + User_minutes * 60;
                Log.i("TimeTest","설마???");
            }
            else
                user_time = m*24*60*60 + (User_hours+0)*60*60 + User_minutes*60;
            Log.i("TimeTest","user hour : "+ User_hours +" user minute : "+ User_minutes + " userTime : " + user_time);
            if(am_pm == 1)
                now_time = now*24*60*60 + (hour+12)*60*60 + minute*60 + second;
            else
                now_time = now*24*60*60 + (hour+0)*60*60 + minute*60 + second;
            Log.i("TimeTest","now hour : "+ hour +"now minute : "+ minute+ " now_time : " + now_time);

            remain_time = user_time - now_time;
            if(remain_time < 0) remain_time = 60*60*24*7 + now_time - user_time;

            Log.i("DayTest","remain : " + remain_time);

            Weeklist.get(x).setInt_dayOfweek(remain_time);
            x++;
        }

        min = Weeklist.get(0).getInt_dayOfweek();
        DayofWeek_container temp_dayofWeek;

        for(int c = 1 ; c < Weeklist.size() ; c++){
            if(Weeklist.get(c).getInt_dayOfweek() < min){
                min = Weeklist.get(c).getInt_dayOfweek();
                Log.i("TimeTest", "min : " + min);
                temp_dayofWeek = Weeklist.get(c);
            }
        }

        //오전 오후에 따라서 확인한다.
        //mDbOpenHelper.updateColumn()

        Log.i("timecheck2","remain_time : " + remain_time + " user_time : " + user_time + " now_time : " + now_time);
        //Log.i("test",year + " 년 "  + month + " 월 " + date + " 일 " + realDayofWeek +" 요일 " +" " + am_pm  +" am_pm "+hour +" 시 " + minute + " 분 " + second + " 초" + "remain_time : " + remain_time);

        return min;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.mainActivity_plusBtn :
                Intent intent = new Intent(this, settingActivity.class);
                intent.putExtra("db_id",-2);
                intent.putExtra("pL",-2);
                intent.putExtra("check_new","new");
                startActivity(intent);
                break;
            case R.id.mainActivity_mypage :
                Intent my_intent = new Intent(this, MyPageActivity.class);
                startActivity(my_intent);
        }


    }
    @Override
    public void onResume() {
        super.onResume();
        //mDbOpenHelper.open();
        Log.i("DBtest","Dbtest : MainActivity Enter in onResume");
        //Log.i("Dbtest","MainActivity onResume : seletColumns" + mDbOpenHelper.selectColumns());
        alarms_list.clear();//매번 삭제 해줘야 새로운 데이터를 가지고 올수있다. 아니면 안가져와짐,,,,,시발거
        mDbOpenHelper = new DbOpenHelper(this);
        mDbOpenHelper.open();
        //mDbOpenHelper.deleteAllColumns();//테스트할때 계속 쌓여서 삭제하고 진행
        iCursor = mDbOpenHelper.selectColumns();

        /* 열 알아내는 코드
        String[] columnNames = iCursor.getColumnNames();
        if(columnNames!=null){
            for( int i=0;i<columnNames.length;++i){
                Log.i("columnNames", "columnNames" + columnNames[i]);
            }
        }
        */

        final LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission( getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions( MainActivity.this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },
                    0 );
        }
        else {
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            String provider = location.getProvider();
            longitude = location.getLongitude();
            latitude = location.getLatitude();

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    10000,
                    10,
                    gpsLocationListener);

            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                    10000,
                    10,
                    gpsLocationListener);

        }

        while(iCursor.moveToNext()){
            //substring을 이용해서 자를수있다. 매개변수부터 자름
            //지금 드는 생각은 먼저 총 데이터를 0번째와 나머지로 잘라서 하나는 dayofWeek에 하나는 nextdayofweek에 저장하고 관린한다.
            alarms = new alarmsItem();//매번 새롭게 할당해야지 덮어쓰기가 안됨. 이거없으면 계속 덮어써짐
            Log.i("DBtest","Dbtest : enter in icursor while sentence");

            String tempAlarmID = iCursor.getString(iCursor.getColumnIndex("alarmId"));
            tempHour = iCursor.getString(iCursor.getColumnIndex("hours"));
            tempMinute= iCursor.getString(iCursor.getColumnIndex("minute"));
            //String tempDayOfWeek = iCursor.getString(iCursor.getColumnIndex("dayOfWeek"));
            String tempDayOfAmPm = iCursor.getString(iCursor.getColumnIndex("dayofampm"));
            String tempNextDayOfWeek = iCursor.getString(iCursor.getColumnIndex("nextdayofweek"));
            Log.i("DBtest","iCursor : " + iCursor.getInt(iCursor.getColumnIndex("_id")) + tempAlarmID);//이걸로 하면 가능  iCursor.getInt(iCursor.getColumnIndex("_id"))

            String temptime = tempHour +"시 " +tempMinute +"분";
            //int position = iCursor.getPosition();
            Log.i("Time_test","nextdayofweek : " + tempNextDayOfWeek);
            int remain_time = compareToRealTime(Integer.parseInt(tempHour),Integer.parseInt(tempMinute),tempDayOfAmPm,tempNextDayOfWeek) * 1000;
            Log.i("Time_test : ","remain_time  : "  + remain_time);
            String onOff = iCursor.getString(iCursor.getColumnIndex("onoff"));
            boolean real_onoff;
            if(onOff.compareTo("ON")==0){
                real_onoff = true;
            }
            else{
                real_onoff = false;
            }


            alarms.setAmPm(tempDayOfAmPm);
            alarms.setHours(Integer.parseInt(tempHour));
            alarms.setMinutes(Integer.parseInt(tempMinute));
            alarms.setAlarms_names(tempAlarmID);
            alarms.setCountDown(remain_time);
            alarms.setTime(temptime);
            alarms.setAlarms_OnOff(real_onoff);
            alarms.setNextDayOfWeek(tempNextDayOfWeek);
            alarms.setDataBase_id(iCursor.getPosition());

            alarms_list.add(alarms);


            /*나중에 참고하면 좋을듯 일단 남겨둠
            if(tempAlarmID.equals("database youwan")){
                String Result = tempAlarmID + "," +temptime + "," + tempDayOfWeek;
                Log.i("TEST",Result);
            }
            */

        }

        alarmsItem_recyclerView = findViewById(R.id.alarmsItem_recyclerView) ;
        alarmsItem_recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;
        alarmItems_adapter = new alarms_adapter(alarms_list, this,latitude,longitude);
        alarmsItem_recyclerView.setAdapter(alarmItems_adapter);




    }

    @Override
    public void onPause(){
        super.onPause();
        //mDbOpenHelper.deleteAllColumns();//테스트할때 계속 쌓여서 삭제하고 진행
        mDbOpenHelper.close();
    }


    public static class AlarmHATT {
        private Context context;

        public AlarmHATT(Context context) {
            this.context = context;
        }

        public void Alarm(int h , int m) {
            AlarmManager am = (AlarmManager) mContext.getSystemService(mContext.ALARM_SERVICE);
            Intent intent = new Intent(mContext, BroadcastD.class);

            PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            //알람시간 calendar에 set해주기
            Log.i("alarmTest", "ENTER F");
            m = m -15;

            if(m-15<0){
                h=h-1;
                m = 60 + (m-15);
            }

            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), h, m, 0);
            Log.i("alarmTest","calendar.getTimeInMillis() : " + calendar.getTimeInMillis());

            //알람 예약
            am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
        }
    }

    public void sendSMS(String phoneNumber, String message) {
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";

        PendingIntent sentPI = PendingIntent.getBroadcast(this, 0,
                new Intent(SENT), 0);

        PendingIntent deliveredPI = PendingIntent.getBroadcast(this, 0,
                new Intent(DELIVERED), 0);

        //---when the SMS has been sent---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "목표시간까지 도착하지 못할 시 예약문자가 발송됩니다.",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        Toast.makeText(getBaseContext(), "Generic failure",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        Toast.makeText(getBaseContext(), "No service",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        Toast.makeText(getBaseContext(), "Null PDU",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        Toast.makeText(getBaseContext(), "Radio off",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(SENT));

        //---when the SMS has been delivered---
        registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        Toast.makeText(getBaseContext(), "SMS delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                    case Activity.RESULT_CANCELED:
                        Toast.makeText(getBaseContext(), "SMS not delivered",
                                Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        }, new IntentFilter(DELIVERED));

        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);
    }


}
