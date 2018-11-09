package mohalim.android.bmonline.Utils;

import android.content.Context;
import android.content.SharedPreferences;

public class CredentialsSharedPrefrences {
    private static final String APP_SETTINGS = "APP_SETTINGS";
    private static final String USERNAME = "USERNAME_VALUE";
    private static final String PASSWORD = "PASSWORD_VALUE";

    public CredentialsSharedPrefrences() {}

    private static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(APP_SETTINGS, Context.MODE_PRIVATE);
    }

    public static String getUsernameValue(Context context) {
        return getSharedPreferences(context).getString(USERNAME , null);
    }

    public static void setUsernameValue(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USERNAME , newValue);
        editor.commit();
    }

    public static String getPasswordValue(Context context) {
        return getSharedPreferences(context).getString(PASSWORD , null);
    }

    public static void setPasswordValue(Context context, String newValue) {
        final SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PASSWORD , newValue);
        editor.commit();
    }

}
