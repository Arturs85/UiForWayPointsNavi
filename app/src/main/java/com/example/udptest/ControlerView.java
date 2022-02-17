package com.example.udptest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


/**
 * Created by user on 2016.09.10..
 */
public class ControlerView extends View {
    Paint paint = new Paint();
    Paint paint2 = new Paint();
   int width=480;
   int screenHeight=800;
   double forwardRegion = 0.225;// of total height
    double reveseRegion = 0.225;
    double zeroRegion = 0.15;
int zeroZoneDir = 10;//px
    int zeroZonePx = 20; //vertical distance form controller center to active zone
    //Bitmap bitmap;
    Point controlerCenter;
    float x =0, y = 0;
    volatile boolean isSending= false;
    MainActivity mainActivity;
    public ControlerView(Context context) {
        super(context);
width = MainActivity.screenWidth;
screenHeight = MainActivity.screenHeight;
        controlerCenter = new Point(width/2,(int)(screenHeight*(forwardRegion+zeroRegion/2)));
        zeroZonePx = (int)(screenHeight*zeroRegion/2);
        zeroZoneDir = (int)(width*0.05);
        paint.setAntiAlias(true);
        paint.setColor(Color.CYAN);
        paint2.setColor(Color.GRAY);
        x=controlerCenter.x;
        y=controlerCenter.y-55;
       mainActivity = (MainActivity) context;
        // this.bitmap = bitmap;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        //Log.d("controllerView", "onDraw");
        super.onDraw(canvas);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        canvas.drawCircle(x,y,50,paint);
        canvas.drawLine(controlerCenter.x,controlerCenter.y,x,y,paint);
        canvas.drawCircle(controlerCenter.x,controlerCenter.y,20,paint);
        canvas.drawRect(0,controlerCenter.y-zeroZonePx,width,controlerCenter.y+zeroZonePx,paint);
//canvas.drawText("fyfiiifytxzsfghh",100,100,paint2);

        // if (bitmap != null)
        //     canvas.drawBitmap(bitmap, 0, 0, null);
        // else
        //    canvas.drawText("Null bitmap", 44, 40, paint);
        super.onDraw(canvas);

    }
    // invalidate();

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.
        x = e.getX();
        y = e.getY();


        if (e.getAction() == MotionEvent.ACTION_DOWN) {



            Log.d("TouchEventv", "x = " + x+" y = "+y);

        }



        invalidate();
        return true;
    }
    float getRadius (){

        float d = (x-controlerCenter.x);

        if(d<zeroZoneDir && d>-zeroZoneDir) return 10000;
        float sign = Math.signum(d);
     // to percent
        d = controlerCenter.x/d;

        float radi = 0.5f*sign*(1-Math.abs(d));// max radi = 10
        return radi;

    }
    int getSpeed(double radius){


        int d = controlerCenter.y-(int)y;
        int sign = (int)Math.signum(d);
int speed = Math.abs(d)- zeroZonePx;
if(speed<0)speed =0;
else if(speed>100) speed =100;
speed=speed*sign;

if(Math.abs(radius)<0.3) speed*=Math.abs(radius)*3;
return speed*2;

    }
void stopSending(){
        isSending = false;
}
     void startSending() {
        {
            isSending = true;
            Thread thread = new Thread(new Runnable() {

                String stringData;

                @Override
                public void run() {

                    while (isSending) {
                       double radi = getRadius();
                        mainActivity.sendMessage("CONTROL,"+getSpeed(radi)+","+radi);
                        try {
                            Thread.sleep(100);//adjust to 100?
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            thread.start();
        }


    }


}
