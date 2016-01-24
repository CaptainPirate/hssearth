package com.hss.saveEarth.game;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.*;

import com.hss.savaEarth.*;
import com.hss.saveEarth.R;

import android.graphics.*;
import android.graphics.drawable.*;
import android.util.Log;
import android.content.res.*;
import android.content.*;
import android.app.*;


public class GameScreen extends Screen {
    long startTime = System.nanoTime();
	World world;
    private Drawable pauseRectangle;

    public static int iconSize;
	Paint paint = new Paint();
	RectF rect = new RectF();

	Bitmap bitmap,blue;
	BitmapDrawable bitmapD,blueD;
	GradientDrawable gradient;

	Context r;
        
    public GameScreen(Game game) {
        super(game);
		r = (Context) game;
		bitmapD = (BitmapDrawable) r.getResources().getDrawable(R.drawable.dot);
		blueD = (BitmapDrawable) r.getResources().getDrawable(R.drawable.blu);
		bitmap = bitmapD.getBitmap();
		blue = blueD.getBitmap();
		iconSize=game.getGraphics().getWidth()/10;
		world = new World(game);	
		world.renew();
		rect.top = world.core.coords.y - world.core.shieldRadius;
		rect.left = world.core.coords.x - world.core.shieldRadius;
		rect.bottom = world.core.coords.y + world.core.shieldRadius;
		rect.right = world.core.coords.x + world.core.shieldRadius;

		paint.setAntiAlias(true);
		paint.setStrokeWidth(0.0F);
		
		// This gradient looks quite smooth, but not perfect
		gradient = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
				new int[]{0xff001319, 0xff013e3f});
		gradient.setGradientType(GradientDrawable.RADIAL_GRADIENT);
		gradient.setGradientRadius((int) world.offScreenRadius);
		gradient.setDither(false);
		gradient.setGradientCenter(0.5F, 0.5F);
		gradient.setBounds(new Rect(0, 0, game.getGraphics().getWidth(),
				   	game.getGraphics().getHeight()));
		
		paint.setTextSize(((float)game.getGraphics().getHeight()) / 30F);
		paint.setTextAlign(Paint.Align.CENTER);
		
		//hss add for pause start
		pauseRectangle =  r.getResources().getDrawable(R.drawable.pause_cell);
		//hss add for pause end
    }
    
    @Override
    public void update(float deltaTime) {
		world.update(deltaTime);
    }

    @Override
    public void present(float deltaTime) {//游戏界面 hss tag
    Canvas c = game.getGraphics().getCanvas();    
	gradient.draw(c);
	paint.setStyle(Paint.Style.FILL_AND_STROKE);
	if(world.core.shieldEnergy > 0.0F)
	{
		paint.setColor(0xff003cca);//护盾颜色 hss tag
		paint.setAlpha((int) (80.0F +
				   	(255.0F - 80.0F) * world.core.shieldEnergy));
		c.drawCircle(world.core.coords.x, world.core.coords.y,
			world.core.shieldRadius, paint);
		paint.setAlpha(255);
	}
	paint.setColor(0xff19dbe2);//地心颜色 hss tag
	c.drawCircle(world.core.coords.x, world.core.coords.y,
		   	world.core.maxRadius * world.core.health,
			paint);
	paint.setStyle(Paint.Style.STROKE);
	paint.setColor(0xffffffff);//挡板颜色 hss tag
	paint.setStrokeWidth(Core.SHIELD_WIDTH);
	c.drawArc(rect, (360.0F - world.core.angle),
			(360.0F - world.core.GAP_ANGLE), false, paint);
	paint.setStrokeWidth(0.0F);
	paint.setStyle(Paint.Style.FILL_AND_STROKE);
	Iterator<Dot> iterator = world.dots.iterator();
	while(iterator.hasNext())
	{
		int color = 0;
		Dot dot = iterator.next();
		if(dot.type == Dot.Type.Enemy)
			//color = 0xffe2192e;
			c.drawBitmap(bitmap, dot.coords.x, dot.coords.y, paint);
		else if(dot.type == Dot.Type.Health)
			//color = 0xff19dbe2;
			c.drawBitmap(blue, dot.coords.x, dot.coords.y, paint);
		else if(dot.type == Dot.Type.Shield){
			color = 0xff003cca;
			paint.setColor(color);
			c.drawCircle(dot.coords.x, dot.coords.y,
					dot.maxRadius * dot.energy, paint);
			//color = 0xff003cca;
		}
			
		//hss add start will delete
//		if(dot.type == Dot.Type.Enemy){
//			//drawPauseButton(c);
//			c.drawBitmap(bitmap, dot.coords.x, dot.coords.y, paint);
//		}else{
//			//hss add start will delete
//		paint.setColor(color);
//		c.drawCircle(dot.coords.x, dot.coords.y,
//				dot.maxRadius * dot.energy, paint);
//		}
    }

	drawPauseButton(c);//hss add for pause
	if(world.state == World.GameState.Running)
		drawMessage(world.getTime(), c);
	else if(world.state == World.GameState.Ready)
		drawMessage(r.getString(R.string.ready), c);
	else if(world.state == World.GameState.Paused)
		drawMessage(r.getString(R.string.paused), c);
	else if(world.state == World.GameState.GameOver)
		drawMessage(r.getString(R.string.game_over)+
				"\n"+r.getString(R.string.your_time) +  " " + world.getTime() +
				"\n\n" + r.getString(R.string.game_url), c);
	}

	private void drawMessage(String message, Canvas c)
	{
		float y = paint.getTextSize();
		for(String line: message.split("\n"))
		{
		// Draw black stroke
		paint.setStrokeWidth(2F);
		paint.setColor(0xff000000);
	    paint.setStyle(Paint.Style.STROKE);

		c.drawText(line, c.getWidth()/2F, y, paint);
		// Draw white text
		paint.setStrokeWidth(0.0F);
		paint.setColor(0xffffffff);//hss tag 时间颜色与提示字体
	    paint.setStyle(Paint.Style.FILL);

		c.drawText(line, c.getWidth()/2F, y, paint);

		y += paint.getTextSize();
		}
	}

	//hss add start
	private void drawPauseButton(Canvas canvas) {
        drawDrawable(canvas,pauseRectangle,iconSize/4,iconSize/4,iconSize+iconSize/4,iconSize+iconSize/4);
        drawDrawable(canvas,r.getResources().getDrawable(R.drawable.pause_game),iconSize/4,iconSize/4,iconSize+iconSize/4,iconSize+iconSize/4);
    }
	 private void drawDrawable(Canvas canvas, Drawable draw, int startingX, int startingY, int endingX, int endingY) {
	        draw.setBounds(startingX, startingY, endingX, endingY);
	        draw.draw(canvas);
	 }
	 //hss add end
    @Override
    public void pause() {
		world.state = World.GameState.Paused;
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
    }            
}
