package bs.lansys.miuitools.utils;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.SystemProperties;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Utils {
	
	public static int getMIUIVersion(){
		String name = SystemProperties.get(Consts.MIUIVERSION_Properties, "v7");
		return Integer.valueOf(name.substring(1));
	}
	
	public static boolean isThemeCrack(){
		return SystemProperties.getBoolean(Consts.THEME_CRACK_Properties, true);
	}
	
	public static boolean isColorToastEnabled(){
		return SystemProperties.getBoolean(Consts.TOAST_COLOR_Properties, true);
	}

	public static boolean isMusicOnline(){
		return SystemProperties.getBoolean(Consts.IS_MUSIC_ONLINE_Properties, true);
	}

	public static boolean isVideoOnline(){
		return SystemProperties.getBoolean(Consts.IS_VIDEO_ONLINE_Properties, true);
	}

	public static boolean isShowDebugMsg(){
		return SystemProperties.getBoolean(Consts.SHOW_DEBUG_MSG_Properties, false);
	}
	
	public static boolean isHaveSuperSu(Activity activity){
		PackageManager pm = activity.getPackageManager();
		try {
			pm.getPackageInfo("eu.chainfire.supersu", PackageManager.GET_ACTIVITIES);
			return true;
		} catch (NameNotFoundException e) {
			return false;
		}
	}

	public static boolean isMiuiFlatColorBarEnable(){
		boolean isEnable = miui.os.SystemProperties.getBoolean(Consts.MIUICOLORBAR_ENABLE_Properties, true);
		return isEnable;
	}

	public static boolean isADCrack(){
		return SystemProperties.getBoolean(Consts.AD_CRACK_Properties, true);
	}
	
	public static String getIncremental(){
		return SystemProperties.get("ro.build.version.incremental",  "unknown");
	}
}
