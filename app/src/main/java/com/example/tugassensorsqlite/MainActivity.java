package com.example.tugassensorsqlite;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity{
    TextView xText,yText,zText;
    Sensor accelerometerSensor;
    SensorManager SM;
    String SQLiteQuery;
    Button View;
    float x,y,z;
    SQLiteDatabase sqLiteDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        xText = (TextView) findViewById(R.id.xText);
        yText = (TextView) findViewById(R.id.yText);
        zText = (TextView) findViewById(R.id.zText);

        SM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (accelerometerSensor == null){
            if (SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null){
                createDatabase();
                int MINUTES = 1; // The delay in minutes
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        addData(); // If the function you wanted was static
                        Handler handler = new Handler(Looper.getMainLooper());
                        handler.post(
                                new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        Toast.makeText(MainActivity.this, "Data baru telah tersimpan", Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }
                }, 0, 1000 * 60 * MINUTES);

                accelerometerSensor = SM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            } else{
                yText.setText("Maaf, Smartphone tidak kompatibel");
            }
        }

        findViewById(R.id.bt_start).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SM.registerListener(accelerometerListener, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
            }
        });

        findViewById(R.id.bt_stop).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                SM.unregisterListener(accelerometerListener);
            }
        });

        View = findViewById(R.id.view);
        View.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListViewActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onResume() {
        super.onResume();
        SM.registerListener(accelerometerListener, accelerometerSensor,
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void onPause() {
        super.onPause();
        SM.unregisterListener(accelerometerListener);
    }
    SensorEventListener accelerometerListener = new SensorEventListener() {
        public void onAccuracyChanged(Sensor sensor, int acc) { }
        @SuppressLint("SetTextI18n")
        public void onSensorChanged(SensorEvent event) {
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 10 seconds
                    xText.setText("X: "+x);
                    yText.setText("Y: "+y);
                    zText.setText("Z: "+z);
                    handler.postDelayed(this, 2000);
                }
            }, 2000);  //the time is in miliseconds
        }
    };
    private String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        return dateFormat.format(date);
    }

    private void createDatabase() {
        sqLiteDatabase = openOrCreateDatabase("Nama_Database_Baru", Context.MODE_PRIVATE, null);
        sqLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS Nama_Tabel (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, title VARCHAR, x VARCHAR, y VARCHAR, z VARCHAR);");
    }

    private void addData() {
        SQLiteQuery = "INSERT INTO Nama_Tabel (title,x,y,z) VALUES ('"+ getCurrentDate() +"', '"+ x +"', '"+ y +"', '"+ z +"');";
        sqLiteDatabase.execSQL(SQLiteQuery);
    }
}