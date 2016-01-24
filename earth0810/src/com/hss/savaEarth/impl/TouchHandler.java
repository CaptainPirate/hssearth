package com.hss.savaEarth.impl;

import java.util.List;

import com.hss.savaEarth.Input.TouchEvent;

import android.view.View.OnTouchListener;


public interface TouchHandler extends OnTouchListener
{
    public boolean isTouchDown();
    
    public int getTouchX();
    
    public int getTouchY();
    
    public List<TouchEvent> getTouchEvents();
}
