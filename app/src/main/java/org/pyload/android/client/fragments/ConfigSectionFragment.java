package org.pyload.android.client.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.thrift.TException;
import org.pyload.android.client.R;
import org.pyload.android.client.module.GuiTask;
import org.pyload.android.client.pyLoadApp;
import org.pyload.thrift.ConfigItem;
import org.pyload.thrift.ConfigSection;
import org.pyload.thrift.Pyload.Client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class ConfigSectionFragment extends Fragment {

    /**
     * Called after settings were saved
     */
    private final Runnable mRefresh;
    private pyLoadApp app;
    private ConfigSection section;
    private String type;
    private HashMap<String, ConfigItemView> items = new HashMap<String, ConfigItemView>();

    public ConfigSectionFragment(Runnable mRefresh) {
        this.mRefresh = mRefresh;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.config_section, null, false);
        createLayout(view);
        TextView t = view.findViewById(R.id.list_header_title);
        t.setText(section.description);

        view.findViewById(R.id.button_submit)
                .setOnClickListener(arg0 -> onSubmit());

        view.findViewById(R.id.button_cancel)
                .setOnClickListener(arg0 -> onCancel());

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        app = (pyLoadApp) getActivity().getApplicationContext();

        Bundle extras = getArguments();

        section = (ConfigSection) extras.getSerializable("section");
        type = extras.getString("type");
    }

    private View createLayout(@NonNull View view) {
        LinearLayout ll = view.findViewById(R.id.layout_root);
        ll.setOrientation(LinearLayout.VERTICAL);

        for (ConfigItem item : section.items) {
            ConfigItemView c = new ConfigItemView(this.getActivity(), item);
            items.put(item.name, c);
            ll.addView(c);
        }

        return ll;
    }

    private void onSubmit() {

        if (!app.hasConnection())
            return;

        app.addTask(new GuiTask(() -> {

            try {
                Client client = app.getClient();

                for (ConfigItem item : section.items) {
                    ConfigItemView view = items.get(item.name);
                    String newValue = view.getValue();
                    if (!item.value.equals(newValue)) {
                        Log.d("pyLoad", String.format(
                                "Set config value: %s, %s, %s", type,
                                section.name, item.name));

                        client.setConfigValue(section.name, item.name,
                                newValue, type);
                    }
                }
            } catch (TException e) {
                // ignore
            }

            getFragmentManager().popBackStack();

        }, mRefresh));
    }

    private void onCancel() {
        getFragmentManager().popBackStack();
    }
}

class ConfigItemView extends LinearLayout {

    private ConfigItem item;
    private View v;
    private Spinner sp = null;
    private ArrayList<String> choices = null;

    public ConfigItemView(Context context, ConfigItem item) {
        super(context);
        this.item = item;

        setOrientation(LinearLayout.VERTICAL);

        if (!item.type.equals("bool")) {
            TextView tv = new TextView(context);
            tv.setText(item.description);
            tv.setTextColor(Color.WHITE);
            tv.setTextSize(16);
            tv.setPadding(2, 0, 0, 0);
            addView(tv);
        }

        if (item.type.equals("int")) {
            EditText et = new EditText(context);
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            et.setText(item.value);
            v = et;
        } else if (item.type.equals("password")) {
            EditText et = new EditText(context);
            et.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            et.setTransformationMethod(PasswordTransformationMethod
                    .getInstance());
            et.setText(item.value);
            v = et;
        } else if (item.type.equals("bool")) {
            CheckBox cb = new CheckBox(context);
            cb.setText(item.description);

            if (item.value.equals("True")) {
                cb.setChecked(true);
            }

            v = cb;
        } else if (item.type.contains(";")) {
            sp = new Spinner(context);

            choices = new ArrayList<>();
            Collections.addAll(choices, item.type.split(";"));

            ArrayAdapter<String> adp = new ArrayAdapter<>(context,
                    android.R.layout.simple_spinner_item, choices);
            adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            sp.setAdapter(adp);
            sp.setSelection(choices.indexOf(item.value));

            v = sp;
        } else {
            v = new EditText(context);
            ((EditText) v).setText(item.value);
        }

        addView(v);

    }

    /**
     * Returns the string representation of the config item
     *
     * @return
     */
    public String getValue() {
        if (item.type.equals("bool")) {
            CheckBox cb = (CheckBox) v;
            if (cb.isChecked())
                return "True";
            else
                return "False";
        } else if (sp != null) {
            return choices.get(sp.getSelectedItemPosition());
        }
        return ((EditText) v).getText().toString();
    }
}
