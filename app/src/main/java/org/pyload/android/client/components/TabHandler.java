package org.pyload.android.client.components;

public interface TabHandler {
    void onSelected();

    void onDeselected();

    void setPosition(int pos);
}