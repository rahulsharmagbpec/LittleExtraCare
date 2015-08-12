package littleextracare.bifortis.com.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class ActivityInviteFriends extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_activity_invite_friends, container, false);
        return view;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
