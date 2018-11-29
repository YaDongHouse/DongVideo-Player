package com.dong.video.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.dong.video.ui.fragment.VideoListFragment;

import java.util.List;

/**
 * @author yadong.qiu
 * Created by 邱亚东
 * Date: 2018/11/29
 * Time: 16:30
 */
public class VideoListPagerAdapter extends FragmentStatePagerAdapter {

    private List<VideoListFragment> mFragments;

    public VideoListPagerAdapter(FragmentManager fm, List<VideoListFragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "列表" + (position + 1);
    }

    @Override
    public int getCount() {
        if (mFragments == null) {
            return 0;
        }
        return mFragments.size();
    }
}
