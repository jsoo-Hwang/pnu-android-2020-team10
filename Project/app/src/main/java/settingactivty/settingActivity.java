package settingactivty;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.jakshim.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.naver.maps.geometry.LatLng;

import database.DbMOpenHelper;
import database.DbOpenHelper;
import mainactivity.MainActivity;
import mapactivity.AlarmSettingPlaceActivity;
import penaltyactivity.MessageActivity;


public class settingActivity extends AppCompatActivity implements View.OnClickListener{

    TextView alarmSetting_dayOfWeek;
    TextView alarmSetting_label;
    TextView alarmSetting_place;
    TextView alarmSetting_penalty;
    EditText alarmSetting_inputLabel;
    LinearLayout linearLayout;
    LinearLayout layout_label;
    LinearLayout layout_penalty;
    LinearLayout layout_btnOne;
    LinearLayout layout_btnTwo;
    LinearLayout layout_location;
    Button store_btnOne;
    Button cancel_btnOne;
    Button store_btnTwo;
    Button cancel_btnTwo;
    Button delete_btnTwo;
    Cursor iCursor;
    Cursor MCursor;
    private static final int MAP_ACTIVITY = 0;
    private static final int MESSAGE_ACTIVITY = 1;
    static LatLng destPosition;

    boolean[] checkedItems = {false, false, false, false, false, false, false};
    private DbOpenHelper mDbOpenHelper;
    private DbMOpenHelper mDbMOpenHelper;
    private String DayofWeek = "";
    private int timepicker_hour;
    private String timepicker_minute;
    private String newTimepicker_hour;
    private String timepicker_ampm;
    private int db_id ;
    String destName = "";
    String labelString = "";
    String tempAlarmID;
    String tempNextDayOfWeek;
    TimePicker setting_timePicker;
    String penalty="";
    int penalty_line ;
    String phoneNumbger = "";
    String message="";
    String desN_desP = "";
    String message_check ="";
    int penalty_choice;

    static final String KEY_DATA = "KEY_DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_setting);


        Log.i("ActivityData","=============enter in Setting Activity===============");

        linearLayout = (LinearLayout)findViewById(R.id.layout_date);
        layout_label = (LinearLayout)findViewById(R.id.layout_label);
        layout_penalty = (LinearLayout)findViewById(R.id.layout_penalty);
        layout_location = (LinearLayout)findViewById(R.id.layout_location);
        alarmSetting_dayOfWeek = (TextView)findViewById(R.id.alarmSetting_dayOfWeeek);

        store_btnOne = (Button)findViewById(R.id.alarmSetting_storeBtnOne);
        store_btnTwo = (Button)findViewById(R.id.alarmSetting_StoreBtnTwo);
        cancel_btnOne = (Button)findViewById(R.id.alarmSetting_btnCancelOne);
        cancel_btnTwo = (Button)findViewById(R.id.alarmSetting_cancelTwo);
        delete_btnTwo = (Button)findViewById(R.id.alarmSetting_deleteTwo);

        alarmSetting_label = (TextView)findViewById(R.id.alarmSetting_labelText);
        alarmSetting_inputLabel = (EditText)findViewById(R.id.addboxdialog_label);
        alarmSetting_penalty = (TextView)findViewById(R.id.alarmSetting_penalty);
        alarmSetting_place = (TextView)findViewById(R.id.alarmSetting_place);

        layout_btnOne = (LinearLayout)findViewById(R.id.alarmSetting_btnLayoutOne);
        layout_btnTwo = (LinearLayout)findViewById(R.id.alarmSetting_btnLayoutTwo);



        linearLayout.setOnClickListener(this);
        layout_label.setOnClickListener(this);
        layout_penalty.setOnClickListener(this);
        store_btnOne.setOnClickListener(this);
        store_btnTwo.setOnClickListener(this);
        cancel_btnOne.setOnClickListener(this);
        cancel_btnTwo.setOnClickListener(this);
        delete_btnTwo.setOnClickListener(this);
        layout_location.setOnClickListener(this);

        if (savedInstanceState != null) {

            String data = savedInstanceState.getString(KEY_DATA);
            Log.i("ActivityData","null : " + data);
            //alarmSetting_label.setText(data);
        }
        else{
            Log.i("ActivityData","else에 들어 왔네");
        }

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    protected Dialog onCreateDialog(int id) {

        // TODO Auto-generated method stub

        final String[] items = {"월", "화", "수", "목", "금", "토", "일"};
        AlertDialog.Builder builder = new AlertDialog.Builder(settingActivity.this);
        builder.setTitle("요일 선택");
//        builder.setMessage("메시지");



        builder.setMultiChoiceItems(items, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                // TODO Auto-generated method stub
                // 바뀐 것을 적용한다.
                checkedItems[which] = isChecked;
            }
        });



        // 같은 onclick을 쓰기때문에 DialogInterface를 적어주어야 에러발생하지 않는다.
        builder.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override

            public void onClick(DialogInterface dialog, int which) {

                // TODO Auto-generated method stub

                // Toast로 현제 체크된 항목 표시하기

                String str = "";
                DayofWeek = "";
                for (int i = 0; i<items.length; i++){
                    Log.i("test","checkedItem : "+checkedItems[i]);
                    if(checkedItems[i]) {
                        str += items[i];
                        DayofWeek += items[i];
                        str += ", ";//수정해야함.
                    }
                }
                str = str.substring(0,str.length()-2);
                Toast.makeText(settingActivity.this, str, Toast.LENGTH_SHORT).show();
                Log.i("test Picker", DayofWeek);
                alarmSetting_dayOfWeek.setText(str);
            }
        });
        return builder.create();
    }

    private void DialogSelectOption() {
        // TODO Auto-generated method stub

        final CharSequence[] items = {"휴대폰 잠금", "예약 문자 발송"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);     // 여기서 this는 Activity의 this

// 여기서 부터는 알림창의 속성 설정

        builder.setTitle("페널티 선택")        // 제목 설정
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

                    // 목록 클릭시 설정
                    public void onClick(DialogInterface dialog, int index) {
                        Log.i("INDEX","test : " + index);
                        penalty_choice = index;

                    }

                });
        builder.setPositiveButton("저장",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int index) {
                        if(penalty_choice == 0){
                            Log.i("INDEX","intent : " + index);

                            Toast.makeText(settingActivity.this, "휴대폰 잠금 패널티로 설정 됐습니다.", Toast.LENGTH_SHORT).show();
                            penalty = "L";

                        }
                        else if(penalty_choice == 1) {
                            Toast.makeText(settingActivity.this, "문자 패널티 화면으로 넘어갑니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(settingActivity.this, MessageActivity.class);
                            intent.putExtra("db_id", db_id);

                            intent.putExtra("pL",penalty_line);
                            startActivityForResult(intent, MESSAGE_ACTIVITY);
                        }

                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

        AlertDialog dialog = builder.create();    // 알림창 객체 생성
        dialog.show();    // 알림창 띄우기

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_date :
                onCreateDialog(1).show();
                break;

            case R.id.layout_penalty :
                DialogSelectOption();
                break;

            case R.id.layout_label :
                label_dialog();
                break;
            case R.id.alarmSetting_storeBtnOne :
                UpdateData();
                break;
            case R.id.alarmSetting_StoreBtnTwo :
                UpdateData();
                break;
            case R.id.alarmSetting_btnCancelOne :
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.alarmSetting_cancelTwo :
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.alarmSetting_deleteTwo :
                iCursor.moveToPosition(db_id);
                mDbOpenHelper.deleteColumn(iCursor.getInt(iCursor.getColumnIndex("_id")));
                MCursor.moveToPosition(penalty_line);
                mDbMOpenHelper.deleteColumn(MCursor.getInt(MCursor.getColumnIndex("_id")));
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.layout_location :
                Log.i("penalty_line","penalty_line after in place : "+penalty_line);
                intent = new Intent(this, AlarmSettingPlaceActivity.class);
                intent.putExtra("db_id", db_id);
                intent.putExtra("pL", penalty_line);
                if(destName.compareTo("")==0){
                    destPosition = new LatLng( 0, 0);
                    destName = "";
                }
                intent.putExtra("destLatitude", destPosition.latitude);
                intent.putExtra("destLongitude", destPosition.longitude);
                intent.putExtra("destName", destName);
                //intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, MAP_ACTIVITY);
                break;

        }
    }
    void label_dialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(settingActivity.this);
        final EditText et = new EditText(settingActivity.this);
        et.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        //et.setPadding(20, 10, 10, 10);


        builder.setTitle("라벨 설정");
        builder.setView(et);
        builder.setPositiveButton("저장",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.i("eeeee","dddd"+et.getText().toString());
                        Toast.makeText(getApplicationContext(),et.getText().toString() ,Toast.LENGTH_SHORT).show();
                        labelString = et.getText().toString();
                        alarmSetting_label.setText(labelString);
                    }
                });
        builder.setNegativeButton("취소",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

    void UpdateData(){
        Log.i("DBtest","SettingActivity enter in updateData");
        //mDbOpenHelper.deleteAllColumns();//테스트할때 계속 쌓여서 삭제하고 진행
        setting_timePicker = (TimePicker) findViewById(R.id.tp);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            timepicker_hour = setting_timePicker.getHour();
            timepicker_minute = setting_timePicker.getMinute() + "";
            Log.i("timeTest","hour : "+timepicker_hour + " minute : "+timepicker_minute);
        } else {
            timepicker_hour = setting_timePicker.getCurrentHour();
            timepicker_minute = setting_timePicker.getCurrentMinute()+ "";
        }
        //Log.i("DBtest","SettingActivity enter in updateData1");
        //am pm
        timepicker_ampm= "AM";
        if(timepicker_hour > 11) timepicker_ampm = "PM";
        if(timepicker_hour > 11) newTimepicker_hour = (timepicker_hour - 12) + "";
        else newTimepicker_hour = (timepicker_hour) + "";
        //Log.i("DBtest","SettingActivity enter in updateData2");
        if(db_id>=0){
            iCursor.moveToPosition(db_id);
            MCursor.moveToPosition(penalty_line);
            Log.i("DBtest","labelString" + labelString + " " + db_id + " " + iCursor.getPosition());
            mDbOpenHelper.updateColumn(iCursor.getInt(iCursor.getColumnIndex("_id")), labelString , newTimepicker_hour, timepicker_minute,
                    timepicker_ampm, DayofWeek, desN_desP, penalty, Integer.toString(penalty_line), "ON");
            Toast.makeText(getApplicationContext(), "수정됐습니다.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else {
            if (labelString.compareTo("")==0|| DayofWeek.compareTo("")==0 || penalty.compareTo("")==0 || desN_desP.compareTo("")==0)
                Toast.makeText(getApplicationContext(), "모든 정보를 입력해주세요.", Toast.LENGTH_SHORT).show();

            else {
                mDbOpenHelper.insertColumn(labelString, newTimepicker_hour, timepicker_minute, timepicker_ampm, DayofWeek, desN_desP, penalty, Integer.toString(penalty_line),"ON");//사용자가 설정한 시간
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "저장됐습니다.", Toast.LENGTH_SHORT).show();
            }

        }

    }

    @Override
    public void onResume() {

        //안드로이드 구조를 새로 수정요망
        super.onResume();
        //mDbMOpenHelper.deleteAllColumns();
        Intent intent = getIntent();
        if(intent.getExtras().getInt("db_id")<0){
            db_id = -1;
        }


        mDbOpenHelper = new DbOpenHelper(getApplicationContext());
        mDbOpenHelper.open();
        iCursor = mDbOpenHelper.selectColumns();

        mDbMOpenHelper = new DbMOpenHelper(getApplicationContext());
        mDbMOpenHelper.open();
        MCursor = mDbMOpenHelper.selectColumns();





        if(db_id >= 0){
            iCursor.moveToPosition(db_id);
            layout_btnTwo.setVisibility(View.VISIBLE);
            layout_btnOne.setVisibility(View.GONE);
            tempAlarmID = iCursor.getString(iCursor.getColumnIndex("alarmId"));
            labelString = iCursor.getString(iCursor.getColumnIndex("alarmId"));
            tempNextDayOfWeek = iCursor.getString(iCursor.getColumnIndex("nextdayofweek"));
            String temp_dayOfWeek="";
            alarmSetting_label.setText(tempAlarmID);
            for(int i=0;i<tempNextDayOfWeek.length();++i){
                if(i == tempNextDayOfWeek.length()-1) temp_dayOfWeek = temp_dayOfWeek + tempNextDayOfWeek.substring(i,i+1);
                else temp_dayOfWeek = temp_dayOfWeek + tempNextDayOfWeek.substring(i,i+1) + ",";
            }
            alarmSetting_dayOfWeek.setText(temp_dayOfWeek);
            DayofWeek = tempNextDayOfWeek;
            penalty_line = iCursor.getInt(iCursor.getColumnIndex("penaltyline"));

        }
        else{
            layout_btnTwo.setVisibility(View.GONE);
            layout_btnOne.setVisibility(View.VISIBLE);
        }


        if(penalty.compareTo("M")==0){
            alarmSetting_penalty.setText("예약 문자 발송");
            if(penalty_line>=0) {
                Log.i("penalty_line","penalty_line in compare: " + penalty_line);
                MCursor.moveToPosition(penalty_line);

                //penalty = MCursor.getString(MCursor.getColumnIndex("PHONE"));
                phoneNumbger = MCursor.getString(MCursor.getColumnIndex("phonenumber"));
                message = MCursor.getString(MCursor.getColumnIndex("message"));

                Log.i("MDB", "phone : " + phoneNumbger + " Message : " + message + MCursor.getPosition());

            }
        }
        else if(penalty.compareTo("L")==0){
            alarmSetting_penalty.setText("휴대폰 잠금");
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        mDbOpenHelper.close();
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {



        super.onSaveInstanceState(savedInstanceState);

        String data = alarmSetting_label.getText().toString();
        Log.i("ActivityData","data : "+data);
        savedInstanceState.putString(KEY_DATA, data);


    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // 추가로 자료를 복원하는 코드는 여기에 작성하세요.
        Log.i("ActivityData","Restore");
        String myString = savedInstanceState.getString(KEY_DATA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        switch(requestCode) {
            case (MAP_ACTIVITY) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.

                    db_id = intent.getExtras().getInt("db_id");
                    penalty_line = intent.getExtras().getInt("pL");
                    double lat = intent.getExtras().getDouble("destLatitude");
                    double lon = intent.getExtras().getDouble("destLongitude");


                    destName = intent.getExtras().getString("destName");

                    if(destName.compareTo("")!=0){
                        destPosition = new LatLng( lat,lon);
                    }
                    alarmSetting_place.setText(destName);
                    Log.i("penalty_line","new : "+ destName);
                    desN_desP  = destName + "/" + destPosition.latitude + "/" + destPosition.longitude;
                    Log.i("destTest","test 111: " + desN_desP);
                }
                break;
            }

            case (MESSAGE_ACTIVITY) : {
                if (resultCode == Activity.RESULT_OK) {
                    // TODO Extract the data returned from the child Activity.
                    db_id = intent.getExtras().getInt("db_id");
                    penalty_line = intent.getExtras().getInt("pL");
                    Log.i("penalty_line","penalty_line : " +penalty_line );
                    message_check = intent.getExtras().getString("check");
                    if(message_check.compareTo("y") == 0){
                        penalty = "M";
                    }
                    else{
                        penalty = "";
                    }
                    Log.i("youwan","new : ");
                }
                break;
            }
        }
    }
}