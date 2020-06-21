package mapactivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jakshim.R;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapOptions;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import settingactivty.settingActivity;

public class AlarmSettingPlaceActivity extends AppCompatActivity implements OnMapReadyCallback, NaverMap.OnMapClickListener, View.OnClickListener{
    private static final int PERMISSION_REQUEST_CODE = 100;

    private static String placeName = "너가 직접 고른 곳";
    private NaverMap map;
    private static LatLng currentPosition = null;
    private static LatLng destPosition = null;
    private static Marker destMarker = new Marker();
    UiSettings uiSettings;

    private ImageView imageView;
    private static EditText editText;

    private FusedLocationSource locationSource;


    private static String searchResult = "";
    private static JSONObject jsonObject = null;
    private static JSONArray jsonArray;
    private static RecyclerView recyclerView;
    private static ArrayList<PlaceData> placelist = new ArrayList<>();
    private static PlaceListAdapter adapter;

    Button saveButton;
    Button cancelButton;

    Boolean checkBtn = false;
    int db_id;
    int pL;
    String old_destName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_setting_place);
        CameraPosition cameraPosition = new CameraPosition(new LatLng(35.23083313833922, 129.0826465934515), 14);

        NaverMapOptions options = new NaverMapOptions()
                .camera(cameraPosition);

        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map);


        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(options);
            getSupportFragmentManager().beginTransaction().add(R.id.map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        locationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

        destMarker.setWidth(70);
        destMarker.setHeight(100);

        imageView = findViewById(R.id.searchButton);
        editText = findViewById(R.id.search);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
               @Override
               public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                   switch (actionId) {
                       case EditorInfo.IME_ACTION_SEARCH:
                           new Thread(){
                               //implementing run method
                               public void run() {
                                   DoSearch(editText.getText().toString());
                                   Log.d("http", searchResult);
                               }
                           }.start();
                           break;
                       default:
                           // 기본 엔터키 동작
                           return false;
                   }
                   return true;
               }
           });
        imageView.setOnClickListener(this);


        recyclerView = findViewById(R.id.placelist) ;
        recyclerView.setLayoutManager(new LinearLayoutManager(this)) ;
        adapter = new PlaceListAdapter(placelist) ;

        adapter.setItemClick(new PlaceListAdapter.ItemClick() {
            @Override
            public void onClick(View view, int position) {
                try {
                    jsonObject = jsonArray.getJSONObject(position);
                    destPosition = new LatLng(jsonObject.getDouble("y"),jsonObject.getDouble("x"));
                    placeName = jsonObject.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                destMarker.setPosition(destPosition);
                destMarker.setMap(map);

                CameraUpdate cameraUpdate = CameraUpdate.scrollTo(destPosition)
                        .animate(CameraAnimation.Easing);
                map.moveCamera(cameraUpdate);

                Log.d("Clicked", destPosition.latitude + ", " +destPosition.longitude);

            }
        });

        recyclerView.setAdapter(adapter);


        saveButton = findViewById(R.id.alarmSetting_place_save);
        cancelButton = findViewById(R.id.alarmSetting_place_cancel);
        saveButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);



    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        map = naverMap;
        uiSettings = map.getUiSettings();
        uiSettings.setLocationButtonEnabled(true);

        map.setLocationSource(locationSource);
        map.setLocationTrackingMode(LocationTrackingMode.Follow);

        map.addOnLocationChangeListener(location -> {
            Log.d("Inform","Location changed!!!");
            Log.d("inform", location.getLatitude() + ", " + location.getLongitude());
            currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        });

        map.setOnMapClickListener(this);


        Intent intent = getIntent();

        double lat = intent.getExtras().getDouble("destLatitude");
        double lng;
        if(lat != 0)
        {
            lng = intent.getExtras().getDouble("destLongitude");
            destPosition = new LatLng(lat,lng);
            placeName = intent.getExtras().getString("destName");

            destMarker.setPosition(destPosition);
            destMarker.setMap(map);
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(destPosition)
                    .animate(CameraAnimation.Easing);
            map.moveCamera(cameraUpdate);
        }

    }





    @Override
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
        Log.d("inform", "Clicked!!");
        destPosition = latLng;
        placeName = "너가 고른곳";
        destMarker.setPosition(destPosition);
        destMarker.setMap(map);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, settingActivity.class);

        Log.i("test","pl : " + pL);
        switch(v.getId()) {
            case R.id.searchButton :
                new Thread(){
                    //implementing run method
                    public void run() {
                        DoSearch(editText.getText().toString());
                        Log.d("http", searchResult);
                    }
                }.start();
                break;
            case R.id.alarmSetting_place_save :
                if(destPosition == null)
                {
                    Toast.makeText(getApplicationContext(), "목표장소를 골라주세요.", Toast.LENGTH_SHORT).show();
                    break;
                }
                checkBtn = true;
                onBackPressed();
                break;

            case R.id.alarmSetting_place_cancel :
                onBackPressed();
                break;
        }
    }

    private void DoSearch(String place) {
        String myurl;
        if(currentPosition == null)
        {
            myurl = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query=" + place + "&coordinate=" + 129.0826465934515 + ", " + 35.23083313833922;
        }
        else
        {
            myurl = "https://naveropenapi.apigw.ntruss.com/map-place/v1/search?query=" + place + "&coordinate=" + currentPosition.longitude + ", " + currentPosition.latitude;
        }


        try {
            HttpURLConnection connection = null;
            URL url = new URL(myurl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("X-NCP-APIGW-API-KEY-ID","f9jmh5xw8t");
            connection.setRequestProperty("X-NCP-APIGW-API-KEY","2mzPF4BcwL7yuA2D4aoApCPN1y42hFzsnncXLoJK");
            connection.setRequestProperty("Accept-Charset", "UTF-8");


            connection.setAllowUserInteraction(false);
            connection.setInstanceFollowRedirects(true);
            connection.setRequestMethod("GET");
            connection.connect();

            InputStream is = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }

            searchResult = response.toString();
            jsonObject = new JSONObject(searchResult);
            jsonArray = jsonObject.getJSONArray("places");



            adapter.clear();

            for(int i=0; i<jsonArray.length(); i++) {
                jsonObject = jsonArray.getJSONObject(i);
                Log.d("place","-------------------------");
                Log.d("place",jsonObject.getString("name"));
                Log.d("place",jsonObject.getString("road_address"));
                Log.d("place",jsonObject.getDouble("x")+ ", "+ jsonObject.getDouble("y"));
                Log.d("place","-------------------------");

                adapter.add(new PlaceData(jsonObject.getString("name"), jsonObject.getString("road_address")));
            }

            new Thread(new Runnable() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                            recyclerView.smoothScrollToPosition(0);
                        }
                    });
                }
            }).start();






        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onBackPressed() {

        Intent oldintent = getIntent();
        db_id = oldintent.getExtras().getInt("db_id");
        pL = oldintent.getExtras().getInt("pL");
        old_destName = oldintent.getExtras().getString("destName");
        double old_lat = oldintent.getExtras().getDouble("destLatitude");
        double old_lon = oldintent.getExtras().getDouble("destLongitude");


        if(checkBtn == true) {
            Log.i("penalty_line", "press BAckBtn");

            Intent intent = new Intent();
            intent.putExtra("destName", placeName);
            //intent.putExtra("destName", placeName);
            intent.putExtra("destLatitude", destPosition.latitude);
            intent.putExtra("destLongitude", destPosition.longitude);
            intent.putExtra("db_id", db_id);
            intent.putExtra("pL", pL);
            Log.i("penalty_line","penalty_line place : " + pL);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        else{
            Intent intent = new Intent();
            intent.putExtra("destName",old_destName);
            intent.putExtra("destLatitude",old_lat);
            intent.putExtra("destLongitude",old_lon);
            intent.putExtra("db_id", db_id);
            intent.putExtra("pL", pL);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

}
