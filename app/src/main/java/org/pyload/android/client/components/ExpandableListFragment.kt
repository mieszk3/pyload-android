package org.pyload.android.client.components


import android.os.Bundle
import android.os.Handler
import android.view.*
import android.view.ContextMenu.ContextMenuInfo
import android.view.View.OnCreateContextMenuListener
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.fragment.app.Fragment

open class ExpandableListFragment : Fragment(), OnCreateContextMenuListener, ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupCollapseListener, ExpandableListView.OnGroupExpandListener {
    private val mHandler = Handler()
    private val mRequestFocus = Runnable { mList?.focusableViewAvailable(mList) }
    private val mOnChildClickListener = this@ExpandableListFragment

    /**
     * Get the ListAdapter associated with this activity's ListView.
     */
    protected var expandableListAdapter: ExpandableListAdapter? = null
        private set
    private var mList: ExpandableListView? = null
    private var mEmptyView: View? = null
    private var mListContainer: View? = null
    private var mListShown: Boolean = false

    /**
     * Provide default implementation to return a simple list view.  Subclasses
     * can override to replace with their own layout.  If doing so, the
     * returned view hierarchy *must* have a ListView whose id
     * is [android.R.id.list] and can optionally
     * have a sibling view id [android.R.id.empty]
     * that is to be shown when the list is empty.
     *
     *
     * If you are overriding this method with your own custom content,
     * consider including the standard layout [android.R.layout.list_content]
     * in your layout file, so that you continue to retain all of the standard
     * behavior of ListFragment.  In particular, this is currently the only
     * way to have the built-in indeterminant progress state be shown.
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val context = inflater.context
        val root = FrameLayout(context)

        val tv = TextView(context)
        tv.id = INTERNAL_EMPTY_ID
        tv.gravity = Gravity.CENTER
        root.addView(tv, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        val lv = ExpandableListView(context)
        lv.id = android.R.id.list
        lv.setDrawSelectorOnTop(false)
        root.addView(lv, FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        val lp = AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        root.layoutParams = lp

        return root
    }

    /**
     * Attach to list view once Fragment is ready to run.
     */
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        ensureList()
    }

    /**
     * Detach from list view.
     */
    override fun onDestroyView() {
        mHandler.removeCallbacks(mRequestFocus)
        mList = null
        super.onDestroyView()
    }

    /**
     * Provide the cursor for the list view.
     */
    protected fun setListAdapter(adapter: ExpandableListAdapter) {
        val hadAdapter = expandableListAdapter != null
        expandableListAdapter = adapter
        val list = mList
        if (list != null) {
            list.setAdapter(adapter)
            val view = view
            if (!mListShown && !hadAdapter && view != null) {
                // The list was hidden, and previously didn't have an
                // adapter.  It is now time to show it.
                setListShown(true, view.windowToken != null)
            }
        }
    }

    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     *
     * @param shown   If true, the list view is shown; if false, the progress
     * indicator.  The initial value is true.
     * @param animate If true, an animation will be used to transition to the
     * new state.
     */
    private fun setListShown(shown: Boolean, animate: Boolean) {
        ensureList()
        if (mListShown == shown) {
            return
        }
        mListShown = shown
        if (shown) {
            if (animate) {
                mListContainer?.startAnimation(AnimationUtils.loadAnimation(
                        activity, android.R.anim.fade_in))
            }
            mListContainer?.visibility = View.VISIBLE
        } else {
            if (animate) {
                mListContainer?.startAnimation(AnimationUtils.loadAnimation(
                        activity, android.R.anim.fade_out))
            }
            mListContainer?.visibility = View.GONE
        }
    }

    private fun ensureList() {
        if (mList != null) {
            return
        }
        val root = view ?: throw IllegalStateException("Content view not yet created")
        if (root is ExpandableListView) {
            mList = root
        } else {
            val mStandardEmptyView = root.findViewById<TextView>(INTERNAL_EMPTY_ID)
            if (mStandardEmptyView == null) {
                mEmptyView = root.findViewById(android.R.id.empty)
            }
            val rawListView = root.findViewById<View>(android.R.id.list)
            mListContainer = rawListView
            if (rawListView !is ExpandableListView) {
                if (rawListView == null) {
                    throw RuntimeException(
                            "Your content must have a ExpandableListView whose id attribute is " + "'android.R.id.list'")
                }
                throw RuntimeException(
                        "Content has view with id attribute 'android.R.id.list' " + "that is not a ExpandableListView class")
            }
            mList = rawListView
            if (mEmptyView != null) {
                rawListView.emptyView = mEmptyView
            }
        }
        mListShown = true
        mList?.setOnChildClickListener(mOnChildClickListener)
        val listAdapter = expandableListAdapter
        if (listAdapter != null) {
            setListAdapter(listAdapter)
        } else {
            // We are starting without an adapter, so assume we won't
            // have our data right away and start with the progress indicator.
            setListShown(shown = false, animate = false)
        }
        mHandler.post(mRequestFocus)
    }

    override fun onGroupExpand(groupPosition: Int) {
    }

    override fun onGroupCollapse(groupPosition: Int) {
    }

    override fun onChildClick(parent: ExpandableListView, v: View, groupPosition: Int,
                              childPosition: Int, id: Long): Boolean {
        return false
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenuInfo?) {}

    companion object {
        private const val INTERNAL_EMPTY_ID = 0x00ff0001
    }
}