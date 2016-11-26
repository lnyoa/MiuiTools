package bs.lansys.miuitools.injectors;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import miui.maml.ScreenElementRoot.OnExternCommandListener;
import miui.util.Log;

public class MiuiGlobalActionsInjectors extends XC_MethodHook {

	private void SoftReboot() {
		try {
			IActivityManager iam = ActivityManagerNative.asInterface(ServiceManager.checkService("activity"));
			if (iam != null) {
				iam.restart();
			}
		} catch (RemoteException e) {
			Log.e("MGA-lansys:", e.toString());
		}
	}

	private static IPowerManager getPowerManager() {
		return IPowerManager.Stub.asInterface(ServiceManager.getService("power"));
	}

	@Override
	protected void afterHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
		String command = (String) mhparams.args[0];

		 if ("recovery".equals(command)) {
			try {
				getPowerManager().reboot(false, "recovery", false);
			} catch (RemoteException e) {
				Log.e("MGA-lansys:", e.toString());
			}
		} else if ("softreboot".equals(command)) {
			SoftReboot();
		} else if ("bootloader".equals(command)) {
			try {
				getPowerManager().reboot(false, "bootloader", false);
			} catch (RemoteException e) {
				Log.e("MGA-lansys:", e.toString());
			}
		}
	}

}
