package tv.piratemedia.lifi;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

/**
 * Created by eliot on 12/01/2016.
 */
public class utils {
    public static void hideIcon(Context context) {
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context.getPackageName(), context.getPackageName() + ".MainActivity"),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);

    }

    public static void showIcon(Context context) {
        context.getPackageManager().setComponentEnabledSetting(
                new ComponentName(context.getPackageName(), context.getPackageName() + ".MainActivity"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    public static boolean iconEnabled(Context context) {
        return context.getPackageManager().getComponentEnabledSetting(new ComponentName(context.getPackageName(), context.getPackageName() + ".MainActivity")) == PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
    }
}
