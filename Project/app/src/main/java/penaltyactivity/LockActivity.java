package penaltyactivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PointF;
import android.os.CountDownTimer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import com.naver.maps.map.overlay.CircleOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LockActivity extends AppCompatActivity implements OnMapReadyCallback, NaverMap.OnMapClickListener {


    UiSettings uiSettings;
    private NaverMap map;
    private static Marker destMarker = new Marker();
    private static final int PERMISSION_REQUEST_CODE = 100;
    private FusedLocationSource locationSource;
    private static LatLng currentPosition = null;
    private static LatLng destPosition = null;
    private CountDownTimer countDownTimer;

    private static final int MILLISINFUTURE = 10*1000;
    private static final int COUNT_DOWN_INTERVAL = 1000;
    private int count = 10;

    private TextView countTxt ;





    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mLayout;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mLayout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent("Lock");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        mLayout = findViewById(R.id.activity_lock);

        countTxt = (TextView)findViewById(R.id.lock_remain);


        CameraPosition cameraPosition = new CameraPosition(new LatLng(35.23083313833922, 129.0826465934515), 14);
        NaverMapOptions options = new NaverMapOptions()
                .camera(cameraPosition);
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.lockActivity_map);

        if (mapFragment == null) {
            mapFragment = MapFragment.newInstance(options);
            getSupportFragmentManager().beginTransaction().add(R.id.lockActivity_map, mapFragment).commit();
        }

        mapFragment.getMapAsync(this);

        locationSource = new FusedLocationSource(this, PERMISSION_REQUEST_CODE);

        destMarker.setWidth(70);
        destMarker.setHeight(100);

        countDownTimer();
        countDownTimer.start();





        Intent intent = new Intent(LockActivity.this, LockService.class);
        stopService(intent);
        startService(intent);
        Log.d("activity","startService");
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions, grantResults)) {
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void countDownTimer(){

        countDownTimer = new CountDownTimer(MILLISINFUTURE, COUNT_DOWN_INTERVAL) {
            public void onTick(long millisUntilFinished) {
                countTxt.setText(String.valueOf(count));
                count --;
            }
            public void onFinish() {
                finish();
            }
        };
    }


    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }


    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    @Override public void onBackPressed() {
        //super.onBackPressed();
        }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.d("Activity", "User Live Hint");
        Intent intent = new Intent("Lock");
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
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

        //double lat = intent.getExtras().getDouble("destLatitude");
        double lat = 0;
        double lng;
        if(lat != 0)
        {
            lng = intent.getExtras().getDouble("destLongitude");
            destPosition = new LatLng(lat,lng);
            destMarker.setPosition(destPosition);
            destMarker.setMap(map);
            CameraUpdate cameraUpdate = CameraUpdate.scrollTo(destPosition)
                    .animate(CameraAnimation.Easing);
            map.moveCamera(cameraUpdate);
        }

        //임시로.
        destPosition = new LatLng(35.23083313833922, 129.0826465934515);
        destMarker.setPosition(destPosition);
        destMarker.setMap(map);


        CircleOverlay circle = new CircleOverlay();
        circle.setCenter(destPosition);
        circle.setRadius(100);
        circle.setMap(map);
    }
}
