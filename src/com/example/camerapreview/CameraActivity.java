package com.example.camerapreview;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.FaceDetectionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class CameraActivity extends Activity implements FaceDetectionListener{

	public static String TAG = "CameraActivity";

	private Camera mCamera;
	private CameraPreview mPreview;
	private FrameLayout preview ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActionBar actionBar = getActionBar();
		actionBar.hide();

		preview = (FrameLayout) findViewById(R.id.camera_preview);
		
		
		// Create an instance of Camera
		mCamera = getCameraInstance();
		if (mCamera == null){
			AlertMesg("Camera Error", "Camera can't be initiated.");
			return;
		}
		
		Camera.Parameters params = mCamera.getParameters();
		params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);		
		
		// set Camera parameters
		//mCamera.setParameters(params);
		
		if (params.getMaxNumDetectedFaces() > 0){
			mCamera.setFaceDetectionListener(this);
		}
		else{
			AlertMesg("FaceDetection Support", "Camera doesn't support face detection.");
			// Create our Preview view and set it as the content of our activity.
			mPreview = new CameraPreview(this, mCamera);
			preview.addView(mPreview);
			
			final TextView value = (TextView)findViewById(R.id.distance);
			SeekBar seek = (SeekBar)findViewById(R.id.seekBar);
			seek.setMax(10);
			seek.setProgress(5);
			DrawView drawView1 = new DrawView(CameraActivity.this,125, 70, Color.BLACK);
			preview.addView(drawView1);
			
			//value.setText("50");

			seek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
				int radius = 50;

				public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
					radius = (int)(25*progress);
					View v = preview.getChildAt(1);
					if (v != null){
						preview.removeView(v);
					}
					DrawView drawView1 = new DrawView(CameraActivity.this,radius, 70, Color.BLACK);
					preview.addView(drawView1);

					String mesg = String.valueOf((2500/radius));
					value.setText(mesg);
				}

				public void onStartTrackingTouch(SeekBar seekBar) {
					// TODO Auto-generated method stub

				}

				public void onStopTrackingTouch(SeekBar seekBar) {
					//Toast.makeText(CameraActivity.this,"seek bar progress:"+progressChanged,
					//       Toast.LENGTH_SHORT).show();


				}
			});


			//DrawView drawView2 = new DrawView(this,200, 40, Color.RED);
			//preview.addView(drawView2);
			//DrawView drawView3 = new DrawView(this,150, 30, Color.BLUE);
			//preview.addView(drawView3);
			//DrawView drawView4 = new DrawView(this,100, 20, Color.WHITE);
			//preview.addView(drawView4);
		}

	}
	/** A safe way to get an instance of the Camera object. */
	public static Camera getCameraInstance(){
		Camera cam = null;
		try {

			int cameraCount = 0;
			Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
			cameraCount = Camera.getNumberOfCameras();
			for ( int camIdx = 0; camIdx < cameraCount; camIdx++ ) {
				Camera.getCameraInfo( camIdx, cameraInfo );
				if ( cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					try {
						cam = Camera.open( camIdx );
					} catch (RuntimeException e) {
						Log.e(TAG, "Camera failed to open: " + e.getLocalizedMessage());
					}
				}
			}
		}
		catch (Exception e){
			// Camera is not available (in use or does not exist)
		}
		return cam; // returns null if camera is unavailable
	}

	public void AlertMesg( String title, String message ){

		new AlertDialog.Builder(CameraActivity.this)
		.setTitle( title)
		.setMessage( message )
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				//finish();
			}
		}).show();

	}
	
	public void checkMetering(){
		Camera.Parameters params = mCamera.getParameters();
		Log.i(TAG, "get max num of metering area " + params.getMaxNumMeteringAreas());
		if (params.getMaxNumMeteringAreas() > 0){ // check that metering areas are supported
			Log.w(TAG, "Hardware supports metering areas");
			List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();
			Rect areaRect1 = new Rect(-100, -100, 100, 100);    // specify an area in center of image
			meteringAreas.add(new Camera.Area(areaRect1, 600)); // set weight to 60%
			Rect areaRect2 = new Rect(800, -1000, 1000, -800);  // specify an area in upper right of image
			meteringAreas.add(new Camera.Area(areaRect2, 400)); // set weight to 40%
			params.setMeteringAreas(meteringAreas);
		}else {
			Log.w(TAG, "Hardware doesn't support metering areas");
			AlertMesg("Metering Support", "Camera doesn't support metering areas.");
		}
	}
	@Override
	public void onFaceDetection(Face[] faces, Camera camera) {
		// TODO Auto-generated method stub
		if (faces.length > 0){
            Log.d("FaceDetection", "face detected: "+ faces.length +
                    " Face 1 Location X: " + faces[0].rect.centerX() +
                    "Y: " + faces[0].rect.centerY() );
        }
	}
	
	public void onStop(){
		super.onStop();
		mCamera.release();
	}


}