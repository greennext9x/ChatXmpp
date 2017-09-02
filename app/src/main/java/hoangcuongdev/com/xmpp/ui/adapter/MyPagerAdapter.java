package hoangcuongdev.com.xmpp.ui.adapter;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GreenLove on 2017
 */

public class MyPagerAdapter extends FragmentPagerAdapter{
    private String[] mTitles;
    private List<Fragment> mFragmentList = new ArrayList<>();

    public MyPagerAdapter(FragmentManager fm,List<Fragment> fragmentList,String[] titles) {
        super( fm );
        this.mFragmentList.clear();
        this.mFragmentList.addAll(fragmentList);
        this.mTitles = titles;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get( position );
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (mTitles != null && mTitles.length > position) {
            return mTitles[position];
        }
        return "";
    }

}
