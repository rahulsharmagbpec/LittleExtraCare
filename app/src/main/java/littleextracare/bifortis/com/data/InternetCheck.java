package littleextracare.bifortis.com.data;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetCheck
{
    private Context context;

        public InternetCheck(Context context){
            this.context=context;
        }

        public boolean isInternetAvailable(){

            ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            if(connectivityManager!=null){

                NetworkInfo[] allNetworkInfo=connectivityManager.getAllNetworkInfo();

                if(allNetworkInfo!=null){
                    for (NetworkInfo anAllNetworkInfo : allNetworkInfo)
                        if (anAllNetworkInfo.getState() == NetworkInfo.State.CONNECTED) {
                            return true;
                        }
                }
            }
            return false;
        }

}
