package com.semenoh.livewallpaper.nyancat;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;

public class Star extends Point{
	public static final int STAR_STATES_COUNT = 6;
	int state = 0;
	int color = 0xFFFFFFFF;
		
	public Star (int x, int y, int state){
		super(x,y);
		this.state = state;
	}
	
	void Draw(Canvas c){
		Paint p = new Paint();
		p.setColor(color);
		c.save();
		switch(state){
		case 0:
			c.drawRect(x - 3, y - 3, x + 3, y + 3, p); //center 6x6
			break;
		case 1:
			c.drawRect(x - 9, y - 3, x - 3, y + 3, p); //left 6x6
			c.drawRect(x - 3, y - 9, x + 3, y - 3, p); //top 6x6
			c.drawRect(x + 3, y - 3, x + 9, y + 3, p); //right 6x6
			c.drawRect(x - 3, y + 3, x + 3, y + 9, p); //bottom 6x6
			break;
		case 2:
			c.drawRect(x - 15, y - 3, x - 3, y + 3, p); //left 12x6
			c.drawRect(x - 3, y - 15, x + 3, y - 3, p); //top 6x12
			c.drawRect(x + 3, y - 3, x + 15, y + 3, p); //right 12x6
			c.drawRect(x - 3, y + 3, x + 3, y + 15, p); //bottom 6x12
			break;
		case 3:
			c.drawRect(x - 3, y - 3, x + 3, y + 3, p); //center 6x6

			c.drawRect(x - 21, y - 3, x - 9, y + 3, p); //left 12x6
			c.drawRect(x - 3, y - 21, x + 3, y - 9, p); //top 6x12
			c.drawRect(x + 9, y - 3, x + 21, y + 3, p); //right 12x6
			c.drawRect(x - 3, y + 9, x + 3, y + 21, p); //bottom 6x12
			break;
		case 4:
			c.drawRect(x - 21, y - 3, x - 15, y + 3, p); //left 6x6
			c.drawRect(x - 3, y - 21, x + 3, y - 15, p); //top 6x6
			c.drawRect(x + 15, y - 3, x + 21, y + 3, p); //right 6x6
			c.drawRect(x - 3, y + 15, x + 3, y + 21, p); //bottom 6x6
			break;
		case 5:
			c.drawRect(x - 21, y - 3, x - 15, y + 3, p); //left 6x6
			c.drawRect(x - 3, y - 21, x + 3, y - 15, p); //top 6x6
			c.drawRect(x + 15, y - 3, x + 21, y + 3, p); //right 6x6
			c.drawRect(x - 3, y + 15, x + 3, y + 21, p); //bottom 6x6

			c.drawRect(x - 14, y - 14, x - 9, y - 9, p); //lt 5x5
			c.drawRect(x + 9, y - 14, x + 14, y - 9, p); //rt 6x6
			c.drawRect(x + 9, y + 9, x + 14, y + 14, p); //rb 6x6
			c.drawRect(x - 14, y + 9, x - 9, y + 14, p); //lb 6x6
			break;
			default:;
		}
		c.restore();
	}
}
