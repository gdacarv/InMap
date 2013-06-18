package com.contralabs.inmap.location;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorHelper{

	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	private Sensor mMagnetometer;
	private Context mContext;
	private OnSensorChangeListener mOnSensorChangeListener;

	public SensorHelper(Context context) {
		mContext = context;
	}

	public void init() {
		mSensorManager = (SensorManager)mContext.getSystemService(Context.SENSOR_SERVICE);
		mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
	}

	public void beginListening(int rate) {
		if(mSensorManager == null)
			init();
		mSensorManager.registerListener(sensorEventListener, mAccelerometer, rate);
		mSensorManager.registerListener(sensorEventListener, mMagnetometer, rate);
	}

	public void stopListening() {
		mSensorManager.unregisterListener(sensorEventListener);
	}

	public void setOnSensorChangeListener(OnSensorChangeListener listener) {
		mOnSensorChangeListener = listener;
	}

	private SensorEventListener sensorEventListener = new SensorEventListener() {

		private float[] mGravity;
		private float[] mGeomagnetic;
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
				mGravity = event.values;
			if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
				mGeomagnetic = event.values;
			if (mGravity != null && mGeomagnetic != null && mOnSensorChangeListener != null) {
				float R[] = new float[9];
				float I[] = new float[9];
				boolean success = SensorManager.getRotationMatrix(R, I, mGravity, mGeomagnetic);
				if (success) {
					float orientation[] = new float[3];
					SensorManager.getOrientation(R, orientation);
					mOnSensorChangeListener.onSensorChanged(orientation[0], orientation[1], orientation[2]);
				}
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};
	
	public interface OnSensorChangeListener {
		void onSensorChanged(float azimuth, float pitch, float roll);
	}
}
