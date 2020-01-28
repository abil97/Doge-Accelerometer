package com.example.leveler;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    CustomDrawableView myView = null;
    private Sensor sensor;
    final private float alpha = 0.005f;     // alpha coefficient of the 'moving average' filter
    private Bitmap myBitmap;
    public float xPosition, yPosition;      // Coordinates of the image
    public float xmax, ymax;                // Width and Height of screen
    public float smoothedX, smoothedY;      // Smoothed coordinates of the image, after 'moving average' filter
    public final float dogeRadius = 50.0f;  // radius of image




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // Set up the Accelerometer
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME);

        // Get width and height of the screen
        Display display = getWindowManager().getDefaultDisplay();
        xmax = (float)display.getWidth() - 50;
        ymax = (float)display.getHeight() - 50;

        // Get coordinates of the middle of the screen
        xPosition = xmax / 2;
        yPosition = ymax / 2;

        // Set up main view to Canvas
        myView = new CustomDrawableView(this);
        setContentView(myView);
    }
    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            // Smoothing accelerometer data with Moving Average
            smoothedX = (1 - alpha ) * smoothedX - ( alpha ) * event.values[0];
            xPosition +=  smoothedX;    // Updating a new X coordinate of the image

            // Smoothing accelerometer data with Moving Average
            smoothedY = (1 - alpha ) * smoothedY + ( alpha ) * event.values[1];
            yPosition += smoothedY;     // Updating a new Y coordinate of the image

            // Setting constraints, so that image won't fly off the screen
            if (xPosition < 0)
                xPosition = 0;
            if(xPosition + dogeRadius > xmax)
                xPosition = xmax - dogeRadius;

            if (yPosition < dogeRadius)
                yPosition = dogeRadius;
            if(yPosition + dogeRadius > ymax)
                yPosition = ymax - dogeRadius;


        }

    }

    @Override
    protected void onPause() {
           super.onPause();

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



    public class CustomDrawableView extends View {


        public CustomDrawableView(Context context) {
            super(context);

            Bitmap doge = BitmapFactory.decodeResource(getResources(), R.drawable.doge);
            final int dogeWidth = 100;
            final int dogeHeight = 100;
            myBitmap = Bitmap.createScaledBitmap(doge, dogeWidth, dogeHeight, true);
        }

        protected void onDraw(Canvas canvas) {

            final Bitmap bitmap = myBitmap;
            canvas.drawBitmap(bitmap, xPosition, yPosition, null);
            invalidate();

        }
    }
}