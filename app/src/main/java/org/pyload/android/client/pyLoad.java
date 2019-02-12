package org.pyload.android.client;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TextView;

import org.pyload.android.client.components.FragmentTabsPager;
import org.pyload.android.client.dialogs.AccountDialog;
import org.pyload.android.client.fragments.CollectorFragment;
import org.pyload.android.client.fragments.OverviewFragment;
import org.pyload.android.client.fragments.QueueFragment;
import org.pyload.android.client.module.Eula;
import org.pyload.android.client.module.GuiTask;
import org.pyload.thrift.Destination;
import org.pyload.thrift.PackageDoesNotExists;
import org.pyload.thrift.Pyload.Client;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import androidx.core.content.ContextCompat;

public class pyLoad extends FragmentTabsPager {

    private pyLoadApp app;

    // keep reference to set indeterminateProgress
    private MenuItem refreshItem;

    /**
     * Called when the activity is first created.
     */

    public void onCreate(Bundle savedInstanceState) {

        Log.d("pyLoad", "Starting pyLoad App");

        app = (pyLoadApp) getApplicationContext();
        app.prefs = PreferenceManager.getDefaultSharedPreferences(this);
        initLocale();

        super.onCreate(savedInstanceState);
        Eula.show(this);

        app.cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        app.init(this);

        TabHost.TabSpec spec; // Resusable TabSpec for each tab
        String title;

        int tab_pyload, tab_queue, tab_collector;
        if (app.prefs.getBoolean("invert_tabs", false)) {
            tab_pyload = R.drawable.ic_tab_pyload_inverted;
            tab_queue = R.drawable.ic_tab_queue_inverted;
            tab_collector = R.drawable.ic_tab_collector_inverted;
        } else {
            tab_pyload = R.drawable.ic_tab_pyload;
            tab_queue = R.drawable.ic_tab_queue;
            tab_collector = R.drawable.ic_tab_collector;
        }

        title = getString(R.string.overview);
        spec = mTabHost.newTabSpec(title).setIndicator(title,
                ContextCompat.getDrawable(this, tab_pyload));
        mTabsAdapter.addTab(spec, OverviewFragment.class, null);

        title = getString(R.string.queue);
        spec = mTabHost.newTabSpec(title).setIndicator(title,
                ContextCompat.getDrawable(this, tab_queue));
        mTabsAdapter.addTab(spec, QueueFragment.class, null);

        title = getString(R.string.collector);
        spec = mTabHost.newTabSpec(title).setIndicator(title,
                ContextCompat.getDrawable(this, tab_collector));
        mTabsAdapter.addTab(spec, CollectorFragment.class, null);

        int tabCount = mTabHost.getTabWidget().getTabCount();
        for (int i = 0; i < tabCount; i++) {
            final View view = mTabHost.getTabWidget().getChildTabViewAt(i);
            if (view != null) {
                final View textView = view.findViewById(android.R.id.title);
                if (textView instanceof TextView) {
                    ((TextView) textView).setGravity(Gravity.CENTER);
                    ((TextView) textView).setSingleLine(false);

                    textView.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;
                    textView.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();

        // we got a SHARE intent
        if (Intent.ACTION_SEND.equals(action)) {
            Intent addURL = new Intent(app, AddLinksActivity.class);
            addURL.putExtra("url", intent.getStringExtra(Intent.EXTRA_TEXT));
            addURL.putExtra("name", intent.getStringExtra(Intent.EXTRA_SUBJECT));
            startActivityForResult(addURL, AddLinksActivity.NEW_PACKAGE);
            intent.setAction(Intent.ACTION_MAIN);

            // we got a VIEW intent
        } else if (Intent.ACTION_VIEW.equals(action) && data != null) {
            if (intent.getScheme().startsWith("http") || intent.getScheme().contains("ftp")) {
                Intent addURL = new Intent(app, AddLinksActivity.class);
                addURL.putExtra("url", data.toString());
                startActivityForResult(addURL, AddLinksActivity.NEW_PACKAGE);
            } else if (intent.getScheme().equals("file")) {
                Intent addURL = new Intent(app, AddLinksActivity.class);
                addURL.putExtra("dlcpath", data.getPath());
                startActivityForResult(addURL, AddLinksActivity.NEW_PACKAGE);
            }
            intent.setData(null);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        app.refreshTab();
    }

    @Override
    protected void onPause() {
        super.onPause();
        app.clearTasks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        refreshItem = menu.findItem(R.id.refresh);
        refreshItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menu.findItem(R.id.add_links).setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_links:
                startActivityForResult(new Intent(app, AddLinksActivity.class),
                        AddLinksActivity.NEW_PACKAGE);

                return true;

            case R.id.refresh:
                app.resetClient();
                app.refreshTab();

                return true;

            case R.id.settings:
                Intent settingsActivity = new Intent(app, Preferences.class);
                startActivity(settingsActivity);

                return true;

            case R.id.show_accounts:
                AccountDialog accountsList = new AccountDialog();
                accountsList.show(getSupportFragmentManager(), "accountsDialog");

                return true;

            case R.id.remote_settings:
                Intent serverConfigActivity = new Intent(app, RemoteSettings.class);
                startActivity(serverConfigActivity);

                return true;

            case R.id.restart_failed:
                app.addTask(new GuiTask(() -> {
                    Client client = app.getClient();
                    client.restartFailed();
                }, app.handleSuccess));

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case AddLinksActivity.NEW_PACKAGE:
                switch (resultCode) {
                    case RESULT_OK:
                        final String name = data.getStringExtra("name");
                        final String[] link_array = data.getStringExtra("links").trim()
                                .split("\n");
                        final Destination dest;
                        final String filepath = data.getStringExtra("filepath");
                        final String filename = data.getStringExtra("filename");

                        if (data.getIntExtra("dest", 0) == 0)
                            dest = Destination.Queue;
                        else
                            dest = Destination.Collector;

                        final ArrayList<String> links = new ArrayList<>();
                        for (String link_row : link_array)
                            for (String link : link_row.trim().split(" "))
                                if (!link.equals(""))
                                    links.add(link);

                        final String password = data.getStringExtra("password");

                        app.addTask(new GuiTask(() -> {
                            Client client = app.getClient();

                            if (links.size() > 0) {
                                int pid = client.addPackage(name, links, dest);

                                if (password != null && !password.equals("")) {

                                    HashMap<String, String> opts = new HashMap<>();
                                    opts.put("password", password);

                                    try {
                                        client.setPackageData(pid, opts);
                                    } catch (PackageDoesNotExists e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                            if (filename != null && !filepath.equals("")) {

                                File file = new File(filepath);
                                try {
                                    if (file.length() > (1 << 20))
                                        throw new Exception("File size to large");
                                    FileInputStream is = new FileInputStream(file);
                                    ByteBuffer buffer = ByteBuffer
                                            .allocate((int) file.length());

                                    while (is.getChannel().read(buffer) > 0) ;

                                    buffer.rewind();
                                    is.close();
                                    client.uploadContainer(filename, buffer);

                                } catch (Throwable e) {
                                    Log.e("pyLoad", "Error when uploading file", e);
                                }
                            }

                        }, app.handleSuccess));
                        break;
                    default:
                        break;
                }
                break;

            default:
                super.onActivityResult(requestCode, resultCode, data);
        }

    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("pyLoad", "got Intent");
        super.onNewIntent(intent);
    }

    /**
     * Sets the locale defined in config.
     */
    private void initLocale() {

        String language = app.prefs.getString("language", "");
        Locale locale;
        if ("".equals(language))
            locale = Locale.getDefault();
        else
            locale = new Locale(language);

        Log.d("pyLoad", "Change locale to: " + locale);
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    public void setCaptchaResult(final short tid, final String result) {
        app.addTask(new GuiTask(() -> {
            Client client = app.getClient();
            Log.d("pyLoad", "Send Captcha result: " + tid + " " + result);
            client.setCaptchaResult(tid, result);
        }));
    }

    public MenuItem getRefreshItem() {
        return refreshItem;
    }
}