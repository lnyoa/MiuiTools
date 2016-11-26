package bs.lansys.miuitools.utils;

import static bs.lansys.miuitools.utils.FlatColorBarStaticVariables.mToastBackColor;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.MiuiWindowManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.android.internal.app.WindowDecorActionBar;

import bs.lansys.miuitools.helpers.BitmapCanvas;
import bs.lansys.miuitools.utils.BitMapColor.Type;
import de.robv.android.xposed.XposedHelpers;
import miui.os.SystemProperties;
import miui.util.ScreenshotUtils;

public class ColorBarUtils {
	
	private static int sStatusbarHeight = 0;
	private static int sStatusbarBackgroundId = 0;
	private static String[] blacklist = {"com.android.systemui"};
	private static String[] mTranslucentApps = {"com.tencent.mobileqq"};
	
	private static int getBarsHeight(Context context, String name, String type, String pkgName) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier(name, type, pkgName);
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
	
	public static int getStatusBarWidth(Activity activity){
		WindowManager mWindowManager = activity.getWindowManager();
		return mWindowManager.getDefaultDisplay().getWidth();
	}

	public static int getStatusBarHeight(Context context) {
		if (sStatusbarHeight == 0){
			sStatusbarHeight = getBarsHeight(context, "status_bar_height", "dimen", "android");
		}
		return sStatusbarHeight;
	}
	
	
	private static boolean isLauncher(Context context, String packageName) {
		ActivityInfo homeInfo = new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME).resolveActivityInfo(context.getPackageManager(), 0);
		return homeInfo != null && homeInfo.packageName.equals(packageName);
	}
	

	
	public static void setStatusBarDarkMode(Activity activity, boolean darkmode) {
		int darkModeFlag = android.view.MiuiWindowManager.LayoutParams.EXTRA_FLAG_STATUS_BAR_DARK_MODE;
		activity.getWindow().setExtraFlags(darkmode ? darkModeFlag : 0, darkModeFlag);
	}
	
	public static boolean isBrightColor(int color) {
        if (color == -3) {
            return false;
        } else if (color == Color.TRANSPARENT) {
            return false;
        } else if (color == Color.WHITE) {
            return true;
        }
        int[] rgb = { Color.red(color), Color.green(color), Color.blue(color) };
        int brightness = (int) Math.sqrt(rgb[0] * rgb[0] * .241 + rgb[1]
            * rgb[1] * .691 + rgb[2] * rgb[2] * .068);
        if (brightness >= 170) {
            return true;
        }
        return false;
    }
	
	public static int Dip2Pixl(Context context, float dip) {
		return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip,
				context.getResources().getDisplayMetrics());
	}
	
	public static TextView getTextView(View v) {
		if (((v instanceof TextView)) && (!TextUtils.isEmpty(((TextView) v).getText()))) {
			return (TextView) v;
		}
		if (v instanceof ViewGroup) {
			ViewGroup vg = (ViewGroup) v;
			int i = 0;
			while (i < vg.getChildCount()) {
				TextView tv = getTextView(vg.getChildAt(i));
				if (tv != null) {
					return tv;
				}
				i += 1;
			}
		}
		return null;
	}
	
	public static Bitmap MiuiTakeScreenShot(Context context, boolean transparent) {
		Bitmap bitmap = ScreenshotUtils.getScreenshot(context);
		if (bitmap == null){
			return null;
		}
		int top = 0;
		if (!transparent) {
			if (sStatusbarHeight == 0) {
				sStatusbarHeight = getStatusBarHeight(context);
			}
			top = sStatusbarHeight;
		}
		
		int width = bitmap.getWidth();
		
		try {
			Bitmap bm = Bitmap.createBitmap(bitmap, 0, top, width, 15);
			return bm;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static int colorAverage(int... colors) {
		int r = 0;
		int g = 0;
		int b = 0;
		
		for (int color : colors) {
			r += Color.red(color);
			g += Color.green(color);
			b += Color.blue(color);
		}
		
		return Color.rgb(r / colors.length, g / colors.length, b / colors.length);
	}
	
	public static int getMaincolor(int... colors) {
		SparseIntArray map = new SparseIntArray();
		
		for (int color : colors) {
			map.put(color, map.get(color) + 1);
		}
		
		int mainColor = Color.TRANSPARENT;
		int max = 0;
		for (int i = 0; i < map.size(); i++){
			int j = map.valueAt(i);
			if (j > max){
				max = j;
				mainColor = map.keyAt(i);
			}
		}
		
		return mainColor;
	}
	
	public static int getBitmapColor(Bitmap bitmap, int offset_x, int offset_y){
		int count = 20;
		if (bitmap.getWidth() < offset_x){
			throw  new RuntimeException(" x,err!");
		}
		if (bitmap.getHeight() < offset_y){
			throw  new RuntimeException(" y,err!");
		}
		
		int width = bitmap.getWidth() - offset_x;
		int[] colors = new int[count];
		
		for (int i = 0; i < count; i++){
			colors[i] = bitmap.getPixel(offset_x + width * i / count, offset_y);
		}
		
		int color = getMaincolor(colors);
		
		if ( color == Color.TRANSPARENT){
			color = colorAverage(colors);
		}
		
		return color;
	}
	
	public static BitMapColor getBitmapColor(Bitmap bitmap, int offset_y){
		BitMapColor bmc = new BitMapColor();
		int width = bitmap.getWidth();
		int height = offset_y;
		int color1 = bitmap.getPixel(1, (height - 3 >= 0) ? (height - 3) : 0);
		int color2 = bitmap.getPixel(width - 1, (height - 3 >= 0) ? (height - 3) : 0);
		int color3 = bitmap.getPixel(0, height - 1);
		int color4 = bitmap.getPixel(width - 1, height - 1);
		
		if (color1 != color2 || color3 != color4) {
			bmc.mType = BitMapColor.Type.PICTURE;
		}
		else if (color1 != color3) {
			bmc.mType = BitMapColor.Type.GRADUAL;
		}else{
			bmc.mType = BitMapColor.Type.FLAT;
		}
		
		bmc.Color = getBitmapColor(bitmap,0,offset_y);
		return bmc;
		
	}
	
	public static boolean isSimilarColor(int color1, int color2){
		int R1,R2;
		int G1,G2;
		int B1,B2;
		
		R1 = Color.red(color1);
		G1 = Color.green(color1);
		B1 = Color.blue(color1);
		
		R2 = Color.red(color2);
		G2 = Color.green(color2);
		B2 = Color.blue(color2);
		
		return Math.sqrt((R1-R2)^2 + (G1-G2)^2 + (B1-B2)^2) < 5;
	}
	
	public static boolean darkModeStatusBarMiuiActivity(Activity activity) {
		int extraFlags = activity.getWindow().getAttributes().extraFlags;
		int darkmodeFlag = MiuiWindowManager.LayoutParams.EXTRA_FLAG_STATUS_BAR_DARK_MODE;
		if ((extraFlags & darkmodeFlag) == darkmodeFlag) {
			return true;
		}
	return false;
	}
	
	public static boolean isTranslucentMask (int color){
		int alpha = Color.alpha(color);
		return (alpha != 0xFF) && (alpha != 0) && ((0xFFFFFF & color) == 0);
	}
	
	public static int getBitmapColor(Drawable drawable)
			throws IllegalArgumentException {
		Drawable copyDrawable = drawable.getConstantState().newDrawable();
		int color;
		if (copyDrawable instanceof ColorDrawable) {
			color = ((ColorDrawable) drawable).getColor();
		} else {
			Bitmap bitmap = drawableToBitmap(copyDrawable);
			color = getBitmapColor(bitmap,0, Consts.Y_OFFSET);
			bitmap.recycle();
		}
		copyDrawable = null;
		return color;
	}
	
	private static Bitmap drawableToBitmap(Drawable drawable)
			throws IllegalArgumentException {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}
		Bitmap bitmap;

		try {
			int w = drawable.getIntrinsicWidth();
			int h = drawable.getIntrinsicHeight();
			bitmap = Bitmap.createBitmap(1, 80, Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, w, h);
			drawable.draw(canvas);
		} catch (IllegalArgumentException e) {
			throw e;
		}

		return bitmap;
	}
	
	public static void setStatusBarColor(Activity activity, int color){
		Window window = activity.getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		window.setStatusBarColor(color);
		boolean darkmode = ColorBarUtils.isBrightColor(color);
		ColorBarUtils.setStatusBarDarkMode(activity, darkmode);
	}
	
	public static void setActionBarColor(Activity activity, int color){
		ActionBar actionBar = activity.getActionBar();
		if (actionBar != null && actionBar.isShowing()) {
			if (actionBar instanceof WindowDecorActionBar) {
				Drawable drawable = new ColorDrawable(color);
				actionBar.setBackgroundDrawable(drawable);
				FrameLayout container = (FrameLayout) XposedHelpers.getObjectField(actionBar, "mContainerView");
				container.invalidate();
			}
		}
	}
	
	private static View getRootView(Activity context)  {  
        return ((ViewGroup)context.findViewById(android.R.id.content)).getChildAt(0);  
    } 
	
	
	public static WindowType getWindowType(Activity activity){ 
		Window window = activity.getWindow();
		WindowType windowtype = WindowType.UNKOWN;
		
		if (isLauncher(activity, activity.getPackageName())){
			return windowtype;
		}

		String packageName = activity.getApplicationInfo().packageName;

		for (String black : blacklist){
			if (packageName.equals(black))
				return windowtype;
		}

		for (String translucentapp : mTranslucentApps){
			if (translucentapp.equals(packageName)){
				return WindowType.TRANSLUCENT;
			}
		}
		
		int isFloating = com.android.internal.R.styleable.Window_windowIsFloating;
		
		int flags = window.getAttributes().flags;
		ViewGroup decor = (ViewGroup) window.getDecorView();
		
		int Window_windowTranslucentStatus = com.android.internal.R.styleable.Window_windowTranslucentStatus;
		
		int sysui1 = decor.findViewById(android.R.id.content).getSystemUiVisibility();
		int sysui2 = decor.getSystemUiVisibility();
		int sysui3 = ColorBarUtils.getRootView(activity).getSystemUiVisibility();
		int sysui4 = decor.getChildAt(0).getSystemUiVisibility();
		
		int sysui = (sysui1 | sysui2 | sysui3 | sysui4);
		
		TypedArray a = activity.getTheme().obtainStyledAttributes(com.android.internal.R.styleable.Theme);
		boolean translucentStatus = a.getBoolean(com.android.internal.R.styleable.Theme_windowTranslucentStatus, false);
		a.recycle();

		if (window.getWindowStyle().getBoolean(isFloating, false)){
			windowtype = WindowType.FLOAT;
		}else if ((sysui & View.SYSTEM_UI_FLAG_FULLSCREEN) == View.SYSTEM_UI_FLAG_FULLSCREEN ||
				(sysui & View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN ) ==  View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN ||
				(sysui & View.SYSTEM_UI_FLAG_IMMERSIVE) == View.SYSTEM_UI_FLAG_IMMERSIVE ||
				(sysui & View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) {
			windowtype =  WindowType.FULLSCREEN;
		}else if ((flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) == WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS){
			windowtype = WindowType.TRANSLUCENT;
		}else if ((flags & WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS) == WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
						&& window.getStatusBarColor() == Color.TRANSPARENT) {
			windowtype = WindowType.TRANSLUCENT;
		}else if (window.getWindowStyle().getBoolean(Window_windowTranslucentStatus, false)){
			windowtype = WindowType.TRANSLUCENT;
		}else if (translucentStatus){
			windowtype = WindowType.TRANSLUCENT;
		}else{
			windowtype = WindowType.NORMAL;
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
		}
		
		if (windowtype == WindowType.TRANSLUCENT || windowtype == WindowType.FULLSCREEN){
			if (ColorBarUtils.darkModeStatusBarMiuiActivity(activity))
				windowtype = WindowType.MIUISTYLE;
			if (!ColorBarUtils.darkModeStatusBarMiuiActivity(activity) && isFitsSystemWindows(activity)){
				windowtype = WindowType.MATERIALSTYLE;
				if ((flags & WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS) != 0 ){
					window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS );  
					decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN  
							| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);  
					window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS); 
					window.setStatusBarColor(Color.TRANSPARENT);
				}
			}
		}

		return windowtype;
	}
	
	public static boolean isFitsSystemWindows(Activity activity){
		Window window = activity.getWindow();
		ViewGroup decor = (ViewGroup) window.getDecorView();
		
		return ColorBarUtils.getRootView(activity).fitsSystemWindows() || decor.getChildAt(0).fitsSystemWindows() || decor.fitsSystemWindows() || decor.findViewById(android.R.id.content).fitsSystemWindows();
	}
	
	public static int getActionBarColor(Activity activity){
		ActionBar actionBar = activity.getActionBar();
		int actionbarColor = Color.TRANSPARENT;
		if (actionBar != null && actionBar.isShowing()) {
			if (actionBar instanceof WindowDecorActionBar) {
				FrameLayout container = (FrameLayout) XposedHelpers.getObjectField(actionBar, "mContainerView");
				if (container != null) {
					Drawable backgroundDrawable = (Drawable) XposedHelpers.getObjectField(container, "mBackground");
					if (backgroundDrawable != null) {
						actionbarColor = ColorBarUtils.getBitmapColor(backgroundDrawable);
					}
				}
			}
		}
		
		return actionbarColor;
	}
	
	public static int getStatusbarBackgroundId(Context context){
		if (sStatusbarBackgroundId == 0){
			sStatusbarBackgroundId = context.getResources().getIdentifier("android:id/statusBarBackground", null, null);
		}
		return sStatusbarBackgroundId;
	}
	
	public static Bitmap loadBitmapFromActivity(Activity activity, int y_offset, boolean transparent) {  
		View v = activity.getWindow().getDecorView();
        if (v == null) {  
            return null;  
        }  
        Bitmap bitmap1;  
        if (sStatusbarHeight == 0) {
			sStatusbarHeight = getStatusBarHeight(activity);
		}
        int h = sStatusbarHeight + y_offset;
        int width = activity.getWindowManager().getDefaultDisplay().getWidth();
        
        bitmap1 = Bitmap.createBitmap(width, h, Bitmap.Config.ARGB_4444);  
        BitmapCanvas c = new BitmapCanvas(bitmap1);  
        c.translate(-v.getScrollX(), -v.getScrollY());  
        v.draw(c);  
        
        int top = 0;
		if (!transparent) {
			top = sStatusbarHeight;
		}
		
		width = bitmap1.getWidth();
		
		try {
			Bitmap bitmap = Bitmap.createBitmap(bitmap1, 0, top, width, h - top);
			v.invalidate();
			bitmap1.recycle();
			return bitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
    }
	
	public static void setMiuiColorBar(Activity activity, WindowType type, Bitmap bitmap){
		int STATUS_HEIGHT = getStatusBarHeight(activity) + Consts.Y_OFFSET;
		
		Window window = activity.getWindow();
		
		switch (type) {
		case MIUISTYLE:{
			int color = ColorBarUtils.getBitmapColor(bitmap, 0, Consts.Y_OFFSET);
			mToastBackColor = color;
			break;
		}
		case FULLSCREEN:
		case TRANSLUCENT: {
			int color = ColorBarUtils.getBitmapColor(bitmap, 0, Consts.Y_OFFSET);
			mToastBackColor = color;
			ColorBarUtils.setStatusBarDarkMode(activity, ColorBarUtils.isBrightColor(color));
			break;
		}
		case MATERIALSTYLE: {
			BitMapColor statusbarColor = ColorBarUtils.getBitmapColor(bitmap, Consts.Y_OFFSET);
			BitMapColor activityColor = ColorBarUtils.getBitmapColor(bitmap, STATUS_HEIGHT);
			if (statusbarColor.mType == Type.FLAT && activityColor.mType == Type.FLAT) {
				if (statusbarColor.Color != activityColor.Color){
					ColorBarUtils.setStatusBarColor(activity, activityColor.Color);
					mToastBackColor = activityColor.Color;
				}else{
					ColorBarUtils.setStatusBarDarkMode(activity, ColorBarUtils.isBrightColor(statusbarColor.Color));
					mToastBackColor = statusbarColor.Color;
				}
			} else {
				activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
				ColorBarUtils.setStatusBarDarkMode(activity, ColorBarUtils.isBrightColor(statusbarColor.Color));
				mToastBackColor = statusbarColor.Color;
			}
			break;
		}
		case NORMAL: {
			int color = ColorBarUtils.getBitmapColor(bitmap, 0, STATUS_HEIGHT);
			if (window.getStatusBarColor() != color){
				ColorBarUtils.setStatusBarColor(activity, color);
			}
			mToastBackColor = color;
		}
		default:
			break;

		}
	}
	
		
}
