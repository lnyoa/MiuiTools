package bs.lansys.miuitools.injectors;

import java.util.HashMap;

import bs.lansys.miuitools.utils.Consts;
import bs.lansys.miuitools.utils.Utils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

/**
 * Created by lan on 16-11-21.
 */

public class ArrayListAddInjector extends XC_MethodHook {

    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
        Object response = mhparams.args[0];
        if (response == null) return;
        if (response.getClass().getName().equals(Consts.CLASS_MUSIC_DisplayItem)){
            Object uiType = XposedHelpers.getObjectField(response, "uiType");
            HashMap<String, String> extra = (HashMap)XposedHelpers.getObjectField(uiType, "extra");
            if (extra.get("args_type").equals("ad")) {
                  if (Utils.isADCrack()) mhparams.setResult(false);
            }
        }
    }
}
