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

import android.os.Parcelable
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.viewpager.widget.PagerAdapter

/**
 * Implementation of [PagerAdapter] that
 * represents each page as a [Fragment] that is persistently kept in the
 * fragment manager as long as the user can return to the page.
 */
abstract class FragmentPagerAdapter internal constructor(protected val mFragmentManager: FragmentManager) : PagerAdapter() {
    private var mCurTransaction: FragmentTransaction? = null
    private var container: Int = 0

    /**
     * Return the Fragment associated with a specified position.
     */
    abstract fun getItem(position: Int): Fragment

    override fun startUpdate(container: ViewGroup) {}

    fun getFragment(pos: Int): Fragment? {
        val name = makeFragmentName(container, pos)
        return mFragmentManager.findFragmentByTag(name)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val transaction = mCurTransaction ?: let {
            val beginTransaction = mFragmentManager.beginTransaction()
            mCurTransaction = beginTransaction
            beginTransaction
        }

        this.container = container.id
        // Do we already have this fragment?
        val name = makeFragmentName(container.id, position)
        var fragment = mFragmentManager.findFragmentByTag(name)
        if (fragment != null) {
            if (DEBUG) {
                Log.v(TAG, "Attaching item #$position: f=$fragment")
            }
            transaction.attach(fragment)
        } else {
            fragment = getItem(position)
            if (DEBUG) {
                Log.v(TAG, "Adding item #$position: f=$fragment")
            }
            transaction.add(container.id, fragment,
                    makeFragmentName(container.id, position))
        }

        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        if (any is Fragment) {
            val transaction = mCurTransaction ?: let {
                val beginTransaction = mFragmentManager.beginTransaction()
                mCurTransaction = beginTransaction
                beginTransaction
            }
            if (DEBUG) {
                Log.v(TAG, "Detaching item #" + position + ": f=" + any + " v="
                        + any.view)
            }
            transaction.detach(any)
        }
    }

    override fun finishUpdate(container: ViewGroup) {
        val transaction = mCurTransaction
        if (transaction != null) {
            transaction.commit()
            mCurTransaction = null
            mFragmentManager.executePendingTransactions()
        }
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return (any as? Fragment)?.view === view
    }

    override fun saveState(): Parcelable? {
        return null
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    companion object {
        private const val TAG = "FragmentPagerAdapter"
        private const val DEBUG = false

        private fun makeFragmentName(viewId: Int, index: Int): String {
            return "android:switcher:$viewId:$index"
        }
    }
}
