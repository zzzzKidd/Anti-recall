/*
 * Copyright © 2016 - 2018 by GitHub.com/JasonQS
 * anti-recall.qsboy.com
 * All Rights Reserved
 */

package com.qsboy.antirecall.ui.activity;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.qsboy.antirecall.R;
import com.qsboy.antirecall.db.Dao;
import com.qsboy.antirecall.ui.fragment.HelpFragment;
import com.qsboy.antirecall.ui.fragment.QQFragment;
import com.qsboy.antirecall.ui.fragment.SettingsFragment;
import com.qsboy.antirecall.ui.fragment.WeChatFragment;
import com.qsboy.antirecall.utils.LogcatHelper;
import com.qsboy.antirecall.utils.UpdateHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import devlight.io.library.ntb.NavigationTabBar;

public class MainActivity extends AppCompatActivity {

    final String TAG = "Main Activity";
    List<Fragment> fragmentList = new ArrayList<>();
    Toolbar toolbar;

    // TODO: 03/06/2018 顶部加filter
    // TODO: 20/06/2018 请求白名单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 记录log
        LogcatHelper.getInstance(this).start();

        // 检查更新
        if (!App.isCheckUpdateOnlyOnWiFi || isWifi())
            new UpdateHelper(this).checkUpdate();

//        prepareDataForTest();
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        App.deviceHeight = getWindowManager().getDefaultDisplay().getHeight();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
        fragmentList.clear();
        initTabBar();
    }

    private void initTabBar() {
        ViewPager viewPager = findViewById(R.id.vp_horizontal_ntb);
        if (fragmentList.size() < 3) {
            fragmentList.add(new QQFragment());
            fragmentList.add(new WeChatFragment());
            fragmentList.add(new SettingsFragment());
        }
        viewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return fragmentList.size();
            }

            @Override
            public Fragment getItem(int position) {
                return fragmentList.get(position);
            }
        });

        // 保存页面位置
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                App.activityPageIndex = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        NavigationTabBar navigationTabBar = findViewById(R.id.ntb);
        navigationTabBar.show();
        final ArrayList<NavigationTabBar.Model> models = new ArrayList<>();
        models.add(new NavigationTabBar.Model.Builder(
                VectorDrawableCompat.create(getResources(), R.drawable.ic_qq, null),
                getResources().getColor(R.color.colorQQ))
                .title("QQ/Tim")
                .build());
        models.add(new NavigationTabBar.Model.Builder(
                VectorDrawableCompat.create(getResources(), R.drawable.ic_wechat, null),
                getResources().getColor(R.color.colorWX))
                .title("WeChat")
                .build());
        models.add(new NavigationTabBar.Model.Builder(
                VectorDrawableCompat.create(getResources(), R.drawable.ic_settings, null),
                getResources().getColor(R.color.colorTim))
                .title("SettingsFragment")
                .build());

        navigationTabBar.setModels(models);
        navigationTabBar.setViewPager(viewPager, App.activityPageIndex);
        navigationTabBar.setBehaviorEnabled(true);
    }

    public boolean isWifi() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm != null) {
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                Log.d(TAG, "isWifi");
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content, new HelpFragment())
                    .addToBackStack("help")
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    .commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // for test
    public void prepareDataForTest() {
        Date in = new Date();
        Dao dao = Dao.getInstance(this, Dao.DB_NAME_QQ);
//        dao.deleteAll();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -9);
        for (int i = 1; i < 200; ) {
            for (int j = 1; j < 21; j++, i++) {
                dao.addMessage("Jason", "qs", String.valueOf(i), calendar.getTime().getTime());
                calendar.add(Calendar.MINUTE, 3);
                calendar.add(Calendar.SECOND, 3);
            }
            dao.addRecall(i - 1, "Jason", "qs", String.valueOf(i - 1), calendar.getTime().getTime(), null, null, null, null, null);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        Date out = new Date();
        Log.i(TAG, "prepareDataForTest: tvTime: " + (out.getTime() - in.getTime()));
    }

    private void addData() {
        Dao dao = Dao.getInstance(this, Dao.DB_NAME_QQ);
        dao.addMessage("编程团老年人水群", "ryuujo", " ", Calendar.getInstance().getTime().getTime());

    }
}
