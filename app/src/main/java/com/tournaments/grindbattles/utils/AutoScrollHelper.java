package com.tournaments.grindbattles.utils;

import android.os.Handler;
import android.os.Looper;

import androidx.viewpager.widget.ViewPager;

import java.util.Timer;
import java.util.TimerTask;

public class AutoScrollHelper {
    private ViewPager viewPager;
    private Timer scrollTimer;
    private int currentPage;

    public AutoScrollHelper(ViewPager viewPager) {
        this.viewPager = viewPager;
        this.currentPage = 0;
    }

    public void startAutoScroll() {
        if (scrollTimer != null) {
            scrollTimer.cancel();
        }

        scrollTimer = new Timer();
        scrollTimer.scheduleAtFixedRate(new ScrollTask(), 0, 4000);
    }

    public void stopAutoScroll() {
        if (scrollTimer != null) {
            scrollTimer.cancel();
            scrollTimer = null;
        }
    }

    private class ScrollTask extends TimerTask {
        @Override
        public void run() {
            currentPage++;

            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (currentPage == viewPager.getAdapter().getCount()) {
                        currentPage = 0;
                    }
                    viewPager.setCurrentItem(currentPage, true);
                }
            });
        }
    }
}
