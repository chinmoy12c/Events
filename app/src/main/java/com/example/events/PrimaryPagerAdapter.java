package com.example.events;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class PrimaryPagerAdapter extends FragmentStatePagerAdapter {

    private int mNumOfTabs;

    PrimaryPagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        this.mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 : return new MessagesFragment();
            case 1 : return new MapFragment();
            case 2 : return new NotificationFragment();
            default: return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
