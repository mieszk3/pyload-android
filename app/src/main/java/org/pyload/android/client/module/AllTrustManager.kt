package org.pyload.android.client.module

import java.security.cert.X509Certificate

import javax.net.ssl.X509TrustManager

class AllTrustManager : X509TrustManager {

    override fun checkClientTrusted(chain: Array<X509Certificate>?, authType: String?) {}

    override fun checkServerTrusted(chain: Array<X509Certificate>?, authType: String?) {}

    override fun getAcceptedIssuers(): Array<X509Certificate>? {
        return null
    }
}