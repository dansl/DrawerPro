package com.dansl.DrawerPro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class DrawerPro extends Activity {
	
	private DrawView mDrawView;
	private Context theContext = this;
	private Boolean firstRun = true;
	private int timesLoaded = 0;
	private String[] toolsNameArray = {"Eraser", "Fur", "Long Fur", "Simple", "Sketchy", "Squares", "Circles", "Web"};
	public String SavedUrl;
	
	@Override
    public void onCreate(Bundle idraw) {
        super.onCreate(idraw);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        mDrawView = new DrawView(this);
        
        setContentView(mDrawView);
        
        ColorDialog ColorDialog = new ColorDialog(theContext, mDrawView, mDrawView.BGcolor, mDrawView, mDrawView.antiAlias, "bg");
    	ColorDialog.show();
        
	}
	
	@Override
	protected void onResume() {
		 super.onResume();
		 Resources res = this.getResources();
		Toast.makeText(this, res.getText(R.string.app_name) + " v"+ res.getText(R.string.version), Toast.LENGTH_SHORT).show();
		
		/*if(timesLoaded >= 3 || firstRun == true){
			firstRun = false;
			timesLoaded = 0;
        	showInfoPopup();
        }
		timesLoaded++;*/
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
	    SubMenu toolsMenu = menu.addSubMenu(0, 2, 0, "TOOL");
	    toolsMenu.add(1, 10, 0, toolsNameArray[0]);
	    toolsMenu.add(1, 11, 0, toolsNameArray[1]);
	    toolsMenu.add(1, 12, 0, toolsNameArray[2]);
	    toolsMenu.add(1, 13, 0, toolsNameArray[3]).setChecked(true);
	    toolsMenu.add(1, 14, 0, toolsNameArray[4]);
	    toolsMenu.add(1, 15, 0, toolsNameArray[5]);
	    toolsMenu.add(1, 16, 0, toolsNameArray[6]);
	    toolsMenu.add(1, 17, 0, toolsNameArray[7]);
	    toolsMenu.setGroupCheckable(1, true, true);
	    
	    //menu.add(0, 2, 0, "TOOL");
	    menu.add(0, 3, 0, "COLOR");
	    menu.add(0, 0, 0, "CLEAR");
	    menu.add(0, 1, 0, "SAVE");
	    menu.add(0, 4, 0, "EXIT");
	    return true;
	}
	
	private void showInfoPopup() {
		Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Draw(er)");
        builder.setMessage(	"Thanks for downloading Draw(er).\n" +
        					"Purchase the Pro version for more features.\n"+
        					" ¥ Set Canvas Color\n"+
        					" ¥ Set Brush Sizes\n"+
        					" ¥ Undo/Redo");
        builder.setPositiveButton("Buy Pro", new clickBuy());
        builder.setNegativeButton("Not Now", new clickBuy());
        builder.show();
	}
	
	public void showClearPopup() {
		Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Clear Sketch?");
        builder.setPositiveButton("Yes", new clickClear());
        builder.setNegativeButton("No", new clickClear());
        builder.show();
	}
	
	public void showShare(){
		Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Share Sketch?");
        builder.setPositiveButton("Yes", new clickShare());
        builder.setNegativeButton("No", new clickShare());
        builder.show();
	}
	
	public void shareIt(){
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("image/jpeg");
		share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://"+SavedUrl));
		startActivity(Intent.createChooser(share, "Share Sketch"));
	}
	
	public void buyIt(){
		String url = "market://search?q=pname:com.dansl.DrawerPro";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}
	
	public class clickClear implements OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int buttonInt) {
			//Log.v("DIALOG", "DIALOG "+dialog);
			if(buttonInt == -1){ //YES
	        	mDrawView.clearAll();
	        	Toast.makeText(theContext, "New Canvas", Toast.LENGTH_SHORT).show();
	        	ColorDialog ColorDialog = new ColorDialog(theContext, mDrawView, mDrawView.BGcolor, mDrawView, mDrawView.antiAlias, "bg");
	        	ColorDialog.show();
			}else if(buttonInt == -2){ //NO
				dialog.dismiss();
			}
		}
	}
	
	public class clickShare implements OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int buttonInt) {
			//Log.v("DIALOG", "DIALOG "+dialog);
			if(buttonInt == -1){ //YES
				shareIt();
			}else if(buttonInt == -2){ //NO
				dialog.dismiss();
			}
		}
	}
	
	public class clickBuy implements OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int buttonInt) {
			//Log.v("DIALOG", "DIALOG "+dialog);
			if(buttonInt == -1){ //YES
	        	Toast.makeText(theContext, "Thanks!", Toast.LENGTH_SHORT).show();
	        	buyIt();
			}else if(buttonInt == -2){ //NO
				dialog.dismiss();
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int theID = item.getItemId();
		if(theID >= 10 && theID < 20){
			item.setChecked(true);
			Toast.makeText(this, toolsNameArray[(theID-10)], Toast.LENGTH_SHORT).show();
			mDrawView.setTool((toolsNameArray[(theID-10)]));
			if(toolsNameArray[(theID-10)] == "Eraser"){
				BrushSize dialog1 = new BrushSize(this, mDrawView, mDrawView.brushSize2, mDrawView);
				dialog1.show();
			}else{
				BrushSize dialog2 = new BrushSize(this, mDrawView, mDrawView.brushSize1, mDrawView);
				dialog2.show();
			}
    		
		}
	    switch (theID) {
	    	case 0:			//CLEAR
	    		showClearPopup();
	    		break;
	        case 1:			//SAVE IMAGE	
				//String url = Images.Media.insertImage(getContentResolver(), mDrawView.saveImage(), "Sketch"+System.currentTimeMillis(), "Sketch");
	        	SavedUrl = mDrawView.saveImage();
				if(SavedUrl != "ERROR"){
					Toast.makeText(this, "Saved Image: "+SavedUrl, Toast.LENGTH_LONG).show();
					showShare();
				}else{
					Toast.makeText(this, "ERROR: Is your SD card mounted? ", Toast.LENGTH_LONG).show();
				}
	            break;
	        case 3:			//COLORS
	    		ColorDialog dialog = new ColorDialog(this, mDrawView, mDrawView.color, mDrawView, mDrawView.antiAlias, "brush");
	    		dialog.show();
	    		break;
	        case 4:			//Exit
	    		finish();
	    		break;
	    }
	    return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.v("BACK", "BACK "+keyCode);
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        //moveTaskToBack(true);
	    	mDrawView.Undo();
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}


}
