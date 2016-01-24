package com.hss.savaEarth.impl;

import java.io.*;

import com.hss.savaEarth.*;

import android.graphics.*;


public class AndroidGraphics implements Graphics
{
	Bitmap frameBuffer;
	Canvas canvas;

	public AndroidGraphics(Bitmap frameBuffer)
	{
		this.frameBuffer = frameBuffer;
		this.canvas = new Canvas(frameBuffer);
	}

	@Override
	public Canvas getCanvas()
	{
		return canvas;
	}

	@Override
	public int getWidth()
	{
		return canvas.getWidth();
	}

	@Override
	public int getHeight()
	{
		return canvas.getHeight();
	}
}
