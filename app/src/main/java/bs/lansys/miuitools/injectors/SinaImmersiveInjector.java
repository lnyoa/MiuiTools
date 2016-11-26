package bs.lansys.miuitools.injectors;

import bs.lansys.miuitools.utils.FlatColorBarStaticVariables;
import de.robv.android.xposed.XC_MethodHook;

/**
 * Created by lan on 16-11-13.
 */

public class SinaImmersiveInjector extends XC_MethodHook{
    @Override
    protected void beforeHookedMethod(XC_MethodHook.MethodHookParam mhparams) throws Throwable {
        String featureName = (String)mhparams.args[0];

        if (featureName.equals("immersive_before_marshmallow_enable")){
            mhparams.setResult(true);
        }
    }
}
