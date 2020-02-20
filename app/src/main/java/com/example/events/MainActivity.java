package com.example.events;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar primaryToolbar = findViewById(R.id.main_activity_toolbar);
        setSupportActionBar(primaryToolbar);

        TabLayout primaryTabLayout = findViewById(R.id.primaryTabLayout);
        primaryTabLayout.addTab(primaryTabLayout.newTab().setText(R.string.messageTab));
        primaryTabLayout.addTab(primaryTabLayout.newTab().setText(R.string.mapTab));
        primaryTabLayout.addTab(primaryTabLayout.newTab().setText(R.string.notificationTab));

        primaryTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = findViewById(R.id.primaryViewPager);
        final PagerAdapter pagerAdapter = new PrimaryPagerAdapter(getSupportFragmentManager(),primaryTabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(primaryTabLayout));
        primaryTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
