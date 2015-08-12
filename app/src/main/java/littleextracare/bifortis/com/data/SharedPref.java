package littleextracare.bifortis.com.data;

import android.content.Context;
import android.content.SharedPreferences;

import littleextracare.bifortis.com.Constants.SharedPrefConstants;

public class SharedPref
{
    private static final String TAG = SharedPref.class.getName();
    public static String getData(Context context, String key)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(SharedPrefConstants.tokenKey,
                                                                    Context.MODE_PRIVATE);
        String value = sharedPref.getString(key, null);
        return value;
    }

    public static void setData(Context context, String key, String value)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(SharedPrefConstants.tokenKey,
                                                                    Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static void removeData(Context context, String key)
    {
        SharedPreferences sharedPref = context.getSharedPreferences(SharedPrefConstants.tokenKey,
                                                                    Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove(key);
        editor.apply();
    }
}
