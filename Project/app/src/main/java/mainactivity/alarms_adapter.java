package mainactivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Build;
import android.os.CountDownTimer;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.recyclerview.widget.RecyclerView;

import com.example.jakshim.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import database.DbMOpenHelper;
import database.DbOpenHelper;
import penaltyactivity.LockActivity;
import settingactivty.settingActivity;


public class alarms_adapter extends RecyclerView.Adapter<alarms_adapter.ViewHolder> {

    private ArrayList<alarmsItem> alarms_list = null ;
    Map<Integer, CountDownTimer> timerMap = new HashMap<Integer, CountDownTimer>();
    private int remainMap[] = new int[10];
    private  int standard_time = 60*60*24*1000;
    private MainActivity mainActivity;
    public ClickListener clickListener;
    private DbOpenHelper mDbOpenHelper;
    private DbMOpenHelper mDbMOpenHelper;


    Cursor iCursor;
    Cursor MCursor;

    private int hours;
    private int mins;
    private int db_id;

    double Lat;
    double Lon;



    alarms_adapter(ArrayList<alarmsItem> list, MainActivity mainActivity,double lat, double lon) {
        this.mainActivity = mainActivity;
        alarms_list = list ;
        Lat = lat;
        Lon = lon;

    }


    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public alarms_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.alarm_items, parent, false) ;
        alarms_adapter.ViewHolder vh = new alarms_adapter.ViewHolder(view) ;

        return vh ;
    }

    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {



        Log.i("destTest","latitude111 : " + Lat +" longitude1 : " + Lon);

        Log.i("test","onBindviewholder : MAIN   " + "position : " + position );
        mDbOpenHelper = new DbOpenHelper(mainActivity.getApplicationContext());
        mDbOpenHelper.open();
        iCursor = mDbOpenHelper.selectColumns();
        iCursor.moveToPosition(db_id);


        mDbMOpenHelper = new DbMOpenHelper(mainActivity.getApplicationContext());
        mDbMOpenHelper.open();
        MCursor = mDbMOpenHelper.selectColumns();

        String place = iCursor.getString(iCursor.getColumnIndex("place"));
        String place_db[] = place.split("/");
        Log.i("destTest","test : " + place + " "+ place_db[1] + " " + place_db[2]);

        double lat_db = Double.parseDouble(place_db[1]);
        double lon_db = Double.parseDouble(place_db[2]);

        double distance;

        Location locationA = new Location("point A");

        locationA.setLatitude(lat_db);
        locationA.setLongitude(lon_db);

        Location locationB = new Location("point B");

        locationB.setLatitude(Lat);
        locationB.setLongitude(Lon);

        distance = locationA.distanceTo(locationB);
        Log.i("destTest","lat lon : " + lat_db + " "+ lon_db + " "+ Lat + " "+ Lon + " ");
        Log.i("destTest","distance : " + distance);


        db_id = alarms_list.get(position).getDataBase_id();
        Log.i("DBID","db_id" + db_id);
        holder.alarmsItems_name.setText(alarms_list.get(position).getAlarms_names());//알람 이름이니깐 바로 적용
        holder.alarmsItem_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainActivity, settingActivity.class);
                intent.putExtra("db_id",alarms_list.get(position).getDataBase_id());
                intent.putExtra("pL",iCursor.getString(iCursor.getColumnIndex("penaltyline")));
                //intent.putExtra("pl",alarms_list.get(position).getDataBase_id());

                mainActivity.startActivity(intent);
                //데이터를 넘길때 필요한것들 일단 지금 가지고있는 alarms_list를 보내면 제일 편하겠지 거기다가 추가적으로 db 번호까지 보내면 가장 좋음 애초에 db번호를 alarm_list에 저장하자.
            }
        });


        holder.alarmsItem_checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db_id = alarms_list.get(position).getDataBase_id();
                iCursor.moveToPosition(db_id);
                Log.i("DBID","db_id" + db_id);
                if(alarms_list.get(position).getAlarms_OnOff()) {
                    Toast.makeText(mainActivity.getApplicationContext(), "당신의 의지는 고작 이정도??", Toast.LENGTH_SHORT).show();
                    alarms_list.get(position).setAlarms_OnOff(false);
                    mDbOpenHelper.updateColumn(iCursor.getInt(iCursor.getColumnIndex("_id")),
                            iCursor.getString(iCursor.getColumnIndex("alarmId")) , iCursor.getString(iCursor.getColumnIndex("hours")),
                            iCursor.getString(iCursor.getColumnIndex("minute")), iCursor.getString(iCursor.getColumnIndex("dayofampm")),
                            iCursor.getString(iCursor.getColumnIndex("nextdayofweek")), iCursor.getString(iCursor.getColumnIndex("place")),
                            iCursor.getString(iCursor.getColumnIndex("penalty")),iCursor.getString(iCursor.getColumnIndex("penaltyline")),"OFF");
                }
                else {
                    Toast.makeText(mainActivity.getApplicationContext(), "굳!! 돌아온걸 환영합니다!!", Toast.LENGTH_SHORT).show();
                    alarms_list.get(position).setAlarms_OnOff(true);
                    mDbOpenHelper.updateColumn(iCursor.getInt(iCursor.getColumnIndex("_id")),
                            iCursor.getString(iCursor.getColumnIndex("alarmId")) , iCursor.getString(iCursor.getColumnIndex("hours")),
                            iCursor.getString(iCursor.getColumnIndex("minute")), iCursor.getString(iCursor.getColumnIndex("dayofampm")),
                            iCursor.getString(iCursor.getColumnIndex("nextdayofweek")), iCursor.getString(iCursor.getColumnIndex("place")),
                            iCursor.getString(iCursor.getColumnIndex("penalty")),iCursor.getString(iCursor.getColumnIndex("penaltyline")),"ON");

                    remainMap[position] = mainActivity.compareToRealTime(alarms_list.get(position).getHours(),
                            alarms_list.get(position).getMinutes(),alarms_list.get(position).getAmPm(),alarms_list.get(position).getNextDayOfWeek());

                    Log.i("test","click button test remain : " + remainMap[position]);
                    alarms_list.get(position).setCountDown(remainMap[position]*1000);
                }

                notifyDataSetChanged();
            }
        });

        iCursor.moveToPosition(db_id);
        if(iCursor.getString(iCursor.getColumnIndex("onoff")).compareTo("ON")==0){

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.alarmsItem_layout.setBackgroundResource(R.drawable.border_on);
                holder.alarmsItem_clockImg.setBackgroundResource(R.drawable.alarm_icon_red);
                //여기까지는 단순 적용이니깐 바로 적용한다.
            }
            Log.i("test","onBindviewholder : On");


            holder.alarmsItem_checkbox.setChecked(true);


            CountDownTimer timer = timerMap.get(position);//카운터 컨테이너 관리
            if (timer == null) {
                Log.i("test","onBindviewholder : On && NULL" + alarms_list.get(position).getCountDown());
                timer = new CountDownTimer(alarms_list.get(position).getCountDown(), 1000) {

                    public void onTick(long millisUntilFinished) {
                        if (millisUntilFinished <= standard_time) {
                            if (millisUntilFinished <= standard_time / 24) {
                                holder.alarmsItem_countDown.setText("남은시간 : " + millisUntilFinished / (60 * 1000) + "분" + (millisUntilFinished - (millisUntilFinished / (60 * 1000)) * 60 * 1000) / 1000 + "초");
                                if(millisUntilFinished / (60 * 1000) == 15){
                                    new MainActivity.AlarmHATT(mainActivity.getApplicationContext()).Alarm(alarms_list.get(position).getHours(),alarms_list.get(position).getMinutes());

                                }


                            }
                            else
                                holder.alarmsItem_countDown.setText("남은시간 : " + millisUntilFinished / (60 * 60 * 1000) + "시" + (millisUntilFinished - millisUntilFinished / (60 * 60 * 1000) * 60 * 60 * 1000) / (60 * 1000) + "분");

                        } else if (millisUntilFinished < standard_time * 2) {
                            holder.alarmsItem_countDown.setText("남은시간 : " + "1일전...");
                        } else if (millisUntilFinished < standard_time * 3) {
                            holder.alarmsItem_countDown.setText("남은시간 : " + "2일전...");
                        } else if (millisUntilFinished < standard_time * 4) {
                            holder.alarmsItem_countDown.setText("남은시간 : " + "3일전...");
                        } else if (millisUntilFinished < standard_time * 5) {
                            holder.alarmsItem_countDown.setText("남은시간 : " + "4일전...");
                        } else if (millisUntilFinished < standard_time * 6) {
                            holder.alarmsItem_countDown.setText("남은시간 : " + "5일전...");
                        } else if (millisUntilFinished < standard_time * 7) {
                            holder.alarmsItem_countDown.setText("남은시간 : " + "6일전...");
                        } else if (millisUntilFinished < standard_time * 8) {
                            holder.alarmsItem_countDown.setText("남은시간 : " + "7일전...");
                        }
                    }

                    public void onFinish() {
                        //여기에 줘야겠지 뭔가를
                        holder.alarmsItem_countDown.setText("done!");
                        int pos =  Integer.parseInt(iCursor.getString(iCursor.getColumnIndex("penaltyline")));
                        MCursor.moveToPosition(pos);
                        String check_penalty = iCursor.getString(iCursor.getColumnIndex("penalty"));
                        Log.i("check_penalty","check_penalty : " + check_penalty);

                        if(check_penalty.compareTo("L")==0){
                            Log.i("check_penalty","check_penalty LL: " + check_penalty);
                            Intent intent = new Intent(mainActivity, LockActivity.class);
                            mainActivity.startActivity(intent);
                        }
                        else if(check_penalty.compareTo("M")==0) {
                            mainActivity.sendSMS(MCursor.getString(MCursor.getColumnIndex("phonenumber")), MCursor.getString(MCursor.getColumnIndex("message")));
                        }
                    }

                };
                Log.i("test","onBindviewholder : ON  timer is start");

                timer.start();
                timerMap.put(position,timer);


            }
            else{
                Log.i("test","onBindViewholder : ON && else");

            }
        }

        else{
            Log.i("test","onBindviewholder : OFF");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.alarmsItem_layout.setBackgroundResource(R.drawable.border_off);
                holder.alarmsItem_clockImg.setBackgroundResource(R.drawable.alarm_icon_gray);
            }
            CountDownTimer timer = timerMap.get(position);
            try{
                timer.cancel();
            } catch (Exception e) {}
            timer=null;

            timerMap.put(position,null);

            db_id = alarms_list.get(position).getDataBase_id();
            iCursor.moveToPosition(db_id);
            mDbOpenHelper.updateColumn(iCursor.getInt(iCursor.getColumnIndex("_id")),
                    iCursor.getString(iCursor.getColumnIndex("alarmId")) , iCursor.getString(iCursor.getColumnIndex("hours")),
                    iCursor.getString(iCursor.getColumnIndex("minute")), iCursor.getString(iCursor.getColumnIndex("dayofampm")),
                    iCursor.getString(iCursor.getColumnIndex("nextdayofweek")), iCursor.getString(iCursor.getColumnIndex("place")),
                    iCursor.getString(iCursor.getColumnIndex("penalty")),iCursor.getString(iCursor.getColumnIndex("penaltyline")),"OFF");


            Log.i("test","onBindviewHolder : OFF : " + "");
            holder.alarmsItem_countDown.setText("...");
            holder.alarmsItem_checkbox.setChecked(false);
        }

        if(distance <= 30){
            alarms_list.get(position).setAlarms_OnOff(false);
            holder.alarmsItem_checkbox.setChecked(false);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                holder.alarmsItem_layout.setBackgroundResource(R.drawable.border_off);
                holder.alarmsItem_clockImg.setBackgroundResource(R.drawable.alarm_icon_gray);
            }
            CountDownTimer timer = timerMap.get(position);
            try{
                timer.cancel();
            } catch (Exception e) {}

            timer=null;
            timerMap.put(position,null);
            holder.alarmsItem_countDown.setText("...");
            holder.alarmsItem_checkbox.setChecked(false);
            Toast.makeText(mainActivity.getApplicationContext(), "도착했습니다. 알람이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }


        String DayOfweekString = "월 화 수 목 금 토 일";

        SpannableString spannableString = new SpannableString(DayOfweekString);
        String word = alarms_list.get(position).getNextDayOfWeek();

        for(int i=0;i<word.length();++i){

            String temp = word.substring(i,i+1);
            int start = DayOfweekString.indexOf(temp);
            int end = start + temp.length();

            spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF6702")), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            spannableString.setSpan(new RelativeSizeSpan(1.0f), start, end, SpannableString.SPAN_EXCLUSIVE_EXCLUSIVE);
        }


        holder.alarmsItem_dayOfWeek.setText(spannableString) ;
        holder.alarmsItem_time.setText(alarms_list.get(position).getTime()) ;


        Log.i("test","=======================\n");

    }



    public alarmsItem getItem(int index) {
        return alarms_list.get(index);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    @Override
    public int getItemCount() {
        return alarms_list.size();
    }

    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView alarmsItems_name ;
        public TextView alarmsItem_countDown ;
        TextView alarmsItem_dayOfWeek ;
        TextView alarmsItem_time ;
        CheckBox alarmsItem_checkbox;
        LinearLayout alarmsItem_layout;
        ImageView alarmsItem_clockImg;


        //public final View myView;

        ViewHolder(View itemView) {
            super(itemView) ;

            //myView =itemView;
            // 뷰 객체에 대한 참조. (hold strong reference)
            alarmsItems_name = itemView.findViewById(R.id.alarmsItem_name) ;
            alarmsItem_countDown = itemView.findViewById(R.id.alarmsItem_countDown) ;
            alarmsItem_dayOfWeek = itemView.findViewById(R.id.alarmsItem_dayOfWeek) ;
            alarmsItem_time = itemView.findViewById(R.id.alarmsItem_time) ;
            alarmsItem_checkbox = itemView.findViewById(R.id.alarmsItem_checkBox);
            alarmsItem_layout = itemView.findViewById(R.id.alarmsItem_layout);
            alarmsItem_clockImg = itemView.findViewById(R.id.alarmsItem_clockImg);



        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(),v);
        }

    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }




}
