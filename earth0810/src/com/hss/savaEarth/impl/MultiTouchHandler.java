package com.hss.savaEarth.impl;

import java.util.ArrayList;
import java.util.List;

import com.hss.savaEarth.Pool;
import com.hss.savaEarth.Input.TouchEvent;
import com.hss.savaEarth.Pool.PoolObjectFactory;
import com.hss.saveEarth.game.GameScreen;

import android.view.MotionEvent;
import android.view.View;
import android.annotation.SuppressLint;
import android.util.*;


@SuppressLint("NewApi")
public class MultiTouchHandler implements TouchHandler {
    boolean[] isTouched = new boolean[20];

    //hss add for icon press start
    private float x;
    private float y;
    private float previousX;
    private float previousY;
    private float startingX;
    private float startingY;
    private AndroidFastRenderView mview;
  //hss add for icon press end
    int[] touchX = new int[20];
    int[] touchY = new int[20];
    Pool<TouchEvent> touchEventPool;
    List<TouchEvent> touchEvents = new ArrayList<TouchEvent>();
    List<TouchEvent> touchEventsBuffer = new ArrayList<TouchEvent>();

    public MultiTouchHandler(AndroidFastRenderView view) {
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

    @SuppressLint("NewApi")
	@Override
    public boolean onTouch(View v, MotionEvent event) {
        synchronized (this) {
            int action = event.getAction() & MotionEvent.ACTION_MASK;
            int pointerIndex = (event.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT;
            int pointerId = event.getPointerId(pointerIndex);
            TouchEvent touchEvent;

            switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                touchEvent = touchEventPool.newObject();
                touchEvent.type = TouchEvent.TOUCH_DOWN;
                touchEvent.pointer = pointerId;
                previousX = startingX = touchEvent.x = touchX[pointerId] = (int) event
                        .getX(pointerIndex);
                previousY = startingY = touchEvent.y = touchY[pointerId] = (int) event
                        .getY(pointerIndex);
                isTouched[pointerId] = true;
                touchEventsBuffer.add(touchEvent);
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                touchEvent = touchEventPool.newObject();
                touchEvent.type = TouchEvent.TOUCH_UP;
                touchEvent.pointer = pointerId;
                x=touchEvent.x = touchX[pointerId] = (int) event
                        .getX(pointerIndex);
                y=touchEvent.y = touchY[pointerId] = (int) event
                        .getY(pointerIndex);
                
                //hss add for icon toach start
                if (inRange(GameScreen.iconSize/4,x,GameScreen.iconSize/4+GameScreen.iconSize)&&
                		inRange(GameScreen.iconSize/4,y,GameScreen.iconSize/4+GameScreen.iconSize)) {
                	if(mview.running){
                		mview.pause();
                	}else{
                		mview.resume();
                	}
                	
                }
                //hss add for icon toach end
                
                isTouched[pointerId] = false;
                touchEventsBuffer.add(touchEvent);
                break;

            case MotionEvent.ACTION_MOVE:
                int pointerCount = event.getPointerCount();
                for (int i = 0; i < pointerCount; i++) {
                    pointerIndex = i;
                    pointerId = event.getPointerId(pointerIndex);

                    touchEvent = touchEventPool.newObject();
                    touchEvent.type = TouchEvent.TOUCH_DRAGGED;//滑动
                    touchEvent.pointer = pointerId;
                    previousX = startingX = touchEvent.x = touchX[pointerId] = (int) event
                            .getX(pointerIndex);
                    previousY = startingY = touchEvent.y = touchY[pointerId] = (int) event
                            .getY(pointerIndex);
                    touchEventsBuffer.add(touchEvent);
                }
                break;
            }

            return true;
        }
    }

    @Override
    public boolean isTouchDown() {
        synchronized (this) {
			return (getMaxPointer() >= 0);
        }
    }

    @Override
    public int getTouchX() {
        synchronized (this) {
	int pointer = getMaxPointer();
            if (pointer < 0 || pointer >= 20)
                return 0;
            else
                return touchX[pointer];
        }
    }

    @Override
    public int getTouchY() {
        synchronized (this) {
	int pointer = getMaxPointer();
            if (pointer < 0 || pointer >= 20)
                return 0;
            else
                return touchY[pointer];
        }
    }

    @Override
    public List<TouchEvent> getTouchEvents() {
        synchronized (this) {
            int len = touchEvents.size();
            for (int i = 0; i < len; i++)
                touchEventPool.free(touchEvents.get(i));
            touchEvents.clear();
            touchEvents.addAll(touchEventsBuffer);
            touchEventsBuffer.clear();
            return touchEvents;
        }
    }

	private int getMaxPointer()
	{
		for(int i = 20 - 1; i >= 0; i--)
		{
			if(isTouched[i])
			return i;
		}
		return -1;
	}
	
	//hss add for icon press start
	private float pathMoved() {
        return (x - startingX) * (x - startingX) + (y - startingY) * (y - startingY);
    }

    private boolean iconPressed(int sx, int sy) {
        return isTap(1) && inRange(sx, x, sx + GameScreen.iconSize)
                && inRange(sy, y, sy + GameScreen.iconSize);
    }

    private boolean inRange(float starting, float check, float ending) {
        return (starting <= check && check <= ending);
    }

    private boolean isTap(int factor) {
        return pathMoved() <= GameScreen.iconSize * factor;
    }
	//hss add for icon press end
}
