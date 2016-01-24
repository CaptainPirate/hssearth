package com.hss.savaEarth.impl;

import java.util.ArrayList;
import java.util.List;

import com.hss.savaEarth.Pool;
import com.hss.savaEarth.Input.TouchEvent;
import com.hss.savaEarth.Pool.PoolObjectFactory;
import com.hss.saveEarth.game.GameScreen;

import android.view.MotionEvent;
import android.view.View;


public class SingleTouchHandler implements TouchHandler {
    boolean isTouched;
    View mview;//hss add for pause
    int touchX;
    int touchY;
    Pool<TouchEvent> touchEventPool;
    List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
    List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();
    
    public SingleTouchHandler(View view) {
    	mview = view;
        PoolObjectFactory<TouchEvent> factory = new PoolObjectFactory<TouchEvent>() {
            @Override
            public TouchEvent createObject() {
                return new TouchEvent();
            }            
        };
        touchEventPool = new Pool<TouchEvent>(factory, 100);
        view.setOnTouchListener(this);
    }
    
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        synchronized(this) {
            TouchEvent touchEvent = touchEventPool.newObject();
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchEvent.type = TouchEvent.TOUCH_DOWN;
                isTouched = true;
                break;
            case MotionEvent.ACTION_MOVE:
                touchEvent.type = TouchEvent.TOUCH_DRAGGED;
                isTouched = true;
                break;
            case MotionEvent.ACTION_CANCEL:                
            case MotionEvent.ACTION_UP:
                touchEvent.type = TouchEvent.TOUCH_UP;
//                if(event.getX()>GameScreen.iconSize/2&&event.getX()<(GameScreen.iconSize+GameScreen.iconSize/2)){//hss will delete add
//                	
//                }
                isTouched = false;
                break;
            }
            
            touchEvent.x = touchX = (int) event.getX();
            touchEvent.y = touchY = (int) event.getY();
            touchEventsBuffer.add(touchEvent);                        
            
            return true;
        }
    }

    @Override
    public boolean isTouchDown() {
        synchronized(this) {
                return isTouched;
        }
    }

    @Override
    public int getTouchX() {
        synchronized(this) {
            return touchX;
        }
    }

    @Override
    public int getTouchY() {
        synchronized(this) {
            return touchY;
        }
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        synchronized(this) {     
            int len = touchEvents.size();
            for( int i = 0; i < len; i++ )
                touchEventPool.free(touchEvents.get(i));
            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            return touchEvents;
        }
    }
}