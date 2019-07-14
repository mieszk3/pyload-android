/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pyload.android.client.components

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.TabHost
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager.widget.ViewPager
import org.pyload.android.client.R

/**
 * Demonstrates combining a TabHost with a ViewPager to implement a tab UI that
 * switches between tabs and also allows the user to perform horizontal flicks
 * to move between the tabs.
 */
abstract class FragmentTabsPager : AppCompatActivity() {
    protected lateinit var mTabHost: TabHost
    private lateinit var mViewPager: ViewPager
    protected lateinit var mTabsAdapter: TabsAdapter

    val currentTab: Int
        get() = mTabHost.currentTab

    val currentFragment: Fragment?
        get() = mTabsAdapter.getFragment(currentTab)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.fragment_tabs_pager)
        mTabHost = findViewById(android.R.id.tabhost)
        mTabHost.setup()

        mViewPager = findViewById(R.id.pager)

        mTabsAdapter = TabsAdapter(this, mTabHost, mViewPager)

        if (savedInstanceState != null) {
            mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("tab", mTabHost.currentTabTag)
    }

    /**
     * This is a helper class that implements the management of tabs and all
     * details of connecting a ViewPager with associated TabHost. It relies on a
     * trick. Normally a tab host has a simple API for supplying a View or
     * Intent that each tab will show. This is not sufficient for switching
     * between pages. So instead we make the content part of the tab host 0dp
     * high (it is not shown) and the TabsAdapter supplies its own dummy view to
     * show as the tab content. It listens to changes in tabs, and takes care of
     * switch to the correct paged in the ViewPager whenever the selected tab
     * changes.
     */
    class TabsAdapter internal constructor(activity: FragmentActivity, private val mTabHost: TabHost,
                                           private val mViewPager: ViewPager) : FragmentPagerAdapter(activity.supportFragmentManager), TabHost.OnTabChangeListener, ViewPager.OnPageChangeListener {
        private val mContext: Context
        private val mTabs = ArrayList<TabInfo>()
        private var selected = 0

        internal class TabInfo(val clss: Class<*>, val args: Bundle?)

        internal class DummyTabFactory(private val mContext: Context) : TabHost.TabContentFactory {

            override fun createTabContent(tag: String?): View {
                val v = View(mContext)
                v.minimumWidth = 0
                v.minimumHeight = 0
                return v
            }
        }

        init {
            mContext = activity
            mTabHost.setOnTabChangedListener(this)
            mViewPager.adapter = this
            mViewPager.addOnPageChangeListener(this)
        }

        fun addTab(tabSpec: TabHost.TabSpec, clss: Class<*>, args: Bundle?) {
            tabSpec.setContent(DummyTabFactory(mContext))
            val info = TabInfo(clss, args)
            mTabs.add(info)
            mTabHost.addTab(tabSpec)
            notifyDataSetChanged()
        }

        override fun getCount(): Int {
            return mTabs.size
        }

        override fun getItem(position: Int): Fragment {
            val info = mTabs[position]

            val frag = mFragmentManager.fragmentFactory.instantiate(mContext.classLoader, info.clss.name).apply {
                arguments = info.args
            }
            if (frag is TabHandler) {
                frag.setPosition(position)
            }

            return frag
        }


        override fun onTabChanged(tabId: String?) {
            val position = mTabHost.currentTab
            mViewPager.currentItem = position
        }

        override fun onPageScrolled(position: Int, positionOffset: Float,
                                    positionOffsetPixels: Int) {
        }

        override fun onPageSelected(position: Int) {

            val pos = getFragment(position)
            if (pos is TabHandler) {
                pos.onSelected()
            }

            val old = getFragment(selected)
            if (old is TabHandler && selected != position) {
                old.onDeselected()
            }

            mTabHost.currentTab = position
            selected = position
        }

        override fun onPageScrollStateChanged(state: Int) {}
    }
}