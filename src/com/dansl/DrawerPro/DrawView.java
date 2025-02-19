package com.dansl.DrawerPro;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.widget.Toast;

public class DrawView extends SurfaceView {
	
	private Paint thePaint;
	private Paint thePaint2;
	private Paint eraserPaint;
	
	public float touchX = 0;
	public float touchY = 0;
	public float oldTouchX = 0.0f;
	public float oldTouchY = 0.0f;
	
	public Boolean isMouseDown = false;
	public Boolean antiAlias = true;
	public Boolean undoSet = false;
	public Boolean redoSet = false;

	public float[] pointsX;
	public float[] pointsY;
	
	public BitmapDrawable UndoIt;
	public BitmapDrawable RedoIt;
	
	public int curUndoPlacement = 0;
	
	public int count = 0;
	public int maxArrayNum = 90000;
	public int color = 0xFF000000;
	public int BGcolor = 0xFFFFFFFF;
	public int NextBGColor = 0xFFFFFFFF;
	public int brushSize1 = 1; //Main lines
	public int brushSize2 = 15; //Sketchy lines
	
	public Path drawPath;
	public Path drawPath2;
	public Path eraserPath;
	
	public Canvas mainCanvas;
	public Context theContext;
	
	public String toolStyle = "Simple";
	
	public Bitmap drawImage;
	
	public DrawView(Context context){
		super(context);
		
		theContext = context;
		
		thePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		thePaint.setDither(true);
		thePaint.setColor(color);
		thePaint.setStyle(Paint.Style.STROKE);
		thePaint.setStrokeJoin(Paint.Join.ROUND);
		thePaint.setStrokeCap(Paint.Cap.ROUND);
		thePaint.setStrokeWidth(brushSize1+1);
        
		thePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
		thePaint2.setDither(true);
		thePaint2.setColor(color);
		thePaint2.setStyle(Paint.Style.STROKE);
		thePaint2.setAlpha(90);
		thePaint2.setStrokeWidth(brushSize1);
		
		eraserPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		eraserPaint.setDither(true);
		eraserPaint.setColor(0xFFFFFFFF);
		eraserPaint.setStyle(Paint.Style.STROKE);
		eraserPaint.setStrokeJoin(Paint.Join.ROUND);
		eraserPaint.setStrokeCap(Paint.Cap.ROUND);
		eraserPaint.setStrokeWidth(brushSize2);
        
        pointsX = new float[maxArrayNum];
        pointsY = new float[maxArrayNum];
        
        drawPath = new Path();
        drawPath2 = new Path();
        eraserPath = new Path();
    	
        this.setDrawingCacheQuality(DRAWING_CACHE_QUALITY_HIGH);
    	this.setDrawingCacheEnabled(true);
        this.setBackgroundColor(BGcolor);
        
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		super.onDraw(canvas);
		Log.v("TOOL", "TOOL "+toolStyle);
		if(isMouseDown){
			
			if(toolStyle == "Simple"){
				
				drawPath.moveTo(oldTouchX, oldTouchY);
				drawPath.lineTo(touchX, touchY);
		
			}else if(toolStyle == "Eraser"){
				
				eraserPath.moveTo(oldTouchX, oldTouchY);
				eraserPath.lineTo(touchX, touchY);
		
			}else if(toolStyle == "Web"){
				pointsX[count] = touchX;
				pointsY[count] = touchY;
				
				drawPath.moveTo(oldTouchX, oldTouchY);
				drawPath.lineTo(touchX, touchY);
		
				for (int i = 0; i < count; ++i){
					float dx = pointsX[i] - pointsX[count];
					float dy = pointsY[i] - pointsY[count];
					float d = dx * dx + dy * dy;
		
					if (d < 2500 && Math.random() > 0.8){
						drawPath2.moveTo(pointsX[count], pointsY[count]);
						drawPath2.lineTo(pointsX[i], pointsY[i]);
					}
				}
			}else if(toolStyle == "Squares"){
			    
				float b = touchX - oldTouchX;
				float a = touchY - oldTouchY;
				float g = (float) 1.57079633;
				float e = (float) (Math.cos(g) * b - Math.sin(g) * a);
                float c = (float) (Math.sin(g) * b + Math.cos(g) * a);
             
                drawPath.moveTo(oldTouchX - e, oldTouchY - c);
                drawPath.lineTo(oldTouchX + e, oldTouchY + c);
                drawPath.lineTo(touchX + e, touchY + c);
                drawPath.lineTo(touchX - e, touchY - c);
                drawPath.lineTo(oldTouchX - e, oldTouchY - c);
                
            } else if(toolStyle == "Circles"){
			    
				float b = touchX - oldTouchX;
				float a = touchY - oldTouchY;
				float g = (float) 1.57079633;
				float e = (float) (Math.cos(g) * b - Math.sin(g) * a);
                float c = (float) (Math.sin(g) * b + Math.cos(g) * a);
                
                double dx = touchX-oldTouchX;
                double dy = touchY-oldTouchY;
                double dist = Math.sqrt(dx*dx + dy*dy);
                
                float xpos = (float) ((touchX+oldTouchX)*0.5);
                float ypos = (float) ((touchY+oldTouchY)*0.5);
             
                drawPath.moveTo(oldTouchX - e, oldTouchY - c);
                drawPath.addCircle(xpos, ypos, (float) (dist*0.5), Path.Direction.CW);
                
            } else if(toolStyle == "Sketchy"){
            	pointsX[count] = touchX;
            	pointsY[count] = touchY;
				
            	drawPath.moveTo(oldTouchX, oldTouchY);
            	drawPath.lineTo(touchX, touchY);
                
                for (int e = 0; e < count; ++e) {
                    float b = pointsX[e] - pointsX[count];
                    float a = pointsY[e] - pointsY[count];
                    float g = b * b + a * a;
                    if (g < 4000 && Math.random() > 0.6) {
                    	drawPath2.moveTo((float) (pointsX[count] + (b * 0.3)), (float) (pointsY[count] + (a * 0.3)));
                    	drawPath2.lineTo((float) (pointsX[e] - (b * 0.3)), (float) (pointsY[e] - (a * 0.3)));
                    }
                }
            }else if(toolStyle == "Fur"){
            	
            	pointsX[count] = touchX;
            	pointsY[count] = touchY;
            	
            	drawPath.moveTo(oldTouchX, oldTouchY);
            	drawPath.lineTo(touchX, touchY);
            	
            	 for (int j = 0; j < count; ++j) {
            		 float b = pointsX[j] - pointsX[count];
            		 float a = pointsY[j] - pointsY[count];
            		 float g = b * b + a * a;
            		 if (g < 2000 && Math.random() > 0.4) {
            			 drawPath.moveTo((float)(touchX + (b * 0.5)), (float)(touchY + (a * 0.5)));
            			 drawPath.lineTo((float)(touchX - (b * 0.5)), (float)(touchY - (a * 0.5)));
            		 }
            	 }            	
            }else if(toolStyle == "Long Fur"){
            	
            	
            	pointsX[count] = touchX;
            	pointsY[count] = touchY;
            	
            	 for (int ii = 0; ii < count; ++ii) {
            		 double e = -(Math.random());
            		 float b = pointsX[ii] - pointsX[count];
            		 float a = pointsY[ii] - pointsY[count];
            		 float h = b * b + a * a;
            		 if (h < 4000 && Math.random() > 0.4) {
            			 drawPath.moveTo((float)(pointsX[count] + (b * e)), (float)(pointsY[count] + (a * e)));
            			 drawPath.lineTo((float)(pointsX[ii] - (b * e) + Math.random() * 2), (float)(pointsY[ii] - (a * e) + Math.random() * 2));
            		 }
            	 }
            }
			
			oldTouchX = touchX;
            oldTouchY = touchY;
			
			if(drawPath != null){
				canvas.drawPath(drawPath, thePaint);
			}
			if(drawPath2 != null){
				canvas.drawPath(drawPath2, thePaint2);
			}
			if(eraserPath != null){
				canvas.drawPath(eraserPath, eraserPaint);
			}
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event){
		
		if(event.getAction() == MotionEvent.ACTION_DOWN){
        	isMouseDown = true;
        		 
        	oldTouchX = event.getX();
        	oldTouchY = event.getY();
        	touchX = event.getX();
        	touchY = event.getY();
        	
        	setUndo();
			
        }else if(event.getAction() == MotionEvent.ACTION_MOVE){
        	//Log.i("MOVIN", "MOVIN-X "+event.getX()+"MOVIN-Y"+event.getY());
        	touchX = event.getX();
        	touchY = event.getY();
        	
    		count ++;
        	
    		//Log.i("COUNT", "COUNT "+count);
        	if(toolStyle == "Fur" || toolStyle == "Squares" || toolStyle == "Long Fur" || toolStyle == "Eraser" || toolStyle == "Simple"){
        		if(count >= 50){
        			flatten();
        		}
        	}
        		
        	if(toolStyle == "Squares" || toolStyle == "Long Fur" || toolStyle == "Circles"){
        		invalidate();
        	}else{
        		invalidate((int)(touchX-70), (int)(touchY-70), (int)(touchX+70), (int)(touchY+70));
        	}
			
        	count %= maxArrayNum;
        }else if(event.getAction() == MotionEvent.ACTION_UP){
        	//isMouseDown = false;
        	invalidate();
        	setRedo();
        }
		return true;
	}
	
	public void clearAll() {
        BGcolor = NextBGColor;
        
        clear();
        
        resetBrushesPaint();
        
        RedoIt = null;
        UndoIt = null;
        undoSet = false;
		redoSet = false;
	}
	
	public void clear() {
		drawPath = new Path();
		drawPath2 = new Path();
		eraserPath = new Path();
    	
    	pointsX = new float[maxArrayNum];
        pointsY = new float[maxArrayNum];

        count = 0;
        
        this.destroyDrawingCache();
        this.setBackgroundColor(BGcolor);
        
    	invalidate();
	}
	
	public void setTool(String _tool){
		flatten();
		toolStyle = _tool;
		Log.v("TOOL", "TOOL "+toolStyle);
	}
	
	public void setColor(int _color){
		
		flatten();
		
		color = _color;
		
		resetBrushesPaint();
	}
	
	public void setBrushSize(int _size){
		if(_size <= 0){
			_size = 1;
		}
		if(toolStyle == "Eraser"){
			brushSize2 = _size;
		}else{
			brushSize1 = _size;
		}
		resetBrushesPaint();
	}
	
	public void resetBrushesPaint() {
		if(antiAlias == false){
			thePaint = new Paint();
			thePaint2 = new Paint();
			eraserPaint = new Paint();
		}else{
			thePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
			thePaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
			eraserPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		}
		
		thePaint.setDither(true);
		thePaint.setColor(color);
		thePaint.setStyle(Paint.Style.STROKE);
		thePaint.setStrokeJoin(Paint.Join.ROUND);
		thePaint.setStrokeCap(Paint.Cap.ROUND);
		thePaint.setStrokeWidth(brushSize1+1);
		
		thePaint2.setDither(true);
		thePaint2.setColor(color);
		thePaint2.setStyle(Paint.Style.STROKE);
		thePaint2.setAlpha(80);
		thePaint2.setStrokeWidth(brushSize1);
		
		eraserPaint.setDither(true);
		eraserPaint.setColor(BGcolor);
		eraserPaint.setStyle(Paint.Style.STROKE);
		eraserPaint.setStrokeJoin(Paint.Join.ROUND);
		eraserPaint.setStrokeCap(Paint.Cap.ROUND);
		eraserPaint.setStrokeWidth(brushSize2);
	}
	
	public void flatten(){
		BitmapDrawable b = new BitmapDrawable(Bitmap.createBitmap(this.getDrawingCache()));
		clear();
		this.setBackgroundDrawable(b.getCurrent());
	}
	
	public void setUndo(){
		BitmapDrawable b = new BitmapDrawable(Bitmap.createBitmap(this.getDrawingCache()));
		UndoIt = b;
		clear();
		this.setBackgroundDrawable(UndoIt.getCurrent());
		undoSet = true;
	}
	
	public void setRedo(){
		BitmapDrawable b = new BitmapDrawable(Bitmap.createBitmap(this.getDrawingCache()));
		RedoIt = b;
		clear();
		this.setBackgroundDrawable(RedoIt.getCurrent());
		
	}
	
	public void Undo(){
		if(undoSet == true){
			//curUndoPlacement--;
			clear();
			this.setBackgroundDrawable(UndoIt.getCurrent());
			Toast.makeText(theContext, "Undo", Toast.LENGTH_SHORT).show();
			undoSet = false;
			redoSet = true;
		}else if(redoSet == true){
			clear();
			this.setBackgroundDrawable(RedoIt.getCurrent());
			Toast.makeText(theContext, "Redo", Toast.LENGTH_SHORT).show();
			undoSet = true;
			redoSet = false;
		}
	}
	
	public String saveImage() {
		flatten();
		String pathName = "Sketch-"+System.currentTimeMillis()+".jpg";
		File sdCard = Environment.getExternalStorageDirectory();
		File myDir = new File(sdCard+"/Draw(er)");
		myDir.mkdirs();
		File file = new File(myDir, pathName);
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(file);
			Bitmap b = this.getDrawingCache();
			b.compress(CompressFormat.JPEG, 95, fos);
			
			try {
				fos.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			Uri uri = Uri.fromFile(file);
			theContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
			
			String output = myDir+"/"+pathName;
			return output;
		} catch (FileNotFoundException e) {
			String error = "ERROR: Is your SD card mounted?";
			return error;
		}
		
    }
	
	public void SetBGColor(int _color) {
		NextBGColor = _color;
		clearAll();
	}
	
}
