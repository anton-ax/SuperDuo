package barqsoft.footballscores;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.service.FetchService;

/**
 * Created by yehya khaled on 2/27/2015.
 */
public class PagerFragment extends Fragment {

    public static final int NUM_PAGES = 5;
    private static final long ONE_DAY = 86400000;

    public ViewPager mPagerHandler;
    private MainScreenFragment[] viewFragments = new MainScreenFragment[5];

    private SimpleDateFormat dayFormat;

    private int day = 14;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.pager_fragment, container, false);
        mPagerHandler = (ViewPager) rootView.findViewById(R.id.pager);
        TabLayout pager_header = (TabLayout) rootView.findViewById(R.id.pager_header);

        dayFormat = new SimpleDateFormat("EEEE", getResources().getConfiguration().locale);

        myPageAdapter mPagerAdapter = new myPageAdapter(getChildFragmentManager());
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd",
                getResources().getConfiguration().locale);

        Intent service_start = new Intent(getActivity().getApplicationContext(), FetchService.class);
        getActivity().getApplicationContext().startService(service_start);

        for (int i = 0; i < NUM_PAGES; i++) {
            Date fragmentDate = new Date(System.currentTimeMillis() + ((i - 2) * ONE_DAY));

            // warning! debug only:
            if (Utilies.DEBUG) {
                fragmentDate = setDayWithGames();
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

    /*
    For testing only.
    Select day with large amount of games
     */
    private Date setDayWithGames() {
        Date fragmentDate = null;
        String dateSt = "August " + (day++) + ", 2015";// start jd
        try {
            SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            fragmentDate = format.parse(dateSt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fragmentDate;
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
            return getDayName(System.currentTimeMillis() + ((position - 2) * ONE_DAY));
        }

        /**
         * @param dateInMillis selected date
         * @return localized version of Yesterday/Today/Tomorrow or actual week day
         */
        public String getDayName(long dateInMillis) {
            Time t = new Time();
            t.setToNow();
            int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
            int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
            long now = System.currentTimeMillis();
            if ((julianDay >= currentJulianDay - 1 && julianDay <= currentJulianDay + 1)) {
                CharSequence relativeTimeSpanString = DateUtils.getRelativeTimeSpanString(
                        dateInMillis, now, DateUtils.DAY_IN_MILLIS);
                return relativeTimeSpanString.toString();
            } else {
                return dayFormat.format(dateInMillis);
            }
        }
    }
}
