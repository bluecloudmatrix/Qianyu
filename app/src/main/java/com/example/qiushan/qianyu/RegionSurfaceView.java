package com.example.qiushan.qianyu;

/**
 * Created by qiushan on 11/25/2015.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

public class RegionSurfaceView extends SurfaceView implements Callback,Runnable {
    Paint paint;
    SurfaceHolder surfaceHolder;
    Thread thread;
    boolean flag;
    int sleeptime=30;
    boolean isCollsion;
    /*define a rect*/
    Rect rect=new Rect(300, 300, 600, 600);
    /*define a region*/
    Region region=new Region(rect);
    public RegionSurfaceView(Context context) {
        super(context);
        paint=new Paint();
        paint.setTextSize(90);
        paint.setStrokeWidth(5);
        paint.setStyle(Style.STROKE);
        paint.setAntiAlias(true);
        surfaceHolder=getHolder();
        surfaceHolder.addCallback(this);
        thread=new Thread(this);
    }

    private void mydraw(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        canvas.drawRect(rect, paint);
        if(isCollsion){
            paint.setColor(Color.BLUE);
            canvas.drawText("Collsion is ture", 100, 100, paint);
        }
        else{
            paint.setColor(Color.GRAY);
            canvas.drawText("Collsion is false", 100, 100, paint);
        }
    }


    /**
     * Touch Event
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(region.contains((int)event.getX(), (int)event.getY())){
            isCollsion=true;
        }
        else{
            isCollsion=false;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void run() {
        Canvas canvas=null;
        while (flag) {
            try {
                canvas=surfaceHolder.lockCanvas();
                synchronized (canvas) {
                    mydraw(canvas);
                }
            } catch (Exception e) {
                Log.e("region",e.getMessage());
            }
            finally{
                if(canvas!=null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(sleeptime);
            } catch (Exception e) {
                Log.e("region",e.getMessage());
            }
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        flag=true;
        if(!thread.isAlive()){
            thread.start();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        flag=true;
    }

}