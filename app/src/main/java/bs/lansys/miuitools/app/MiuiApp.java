package bs.lansys.miuitools.app;

import miui.external.Application;
import miui.external.ApplicationDelegate;

/**
 * Created by Administrator on 2016/10/11 0011.
 */

public class MiuiApp  extends Application{

    public ApplicationDelegate onCreateApplicationDelegate(){
        return new AppDelegate();
    }
}
