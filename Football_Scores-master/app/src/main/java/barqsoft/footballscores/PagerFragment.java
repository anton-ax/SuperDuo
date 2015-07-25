package barqsoft.footballscores;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.service.myFetchService;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment {
    public static final int NUM_PAGES = 5;
    public ViewPager mPagerHandler;
    private MainScreenFragment[] viewFragments = new MainScreenFragment[5];
    private TabLayout pager_header;

    private int day = 14;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        pager_header = (TabLayout) rootView.findViewById(R.id.pager_header);

        myPageAdapter mPagerAdapter = new myPageAdapter(getChildFragmentManager());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd",
                getResources().getConfiguration().locale);

        Intent service_start = new Intent(getActivity().getApplicationContext(), myFetchService.class);
        getActivity().getApplicationContext().startService(service_start);

        for (int i = 0; i < NUM_PAGES; i++) {
            Date fragmentDate  = new Date(System.currentTimeMillis() + ((i - 2) * 86400000));

            // warning! debug only:
            if(Utilies.DEBUG) {
                String dateSt = "August " + (day++) + ", 2015";// start jd
                try {
                    SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
                    fragmentDate = format.parse(dateSt);
                } catch (Exception e) {
                    e.printStackTrace();
                }//end jd
            }
            viewFragments[i] = new MainScreenFragment();
            Bundle data = new Bundle();
            data.putString("date", mformat.format(fragmentDate));
            viewFragments[i].setArguments(data);
        }
        mPagerHandler.setAdapter(mPagerAdapter);
        mPagerHandler.setCurrentItem(MainActivity.current_fragment);
        pager_header.setupWithViewPager(mPagerHandler);
        return rootView;
    }

    private class myPageAdapter extends FragmentStatePagerAdapter {
        @Override
        public Fragment getItem(int i) {
            return viewFragments[i];
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        public myPageAdapter(FragmentManager fm) {
            super(fm);
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return getDayName(getActivity(), System.currentTimeMillis() + ((position - 2) * 86400000));
        }

        public String getDayName(Context context, long dateInMillis) {
            // If the date is today, return the localized version of "Today" instead of the actual
            // day name.

            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            if (julianDay == currentJulianDay) {
                return context.getString(R.string.today);
            } else if (julianDay == currentJulianDay + 1) {
                return context.getString(R.string.tomorrow);
            } else if (julianDay == currentJulianDay - 1) {
                return context.getString(R.string.yesterday);
            } else {
                Time time = new Time();
                time.setToNow();
                // Otherwise, the format is just the day of the week (e.g "Wednesday".
                SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE",
                        getResources().getConfiguration().locale);
                return dayFormat.format(dateInMillis);
            }
        }
    }
}
