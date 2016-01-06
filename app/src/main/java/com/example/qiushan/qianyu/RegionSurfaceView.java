package com.example.qiushan.qianyu;

/**
 * Created by qiushan on 11/25/2015.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.RegionIterator;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

import java.util.Timer;
import java.util.TimerTask;

public class RegionSurfaceView extends SurfaceView implements Callback,Runnable {
    Paint paint;
    SurfaceHolder surfaceHolder;
    Thread thread;
    boolean flag;
    int sleeptime = 30;
    boolean isCollsion;

    boolean isWin;
    /*define many rect*/
    Rect rect = new Rect(300, 300, 600, 600); // rectangle a
    Rect rectb = new Rect(800, 800, 1000, 1000); // rectangle b
    Rect rectc = new Rect(200, 900, 700, 1400); // rectangle c
    /*define many region*/
    Region region = new Region(rect); // region 1
    Region _region_b = new Region(rectb); // region 2
    Region _region_c = new Region(rectc); // region 3

    private Path ovalPath;
    private RectF rf;
    private Region aim;

    private int mov_x = 0;
    private int mov_y = 0;
    private int mov_x_1 = 0;
    private int mov_y_1 = 0;

    private Path mPath;
    private float mPosX, mPosY;

    private boolean timing = true;
    private final Timer timer = new Timer();
    private TimerTask task;

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            timing = false;
            super.handleMessage(msg);
        }
    };

    public RegionSurfaceView(Context context) {
        super(context);
        paint = new Paint();
        paint.setTextSize(50);
        paint.setTypeface(Typeface.SERIF);
        paint.setStrokeWidth(5);
        paint.setStyle(Style.STROKE);
        paint.setAntiAlias(true);

        mPath = new Path();

        ovalPath = new Path();
        rf = new RectF(800, 1500, 900, 1600);
        ovalPath.addOval(rf, Path.Direction.CCW);
        aim = new Region();
        aim.setPath(ovalPath, new Region(800, 1500, 900, 1600));

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        thread = new Thread(this);

        task = new TimerTask() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        };

        timer.schedule(task, 5000);

    }

    private void mydraw(Canvas canvas){
        canvas.drawColor(Color.WHITE);
        if (timing) {
            canvas.drawRect(rect, paint);
            canvas.drawRect(rectb, paint);
            canvas.drawRect(rectc, paint);
        }
        drawRegion(canvas, aim, paint);

        if (isCollsion) {
            paint.setColor(Color.BLUE);
            canvas.drawText("Hit!", 100, 100, paint);
        } else {
            paint.setColor(Color.GRAY);
            canvas.drawText("Good! Keep", 100, 100, paint);
        }

        if (isWin) {
            paint.setColor(Color.GREEN);
            canvas.drawText("Win!", 700, 100, paint);
        } else {
            paint.setColor(Color.GRAY);
            canvas.drawText("Silent", 700, 100, paint);
        }

        canvas.drawPath(mPath, paint);
    }

    /**
     * Touch Event
     */
    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(region.contains((int)event.getX(), (int)event.getY())
                || _region_b.contains((int)event.getX(), (int)event.getY())
                || _region_c.contains((int)event.getX(), (int)event.getY())){
            isCollsion = true;
        }
        else{
            isCollsion = false;
        }

        if (aim.contains((int)event.getX(), (int)event.getY())) {
            isWin = true;
        } else {
            isWin = false;
        }

        // drag
        int action = event.getAction();
        float x = event.getX();
        float y = event.getY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPath.reset();
                mPosX = x;
                mPosY = y;
                mPath.moveTo(x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.quadTo(mPosX, mPosY, x, y);
                mPosX = x;
                mPosY = y;
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        invalidate();
        //return super.onTouchEvent(event);
        return true;
    }

    @Override
    public void run() {
        Canvas canvas = null;
        while (flag) {
            try {
                canvas = surfaceHolder.lockCanvas();
                synchronized (canvas) {
                    mydraw(canvas);
                }
            } catch (Exception e) {
                Log.e("region", e.getMessage());
            }
            finally{
                if(canvas != null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(sleeptime);
            } catch (Exception e) {
                Log.e("region", e.getMessage());
            }
        }

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        flag = true;
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
        flag = true;
    }


    private void drawRegion(Canvas canvas, Region rgn, Paint paint) {
        RegionIterator iter = new RegionIterator(rgn);
        Rect r = new Rect();

        while (iter.next(r)) {
            canvas.drawRect(r, paint);
        }
    }
}