package org.poream.dejaview.Adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;

import org.poream.dejaview.Fragments.Fragment_myprofile_friends;
import org.poream.dejaview.Fragments.Fragment_myprofile_photos;
import org.poream.dejaview.Fragments.Fragment_myprofile_posts;
import org.poream.dejaview.R;

/**
 * Created by 이승호 on 2017-10-10.
 */

public class ViewPagerAdapter extends FragmentPagerAdapter{

    private  Context mContext;

    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext=context;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return  mContext.getString(R.string.myprofile_tab1);

            case 1:
                return  mContext.getString(R.string.myprofile_tab2);
            case 2:
                return  mContext.getString(R.string.myprofile_tab3);
            default:
                return  null;

        }
    }

    @Override
    public Fragment getItem(int position) {
        if(position==0){
            return new Fragment_myprofile_photos();
        }else if( position ==1){
            return  new Fragment_myprofile_posts();
        }else{
            return  new Fragment_myprofile_friends();
        }
    }

    @Override
    public int getCount() {

        return 3;
    }
}
