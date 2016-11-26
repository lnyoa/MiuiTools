package bs.lansys.miuitools.helpers;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.view.View;
import bs.lansys.miuitools.utils.ColorBarUtils;

public class BitmapCanvas extends Canvas{
	private Bitmap bm;
	
	public BitmapCanvas(View view, Canvas canvas){
		bm = Bitmap.createBitmap(view.getWidth(), ColorBarUtils.getStatusBarHeight(view.getContext()) + 15, Bitmap.Config.ARGB_4444);
		bm.setHasAlpha(false);
		bm.setDensity(canvas.getDensity());
		super.setBitmap(bm);
	}
	
	public BitmapCanvas(Bitmap bitmap){
		super(bitmap);
		bm = bitmap;
	}
	
	public Bitmap getBitmap(){
		return bm;
	}
	
	public void setBitmap(Bitmap bitmap){
		super.setBitmap(bitmap);
		if (bm != null){
			bm.recycle();
		}
		bm = bitmap;
	}
	
	public void recycle(){
		super.setBitmap(null);
		bm.recycle();
	}
}
