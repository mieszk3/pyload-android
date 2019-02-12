package org.pyload.android.client.module;

import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class AllTrustManager implements X509TrustManager {


    public void checkClientTrusted(X509Certificate[] arg0, String arg1) {
    }


    public void checkServerTrusted(X509Certificate[] arg0, String arg1) {
    }


    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }

}
