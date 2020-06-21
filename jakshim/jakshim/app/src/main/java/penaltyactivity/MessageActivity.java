package penaltyactivity;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.jakshim.R;

import database.DbMOpenHelper;

public class MessageActivity extends AppCompatActivity implements View.OnClickListener {
    InputMethodManager imm;
    PendingIntent sentPI;

    private DbMOpenHelper mDbOpenHelper;
    Cursor iCursor;

    EditText input_phoneNumber;
    EditText input_message;
    Button clickButton;
    Button cancelButton;

    int db_id;
    int pL;

    boolean checkBtn = false;

    private String Message = " "; // 문자 보낼 메시지
    private String Phone = "01011112222";  // 문자 보낼 휴대폰 번호
    int pos=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_setting_message);

        input_message = (EditText)findViewById(R.id.messageLayout_M);
        input_phoneNumber = (EditText)findViewById(R.id.messageLayout_pH);
        clickButton = (Button)findViewById(R.id.messageLayout_button);
        cancelButton = (Button)findViewById(R.id.messageLayout_cancel);


        clickButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);


        imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);

    }



    private void sendSMS(String phoneNumber, String message) {

        // 권한이 허용되어 있는지 확인한다
        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.SEND_SMS},1);
            Toast.makeText(this,"권한을 허용하고 재전송해주세요",Toast.LENGTH_LONG).show();
        } else {
            SmsManager sms = SmsManager.getDefault();

            // 아래 구문으로 지정된 핸드폰으로 문자 메시지를 보낸다
            sms.sendTextMessage(phoneNumber, null, message, sentPI, null);
            Toast.makeText(this,"전송을 완료하였습니다",Toast.LENGTH_LONG).show();
        }
    }
    public void ThrowInformation(){
        String temp_phoneNumber = input_phoneNumber.getText().toString();
        String temp_message = input_message.getText().toString();
        mDbOpenHelper.insertColumn(temp_phoneNumber,temp_message,"message");
        while(iCursor.moveToNext()){
            pos = iCursor.getPosition();

        }

        onBackPressed();
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.messageLayout_button:
                checkBtn = true;
                ThrowInformation();
                break;
            case R.id.messageLayout_cancel:
                onBackPressed();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Intent intent = getIntent(); /*데이터 수신*/

        db_id = intent.getExtras().getInt("db_id");
        pL = intent.getExtras().getInt("pL");
        mDbOpenHelper = new DbMOpenHelper(this);
        mDbOpenHelper.open();
        iCursor = mDbOpenHelper.selectColumns();
        mDbOpenHelper.deleteAllColumns();

    }

    @Override
    public void onBackPressed() {

        Log.i("MDB","youwan : " + iCursor.getPosition());
        //수정해야함




        if(checkBtn == true) {
            Log.i("penalty_line", "press BAckBtn : " + pos);

            Intent intent = new Intent();
            //intent.putExtra("destName", placeName);
            intent.putExtra("pL", pos);//db_id가 -1일때는 무조건 이렇게 하고 어차피 하나씩 늘려가면 되니깐!!!!!그리고 이걸 저장해놓으면 됨
            intent.putExtra("db_id", db_id);
            intent.putExtra("check","y");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
        else{
            Intent intent = new Intent();
            intent.putExtra("db_id", db_id);
            intent.putExtra("pL", pL);
            intent.putExtra("check", "n");
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }
}

/*message 전화번호 내용 */
