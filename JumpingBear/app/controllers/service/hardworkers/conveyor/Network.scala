package controllers.service.hardworkers.conveyor

import javax.net.ssl.{HttpsURLConnection, SSLContext, X509TrustManager, TrustManager}
import java.security.cert.X509Certificate

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 17 2014
 */
object Network {
    def byPassSSL() = {
        val trm = new X509TrustManager {
            def getAcceptedIssuers = null

            def checkClientTrusted(p1: Array[X509Certificate], p2: String) = {}

            def checkServerTrusted(p1: Array[X509Certificate], p2: String) = {}
        }

        val sc = SSLContext.getInstance("SSL")
        sc.init(null, Array(trm), null)
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory)
    }
}
