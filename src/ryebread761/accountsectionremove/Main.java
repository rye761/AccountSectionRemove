package ryebread761.accountsectionremove;

import java.util.List;

import android.app.Activity;
import android.preference.PreferenceActivity;
import android.preference.PreferenceActivity.Header;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Main implements IXposedHookLoadPackage{

	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		if (!lpparam.packageName.equals("com.android.settings"))
			return;
		XposedHelpers.findAndHookMethod("com.android.settings.Settings", lpparam.classLoader, "insertAccountsHeaders", List.class, int.class, new XC_MethodReplacement() {
			
			@Override
			protected Object replaceHookedMethod(MethodHookParam param)
					throws Throwable {
				XposedBridge.log("method replacement ran!");
				
				//remove header and add accounts button
				PreferenceActivity activity = (PreferenceActivity) param.thisObject;
				List<Header> headers = (List<Header>) param.args[0];
				int accountButton = findHeaderIndex(activity, headers, "account_add");
				headers.remove(accountButton-1);
				int accountHead = findHeaderIndex(activity, headers, "account_settings");
				headers.remove(accountHead-1);
				
				return param.args[1];
			}
		});
	}
	
	//stolen from https://github.com/MohammadAG/Xposed-Preference-Injector/blob/master/src/com/mohammadag/xposedpreferenceinjector/XposedMod.java
	public static int findHeaderIndex(Activity activity, List<Header> headers, String headerName) {
		int headerIndex = -1;
		int resId = activity.getResources().getIdentifier(headerName, "id", activity.getPackageName());
		if (resId != 0) {
			int i = 0;
			int size = headers.size();
			while (i < size) {
				Header header = headers.get(i);
				int id = (int) header.id;
				if (id == resId) {
					headerIndex = i + 1;
					break;
				}
				i++;
			}
		}
		return headerIndex;
	}

}
