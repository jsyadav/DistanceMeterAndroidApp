package com.example.camerapreview;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

	public static String TAG = "CameraPreview";

	    private SurfaceHolder mHolder;
	    private Camera mCamera;
	   

	    public CameraPreview(Context context, Camera camera) {
	        super(context);
	        mCamera = camera;

	        // Install a SurfaceHolder.Callback so we get notified when the
	        // underlying surface is created and destroyed.
	        mHolder = getHolder();
	        mHolder.addCallback(this);
	        // deprecated setting, but required on Android versions prior to 3.0
	        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	    }

	    public void surfaceCreated(SurfaceHolder holder) {
	        // The Surface has been created, now tell the camera where to draw the preview.
	        try {
	            mCamera.setPreviewDisplay(holder);
	            mCamera.startPreview();
	            startFaceDetection();
	            
	        } catch (IOException e) {
	            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
	        }
	    }

	    public void surfaceDestroyed(SurfaceHolder holder) {
	        // empty. Take care of releasing the Camera preview in your activity.
	    }

	    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
	        // If your preview can change or rotate, take care of those events here.
	        // Make sure to stop the preview before resizing or reformatting it.

	        if (mHolder.getSurface() == null){
	          // preview surface does not exist
	          return;
	        }

	        // stop preview before making changes
	        try {
	        	
	            mCamera.stopPreview();
	        } catch (Exception e){
	          // ignore: tried to stop a non-existent preview
	        }

	        // set preview size and make any resize, rotate or
	        // reformatting changes here

	        // start preview with new settings
	        try {
	            mCamera.setPreviewDisplay(mHolder);
	            mCamera.startPreview();
	            startFaceDetection();
	            

	        } catch (Exception e){
	            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
	        }
	    }
	    
	    public void startFaceDetection(){
	        // Try starting Face Detection
	        Camera.Parameters params = mCamera.getParameters();
	        
	        Log.i(TAG, "Max num Focus Area "+params.getMaxNumFocusAreas());
	        
	        float [] output = new float[3];
	        params.getFocusDistances(output);
	        int l = output.length;
	        for (int i =0; i < l;i++)
	        	Log.i(TAG, "Supported focus distance in meters " +output[i]);
	        

	        // start face detection only *after* preview has started
	        if (params.getMaxNumDetectedFaces() > 0){
	        	Log.w(TAG, "Hardware supports face detection");
	            // camera supports face detection, so can start it:
	            mCamera.startFaceDetection();
	        }else {
	        	 
	        	Log.w(TAG, "Hardware doesn't support face detection");
	        	
	        }
	    }
	}
