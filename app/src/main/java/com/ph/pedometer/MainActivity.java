package com.ph.pedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{
    private TextView mTextCount;
    private Button mBtnReset;
    private SensorManager mSensorManager;
    private Sensor mStepCounterSensor;

    private static int PHYISCAL_ACTIVITY = 0x1001;
    //현재 걸음 수
    private int mSteps = 0;
    //리스너가 등록되고 난 후의 step count
    private int mCounterSteps = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextCount = (TextView) findViewById(R.id.text_count);
        mBtnReset = (Button) findViewById(R.id.btn_reset);
        mBtnReset.setOnClickListener(mOnClickListener);

        mTextCount.setText("" + mSteps);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED)
        {
            //ask for permission
            requestPermissions(new String[]{Manifest.permission.ACTIVITY_RECOGNITION}, PHYISCAL_ACTIVITY);
        }
        else
        {
            initSensors();
        }



//        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//        mStepCounterSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
//
//        if(mStepCounterSensor != null)
//        {
//            mSensorManager.registerListener(mSensorEventListener, mStepCounterSensor, SensorManager.SENSOR_DELAY_GAME);
//        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        if(requestCode == PHYISCAL_ACTIVITY)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                initSensors();

            }
            else
            {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                finish();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onDestroy()
    {
        if(mSensorManager != null)
        {
            mSensorManager.unregisterListener(mSensorEventListener);
        }
        super.onDestroy();
    }

    private void initSensors() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        assert mSensorManager != null;
        Sensor stepSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (stepSensor == null) {
//            createToastMessage("Sensor not found.");
//            selectedFragment = new NoSensor_Fragment();
//            activeFragment = 5;
//            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            Toast.makeText(MainActivity.this, "Type_STEP_COUNT is null", Toast.LENGTH_SHORT).show();
        } else {
            mSensorManager.registerListener(mSensorEventListener, stepSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            mSteps = 0;
            mCounterSteps = 0;
        }
    };

    private SensorEventListener mSensorEventListener = new SensorEventListener()
    {
        @Override
        public void onSensorChanged(SensorEvent event)
        {
            if(event.sensor.getType() == Sensor.TYPE_STEP_COUNTER){

                //stepcountsenersor는 앱이 꺼지더라도 초기화 되지않는다. 그러므로 우리는 초기값을 가지고 있어야한다.
                if (mCounterSteps < 1) {
                    // initial value
                    mCounterSteps = (int) event.values[0];
                }
                //리셋 안된 값 + 현재값 - 리셋 안된 값
                mSteps = (int) event.values[0] - mCounterSteps;
                mTextCount.setText(Integer.toString(mSteps));
                Log.d("AAAA", "Total step count: " + mSteps );
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {
            Toast.makeText(MainActivity.this, "acc"+accuracy, Toast.LENGTH_SHORT).show();
        }
    };
}