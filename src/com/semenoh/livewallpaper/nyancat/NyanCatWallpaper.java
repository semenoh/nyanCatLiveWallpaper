package com.semenoh.livewallpaper.nyancat;

import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

public class NyanCatWallpaper extends AnimationWallpaper {
	
	private final int COLOR_WHITE = 0xFFFFFFFF;
	private final int COLOR_BLACK = 0xFF000000;
	private final int COLOR_GRAY = 0xFF999999;
	
	private final int COLOR_BODY_COOKY = 0xFFFFCC99;
	private final int COLOR_BODY_SUGAR = 0xFFFF99FF;
	private final int COLOR_BODY_DOTS = 0xFFFF3399;
	private final int COLOR_HEAD_FLUSH = 0xFFFF9999;
	
	private final int DIM_BODY_WIDTH = 120;
	private final int DIM_BODY_HEIGHT = 103;

	@Override
	public Engine onCreateEngine() {
		return new AnimationEngine() {
			SurfaceHolder holder = getSurfaceHolder();
			Canvas canvas = null;
			Paint p = new Paint();

			int offsetter = 6;
			
			int iter = 0;
			int state = 0;
			
			final int STARS_COUNT = 6;
			boolean needInit = true;
			int ofs = 0;
			
			Vector<Star> mStars = new Vector<Star>();
			
			@Override
			protected void drawFrame() {
				if (iter % 5 == 0){
					iter = 0;
					try {
						canvas = holder.lockCanvas();
						if (null != canvas) {
							canvas.save();
							canvas.drawRGB(0, 51, 102);
							drawStars(mWidth, mHeight);
							canvas.translate(mWidth/2, 0);
							
							int x = ofs+mOffset + (mWidth - DIM_BODY_WIDTH)/2;
							int y = (mHeight - DIM_BODY_HEIGHT)/2 - offsetter*((state+1)%2);

							drawRainbow(canvas, x + 12, y, state);
							drawLegs(canvas, x-18+((0 == state%3)? offsetter : 0), y+DIM_BODY_HEIGHT);
							drawBody(canvas, x, y);
							drawHead(canvas, x+DIM_BODY_WIDTH/2+6+((0 == state%7) ? offsetter : 0), y+DIM_BODY_HEIGHT/2-30-((0 == state%4) ? offsetter : 0));
							drawTail(canvas, x, y + DIM_BODY_HEIGHT/2, state%7);

							if (!mTouched){
								//moving back to center after swipe
								if (-mWidth / 2 > ofs + mOffset) ofs += 4*offsetter;
								if (-mWidth / 2 < ofs + mOffset) ofs -= 4*offsetter;
							} else {
								//adding stars under your finger
								mTouchedStars.add(new Star(mXTouched, mYTouched, 0));
							}
							
							canvas.restore();
						}
					} finally {
						if (null != canvas){
							holder.unlockCanvasAndPost(canvas);
						}
					}
					++state;
				}
				++iter;
			}
			
			/**
			 * Draws cat's legs.
			 * @param c {@caode Canvas} for drawing.
			 * @param x X coordinate of bottom-left corner
			 * @param y Y coordinate of bottom-left corner
			 */
			private void drawLegs(Canvas c, int x, int y) {
				p.setColor(COLOR_BLACK);
				c.drawRect(x,		y + 12,	x + 18,	y - 6,	p);
				c.drawRect(x+6,		y + 6,	x + 24,	y - 12,	p);
				c.drawRect(x+12,	y,		x + 30,	y - 18,	p);
				p.setColor(COLOR_GRAY);
				c.drawRect(x+6,		y + 6,	x + 18,	y - 6,	p);
				c.drawRect(x+12,	y,		x + 24,	y - 12,	p);

				for (int s : new int[]{0, 42, 74}){
					p.setColor(COLOR_BLACK);
					c.drawRect(s+x+30,	y,		s+x + 54,	y + 6,	p);
					c.drawRect(s+x+36,	y + 6,	s+x + 54,	y + 12,	p);
					p.setColor(COLOR_GRAY);
					c.drawRect(s+x+36,	y,		s+x + 48,	y + 6,	p);
				}
			}
			

			/**
			 * Draws moving stars
			 * @param w The screen width
			 * @param h The screen height
			 */
			private void drawStars(int w, int h){
				if (needInit){
					for (int i = 0; i < STARS_COUNT; ++i)
						bornStar(i, w, h);
					needInit = false;
				}
				
				for (Star star : mStars){
					if (Star.STAR_STATES_COUNT >= star.state){
						star.Draw(canvas);
						star.x -= mWidth/Star.STAR_STATES_COUNT;
					}
					if (Star.STAR_STATES_COUNT == star.state) {
						star.x = (new Random()).nextInt(w/2)+w/2;
						star.y = (new Random()).nextInt(h);
						star.state = (new Random()).nextInt(Star.STAR_STATES_COUNT/2);
					}
					++star.state;
					
				}

				if (!mTouchedStars.isEmpty()){
					for (Iterator<Star> i = mTouchedStars.iterator(); i.hasNext();){
						Star star = i.next();
						if (Star.STAR_STATES_COUNT >= star.state){
							star.Draw(canvas);
//							star.x -= mWidth/Star.STAR_STATES_COUNT;
						}
						if (Star.STAR_STATES_COUNT == star.state) {
							i.remove();
						}
						++star.state;
					}
				}
			}			

			/**
			 * {@code drawStars} helper method. Creates new star in random position
			 * @param i index of star in array
			 * @param w screen width
			 * @param h screen height
			 */
			private void bornStar(int i, int w, int h){
				int x = (new Random()).nextInt(w/2)+w/2;
				int y = (new Random()).nextInt(h/STARS_COUNT) + i*(h/STARS_COUNT);
				int s = (new Random()).nextInt(Star.STAR_STATES_COUNT/2);
				
				mStars.add(new Star(x, y, s));
			}
			
			/**
			 * Draws tail behind the cat in colors of the rainbow
			 * @param canvas {@code Canvas} for drawing
			 * @param x X coordinate of right-top corner
			 * @param y Y coordinate of right-top corner
			 * @param state
			 */
			private void drawRainbow(Canvas canvas, int x, int y, int state){
				final int lineW = 26;
				final int lineH = 17;
				final int lineShift = 5;
				final int[] colors = {
							0xFFFF0000, //red
							0xFFFF9900, //orange
							0xFFFFFF00, //yellow
							0xFF33FF00, //green
							0xFF0099FF, //blue
							0xFF6633FF, //violet
						};
				
				int right = x;
				int left =  right - lineW;

				int fase = 0;
				Paint paint = new Paint();
				for(left = x - lineW; left > -mWidth/2 - lineW; left -= lineW, right -= lineW){
					for (int i = 0; i < colors.length; ++i){ //vertical cycle
						int top = y + lineH*i + ((state+fase)%2)*lineShift;
						int bottom = top + lineH;
						paint.setColor(colors[i]);
						canvas.drawRect(left, top, right, bottom, paint);
					}
					++fase;					
				}
			}
			
			/**
			 * Draws cooky instead of cat's body 
			 * @param c {@code Canvas}
			 * @param x X coordinate of left-top corner
			 * @param y Y coordinate of left-top corner
			 */
			private void drawBody(Canvas c, int x, int y){
				p.setColor(COLOR_BLACK);
				c.drawRect(x, y + 12, x + 6, y + DIM_BODY_HEIGHT - 12, p); //left line
				c.drawRect(x + 12, y, x + DIM_BODY_WIDTH - 12, y + 6, p); //top line
				c.drawRect(x + DIM_BODY_WIDTH - 6, y + 12, x + DIM_BODY_WIDTH, y + DIM_BODY_HEIGHT - 12, p); //right line
				c.drawRect(x + 12, y + DIM_BODY_HEIGHT - 6, x + DIM_BODY_WIDTH - 12, y + DIM_BODY_HEIGHT, p); //bottomline
				
				p.setColor(COLOR_BODY_COOKY);
				c.drawRect(x + 6, y + 6, x + DIM_BODY_WIDTH - 6, y + DIM_BODY_HEIGHT - 6, p); //bottomline
				
				p.setColor(COLOR_BLACK);
				drawDot(c, x + 6, y + 6, p); //left top corner
				drawDot(c, x + DIM_BODY_WIDTH - 12, y + 6, p); //right top corner
				drawDot(c, x + DIM_BODY_WIDTH - 12, y + DIM_BODY_HEIGHT - 12, p); //right bottom corner
				drawDot(c, x + 6, y + DIM_BODY_HEIGHT - 12, p); //left bottom corner

				p.setColor(COLOR_BODY_SUGAR);
				c.drawRect(x + 12, y + 24, x + 18, y + DIM_BODY_HEIGHT - 24, p); 
				c.drawRect(x + 18, y + 18, x + 24, y + DIM_BODY_HEIGHT - 18, p); 
				c.drawRect(x + 24, y + 12, x + DIM_BODY_WIDTH - 24, y + DIM_BODY_HEIGHT - 12, p); 
				c.drawRect(x + DIM_BODY_WIDTH - 24, y + 18, x + DIM_BODY_WIDTH - 18, y + DIM_BODY_HEIGHT - 18, p); 
				c.drawRect(x + DIM_BODY_WIDTH - 18, y + 24, x + DIM_BODY_WIDTH - 12, y + DIM_BODY_HEIGHT - 24, p); 

				p.setColor(COLOR_BODY_DOTS);
				drawDot(c, x + 6*9, y + 6*3, p); 
				drawDot(c, x + 6*12, y + 6*3, p); 
				drawDot(c, x + 6*4, y + 6*4, p); 
				drawDot(c, x + 6*16, y + 6*5, p); 
				drawDot(c, x + 6*8, y + 6*7, p); 
				drawDot(c, x + 6*5, y + 6*9, p); 
				drawDot(c, x + 6*9, y + 6*10, p); 
				drawDot(c, x + 6*3, y + 6*11, p); 
				drawDot(c, x + 6*7, y + 6*13, p); 
				drawDot(c, x + 6*4, y + 6*14, p); 
			}
			
			/**
			 * Draws cat's head
			 * @param c {@code Canvas} for drawing
			 * @param x X coordinate of top-left corner
			 * @param y Y coordinate of top-left corner
			 */
			private void drawHead(Canvas c, int x, int y){
				p.setColor(COLOR_GRAY);
				c.drawRect(x+6,		y+6,	x+18,	y+12,	p); //left
				c.drawRect(x+6,		y+12,	x+24,	y+18,	p); //ear

				c.drawRect(x+66,	y+6,	x+78,	y+12,	p); //right
				c.drawRect(x+60,	y+12,	x+78,	y+18,	p); //ear
				
				c.drawRect(x,		y+18,	x+82,	y+66,	p); 
				c.drawRect(x+12,	y+66,	x+72,	y+72,	p); 
				
				p.setColor(COLOR_BLACK);
				c.drawRect(x-6,		y+30,	x,		y+60,	p);		
				c.drawRect(x,		y+6,	x+6,	y+30,	p);		
				c.drawRect(x,		y+60,	x+6,	y+66,	p);		
				c.drawRect(x+6,		y+66,	x+12,	y+72,	p);		
				c.drawRect(x+6,		y,		x+18,	y+6,	p);
				c.drawRect(x+18,	y+6,	x+24,	y+12,	p);
				c.drawRect(x+24,	y+12,	x+30,	y+18,	p);
				c.drawRect(x+30,	y+18,	x+54,	y+24,	p);
				c.drawRect(x+54,	y+12,	x+60,	y+18,	p);
				c.drawRect(x+60,	y+6,	x+66,	y+12,	p);
				c.drawRect(x+66,	y,		x+78,	y+6,	p);
				c.drawRect(x+78,	y+6,	x+84,	y+30,	p);
				c.drawRect(x+84,	y+30,	x+90,	y+60,	p);
				c.drawRect(x+78,	y+60,	x+84,	y+66,	p);
				c.drawRect(x+72,	y+66,	x+78,	y+72,	p);
				c.drawRect(x+12,	y+72,	x+72,	y+78,	p);
				
				//nose
				drawDot(c, x+48, y+42, p);
				
				//mouthe
				drawDot(c, x+24, y+54, p);
				drawDot(c, x+42, y+54, p);
				drawDot(c, x+60, y+54, p);
				c.drawRect(x+24,	y+60,	x+66,	y+66,	p);
				
				//eyes
				c.drawRect(x+18,	y+36,	x+30,	y+48,	p);
				c.drawRect(x+60,	y+36,	x+72,	y+48,	p);
				
				p.setColor(COLOR_WHITE);
				drawDot(c, x+18, y+36, p);
				drawDot(c, x+60, y+36, p);
				
				p.setColor(COLOR_HEAD_FLUSH);
				c.drawRect(x+6,		y+48,	x+18,	y+60,	p);
				c.drawRect(x+72,	y+48,	x+84,	y+60,	p);
			}
			
			/**
			 * Helper method for drawing all the figures. Draws rectangle 6x6 pixels.
			 * @param c {@code Canvas} for drawing
			 * @param x X coordinate of top-left corner
			 * @param y Y coordinate of top-left corner
			 * @param p {@code Paint}
			 */
			void drawDot(Canvas c, int x, int y, Paint p){
				c.drawRect(x, y, x + 6, y + 6, p); 				
			}
			
			/**
			 * Draws cat's tail.
			 * @param c {@code Canvas} for drawing.
			 * @param x X coordinate of right-center point
			 * @param y Y coordinate of right-center point
			 * @param state Number of animation frame in range 0..7
			 */
			private void drawTail(Canvas c, int x, int y, int state){
				Paint p = new Paint();
				switch(state){
				case 0:
					p.setColor(COLOR_BLACK);
					c.drawRect(x - 11, y, x, y + 4, p);
					c.drawRect(x - 22, y + 4, x - 11, y + 8, p);
					c.drawRect(x - 26, y + 8, x - 22, y + 12, p);
					c.drawRect(x - 30, y + 12, x, y + 16, p);
					c.drawRect(x - 30, y + 16, x - 11, y + 20, p);
					c.drawRect(x - 26, y + 20, x - 15, y + 24, p);
					p.setColor(COLOR_GRAY);
					c.drawRect(x - 11, y + 4, x, y + 8, p);
					c.drawRect(x - 22, y + 8, x, y + 12, p);
					c.drawRect(x - 26, y + 12, x - 15, y + 20, p);
					break;
				case 1:
				case 7:
					p.setColor(COLOR_BLACK);
					c.drawRect(x-6,		y,		x,		y+6,  p);
					c.drawRect(x-24,	y+6,	x,		y+12, p);
					c.drawRect(x-36,	y+12,	x-24,	y+18, p);
					c.drawRect(x-36,	y+18,	x-30,	y+24, p);
					c.drawRect(x-30,	y+24,	x-6,	y+30, p);
					c.drawRect(x-12,	y+18,	x,		y+24, p);
					p.setColor(COLOR_GRAY);
					c.drawRect(x-24,	y+12,	x,		y+18, p);
					c.drawRect(x-30,	y+18,	x-12,	y+24, p);
					break;
				case 2:
				case 6:
					p.setColor(COLOR_BLACK);
					c.drawRect(x-36,	y-12,	x-12,	y-6,  p);
					c.drawRect(x-42,	y-6,	x,		y+6,  p);
					c.drawRect(x-30,	y+6,	x-6,	y+12, p);
					c.drawRect(x-12,	y+12,	x,		y+18, p);
					p.setColor(COLOR_GRAY);
					c.drawRect(x-36,	y-6,	x-18,	y,    p);
					c.drawRect(x-30,	y,		x-6,	y+6,  p);
					c.drawRect(x-6,		y+6,	x,		y+12, p);
					break;
				case 3:
				case 5:
					p.setColor(COLOR_BLACK);
					c.drawRect(x-36,	y-24,	x-12,	y-6,  p);
					c.drawRect(x-30,	y-18,	x-6,	y,    p);
					c.drawRect(x-24,	y-12,	x,		y+6,  p);
					c.drawRect(x-18,	y+6,	x,		y+12, p);
					c.drawRect(x-6,		y+12,	x,		y+18, p);
					p.setColor(COLOR_GRAY);
					c.drawRect(x-30,	y-18,	x-18,	y-12, p);
					c.drawRect(x-24,	y-12,	x-12,	y-6,  p);
					c.drawRect(x-18,	y-6,	x-6,	y,    p);
					c.drawRect(x-12,	y,		x,		y+6,  p);
					break;
				case 4:
					p.setColor(COLOR_BLACK);
					c.drawRect(x-30,	y-18,	x-18,	y+6,  p);
					c.drawRect(x-36,	y-12,	x-12,	y,    p);
					c.drawRect(x-12,	y-6,	x,		y,    p);
					c.drawRect(x-24,	y+6,	x-12,	y+12, p);
					c.drawRect(x-12,	y+12,	x,		y+18, p);
					p.setColor(COLOR_GRAY);
					c.drawRect(x-30,	y-12,	x-18,	y,    p);
					c.drawRect(x-24,	y,		x,		y+6,  p);
					c.drawRect(x-12,	y+6,	x,		y+12, p);
					break;
				default:;
				}
				
			}
		};
	}
	
		
}
