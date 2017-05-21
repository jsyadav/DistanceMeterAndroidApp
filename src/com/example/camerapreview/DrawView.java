package com.example.camerapreview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawView extends View{
	public static String TAG = "DrawView";
	
	 Paint paint = new Paint();
	 int radius = 0;
	 int alpha = 50;
	 int color = Color.WHITE;
	public DrawView(Context context, int radius, int alpha, int color) {
        super(context);  
        this.radius = radius;
        this.alpha = alpha;
        this.color= color;
    }

    @Override
    public void onDraw(Canvas canvas) {
    	paint.setColor(color);
        paint.setAlpha(alpha);
    	canvas.drawCircle(500, 250, radius, paint);
    	//paint.setStrokeWidth(3);
    	//canvas.drawRect(400, 400, 550, 550, paint);
    }


}
