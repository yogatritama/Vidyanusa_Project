package com.example.vidyanusa;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.vidyanusa.FragmentPengaturan;
import com.example.vidyanusa.portal.LihatPortal;
import com.example.vidyanusa.tab2;
import com.example.vidyanusa.tab3;

/**
 * Created by ManInBack on 11/30/2016.
 */

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                LihatPortal LihatPortal =new LihatPortal();
                return LihatPortal;
//                LihatPortalActivity LihatPortalActivity = new LihatPortalActivity();
//                return LihatPortalActivity;
            case 1:
                MateriActivity MateriActivity = new MateriActivity();
                return MateriActivity;
            case 2:
                LihatBlogActivity LihatBlogActivity = new LihatBlogActivity();
                return LihatBlogActivity;
            case 3:
                FragmentPengaturan FragmentPengaturan = new FragmentPengaturan();
                return FragmentPengaturan;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}