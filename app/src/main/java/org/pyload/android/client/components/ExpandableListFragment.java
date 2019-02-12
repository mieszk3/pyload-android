package org.pyload.android.client.components;


import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ExpandableListFragment extends Fragment
        implements OnCreateContextMenuListener,
        ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupCollapseListener,
        ExpandableListView.OnGroupExpandListener {

    private static final int INTERNAL_EMPTY_ID = 0x00ff0001;

    final private Handler mHandler = new Handler();

    final private Runnable mRequestFocus = new Runnable() {
        public void run() {
            mList.focusableViewAvailable(mList);
        }
    };

    final private OnChildClickListener mOnChildClickListener = ExpandableListFragment.this;

    private ExpandableListAdapter mAdapter;
    private ExpandableListView mList;
    private View mEmptyView;
    private View mListContainer;
    private boolean mListShown;

    public ExpandableListFragment() {
    }

    /**
     * Provide default implementation to return a simple list view.  Subclasses
     * can override to replace with their own layout.  If doing so, the
     * returned view hierarchy <em>must</em> have a ListView whose id
     * is {@link android.R.id#list android.R.id.list} and can optionally
     * have a sibling view id {@link android.R.id#empty android.R.id.empty}
     * that is to be shown when the list is empty.
     *
     * <p>If you are overriding this method with your own custom content,
     * consider including the standard layout {@link android.R.layout#list_content}
     * in your layout file, so that you continue to retain all of the standard
     * behavior of ListFragment.  In particular, this is currently the only
     * way to have the built-in indeterminant progress state be shown.
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FrameLayout root = new FrameLayout(getContext());

        TextView tv = new TextView(getContext());
        tv.setId(INTERNAL_EMPTY_ID);
        tv.setGravity(Gravity.CENTER);
        root.addView(tv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ExpandableListView lv = new ExpandableListView(getContext());
        lv.setId(android.R.id.list);
        lv.setDrawSelectorOnTop(false);
        root.addView(lv, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ExpandableListView.LayoutParams lp = new ListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        root.setLayoutParams(lp);

        return root;
    }

    /**
     * Attach to list view once Fragment is ready to run.
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ensureList();
    }

    /**
     * Detach from list view.
     */
    @Override
    public void onDestroyView() {
        mHandler.removeCallbacks(mRequestFocus);
        mList = null;
        super.onDestroyView();
    }

    /**
     * Provide the cursor for the list view.
     */
    protected void setListAdapter(ExpandableListAdapter adapter) {
        boolean hadAdapter = mAdapter != null;
        mAdapter = adapter;
        if (mList != null) {
            mList.setAdapter(adapter);
            if (!mListShown && !hadAdapter) {
                // The list was hidden, and previously didn't have an
                // adapter.  It is now time to show it.
                setListShown(true, getView().getWindowToken() != null);
            }
        }
    }

    /**
     * Control whether the list is being displayed.  You can make it not
     * displayed if you are waiting for the initial data to show in it.  During
     * this time an indeterminant progress indicator will be shown instead.
     *
     * @param shown   If true, the list view is shown; if false, the progress
     *                indicator.  The initial value is true.
     * @param animate If true, an animation will be used to transition to the
     *                new state.
     */
    private void setListShown(boolean shown, boolean animate) {
        ensureList();
        if (mListShown == shown) {
            return;
        }
        mListShown = shown;
        if (shown) {
            if (animate) {
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_in));
            }
            mListContainer.setVisibility(View.VISIBLE);
        } else {
            if (animate) {
                mListContainer.startAnimation(AnimationUtils.loadAnimation(
                        getActivity(), android.R.anim.fade_out));
            }
            mListContainer.setVisibility(View.GONE);
        }
    }

    /**
     * Get the ListAdapter associated with this activity's ListView.
     */
    protected ExpandableListAdapter getExpandableListAdapter() {
        return mAdapter;
    }

    private void ensureList() {
        if (mList != null) {
            return;
        }
        View root = getView();
        if (root == null) {
            throw new IllegalStateException("Content view not yet created");
        }
        if (root instanceof ExpandableListView) {
            mList = (ExpandableListView) root;
        } else {
            TextView mStandardEmptyView = root.findViewById(INTERNAL_EMPTY_ID);
            if (mStandardEmptyView == null) {
                mEmptyView = root.findViewById(android.R.id.empty);
            }
            View rawListView = root.findViewById(android.R.id.list);
            mListContainer = rawListView;
            if (!(rawListView instanceof ExpandableListView)) {
                if (rawListView == null) {
                    throw new RuntimeException(
                            "Your content must have a ExpandableListView whose id attribute is " +
                                    "'android.R.id.list'");
                }
                throw new RuntimeException(
                        "Content has view with id attribute 'android.R.id.list' "
                                + "that is not a ExpandableListView class");
            }
            mList = (ExpandableListView) rawListView;
            if (mEmptyView != null) {
                mList.setEmptyView(mEmptyView);
            }
        }
        mListShown = true;
        mList.setOnChildClickListener(mOnChildClickListener);
        if (mAdapter != null) {
            setListAdapter(mAdapter);
        } else {
            // We are starting without an adapter, so assume we won't
            // have our data right away and start with the progress indicator.
            setListShown(false, false);
        }
        mHandler.post(mRequestFocus);
    }

    @Override
    public void onGroupExpand(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onGroupCollapse(int arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public boolean onChildClick(ExpandableListView arg0, View arg1, int arg2,
                                int arg3, long arg4) {
        return false;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
    }
}

