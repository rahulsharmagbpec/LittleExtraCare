package littleextracare.bifortis.com.adapters;


import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.activities.CustomerProfileActivity;
import littleextracare.bifortis.com.activities.NavigationDrawerCallbacks;
import littleextracare.bifortis.com.activities.R;
import littleextracare.bifortis.com.data.SharedPref;
import littleextracare.bifortis.com.model.NavigationItem;

public class NavigationDrawerAdapter extends RecyclerView.Adapter<NavigationDrawerAdapter.ViewHolder> {

    private static String TAG = NavigationDrawerAdapter.class.getName();
    private List<NavigationItem> mData;
    private NavigationDrawerCallbacks mNavigationDrawerCallbacks;
    private View mSelectedView;
    private int mSelectedPosition;
    private static Context context;

    public NavigationDrawerAdapter(List<NavigationItem> data) {
        mData = data;
    }

    public NavigationDrawerCallbacks getNavigationDrawerCallbacks() {
        return mNavigationDrawerCallbacks;
    }

    public void setNavigationDrawerCallbacks(NavigationDrawerCallbacks navigationDrawerCallbacks) {
        mNavigationDrawerCallbacks = navigationDrawerCallbacks;
    }
    public static int count = 0;

    @Override
    public NavigationDrawerAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v;
        context = viewGroup.getContext();
        //Log.e("onCreateViewHolder", "count:"+count+",int i:"+i);
        if(count > 5)
            count = 0;

        //Log.e("NavigationDrawerAdapter","count = "+count);
        if(count == 0)
        {
            count = 1;
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.resource_profile, viewGroup, false);
            CircleImageView profileImageView = (CircleImageView) v.findViewById(R.id.profile_image3);
            profileImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Log.e(TAG, "Clicked");
                    Intent intent = new Intent(context, CustomerProfileActivity.class);
                    context.startActivity(intent);
                }
            });

        }
        else if(count == 3) {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_nav_drawer_toggle, viewGroup, false);//R.layout.drawer_row, viewGroup, false);
            Switch s = (Switch) v.findViewById(R.id.nav_switch);
            try{
                String flag = SharedPref.getData(context, SharedPrefConstants.PREF_SWITCH);
                if(flag != null)
                {
                    if(flag.equalsIgnoreCase("true"))
                        s.setChecked(true);
                    else
                        s.setChecked(false);
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }

        }
        else
        {
            v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.drawer_row, viewGroup, false);
        }
        final ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.itemView.setClickable(true);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                                                   @Override
                                                   public void onClick(View v) {
                                                       if (mSelectedView != null) {
                                                           mSelectedView.setSelected(false);
                                                       }
                                                       mSelectedPosition = viewHolder.getAdapterPosition();
                                                       //v.setSelected(true);
                                                       mSelectedView = v;
                                                       if (mNavigationDrawerCallbacks != null)
                                                           mNavigationDrawerCallbacks.onNavigationDrawerItemSelected(viewHolder.getAdapterPosition());
                                                   }
                                               }
        );
        viewHolder.itemView.setBackgroundResource(R.drawable.row_selector);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NavigationDrawerAdapter.ViewHolder viewHolder, int i) {
        //Log.e("NavigationDrawerAdapter", "onBindViewHolder" + i);
        if(i != 0)
            count = i;
        try {
            viewHolder.textView.setText(mData.get(i).getText());
            viewHolder.textView.setCompoundDrawablesWithIntrinsicBounds(mData.get(i).getDrawable(), null, null, null);
        }
        catch(Exception e)
        {
            Log.e("NavigationDrawerAdapter", e.toString());
        }
        if (mSelectedPosition == i) {
            if (mSelectedView != null) {
                mSelectedView.setSelected(false);
            }
            mSelectedPosition = i;
            mSelectedView = viewHolder.itemView;
            mSelectedView.setSelected(true);
        }
    }


    public void selectPosition(int position) {
        mSelectedPosition = position;
        notifyItemChanged(position);
        count = 0;
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item_name);
        }
    }
}