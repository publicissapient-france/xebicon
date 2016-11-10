package fr.xebia.xebicon.ui.schedule;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

import java.util.ArrayList;

import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.activity.BaseActivity;
import fr.xebia.xebicon.core.activity.NavigationActivity;
import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.model.MySchedule;
import fr.xebia.xebicon.model.Schedule;
import fr.xebia.xebicon.model.Talk;
import fr.xebia.xebicon.ui.widget.UIUtils;
import icepick.Icepick;
import icepick.Icicle;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.Query;

public class MyScheduleActivity extends NavigationActivity implements ManyQuery.ResultHandler<Talk> {

    private static final long DAY_MILLIS = 24 * 60 * 60 * 1000;

    @InjectView(R.id.tab_layout) TabLayout mPagerStrip;
    @InjectView(R.id.view_pager) ViewPager mViewPager;

    private MySchedule mMySchedule;
    private boolean mStopped;
    private boolean setDefaultPage = true;
    @Icicle int mSelectedPage;

    public MyScheduleActivity() {
        super(R.layout.my_schedule_activity);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int conferenceId = Preferences.getSelectedConference(this);
        String query = "SELECT * FROM Talks WHERE conferenceId=? ORDER BY fromTime ASC, toTime ASC, _id ASC";
        Query.many(Talk.class, query, conferenceId).getAsync(getLoaderManager(), this);
        computSelectedPage();

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int selectedPage) {
                mSelectedPage = selectedPage;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        getSupportActionBar().setTitle(R.string.my_schedule);
    }

    @Override
    protected int getNavId() {
        return R.id.nav_myschedule;
    }

    private void computSelectedPage() {
        long conferenceStartTime = Preferences.getSelectedConferenceStartTime(this);
        long gapFromStart = System.currentTimeMillis() - conferenceStartTime;
        if (gapFromStart > 0) {
            mSelectedPage = (int) (gapFromStart / DAY_MILLIS);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mViewPager.getAdapter() != null && setDefaultPage) {
            computSelectedPage();
            mViewPager.setCurrentItem(mSelectedPage >= mMySchedule.getConferenceDaysCount() ? 0 : mSelectedPage);
            setDefaultPage = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mStopped = false;
        if (mMySchedule != null) {
            setAdapter();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStopped = true;
    }

    @Override
    public boolean handleResult(CursorList<Talk> cursorList) {
        mMySchedule = new MySchedule(new Schedule(cursorList == null ? new ArrayList<Talk>() : cursorList.asList()));
        if (mStopped) {
            return true;
        }
        setAdapter();
        return true;
    }

    private void setAdapter() {
        mViewPager.setAdapter(new MySchedulePagerAdapter(mMySchedule, getSupportFragmentManager()));
        mViewPager.setCurrentItem(mSelectedPage >= mMySchedule.getConferenceDaysCount() ? 0 : mSelectedPage);
        mPagerStrip.setupWithViewPager(mViewPager);
    }

    public static class MySchedulePagerAdapter extends FragmentPagerAdapter {

        private MySchedule mySchedule;

        public MySchedulePagerAdapter(MySchedule mySchedule, FragmentManager fm) {
            super(fm);
            this.mySchedule = mySchedule;
        }

        @Override
        public Fragment getItem(int position) {
            return MyScheduleFragment.newInstance();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            MyScheduleFragment myScheduleFragment = (MyScheduleFragment) super.instantiateItem(container, position);
            myScheduleFragment.setData(mySchedule.getConferenceDayAt(position));
            return myScheduleFragment;
        }

        @Override
        public int getCount() {
            return mySchedule.getConferenceDaysCount();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mySchedule.getConferenceDayAt(position).title;
        }

        public void setSchedule(MySchedule mySchedule) {
            this.mySchedule = mySchedule;
            notifyDataSetChanged();
        }
    }
}
