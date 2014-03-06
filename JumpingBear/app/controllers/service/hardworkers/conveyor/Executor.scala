package controllers.service.hardworkers.conveyor

import java.io.InputStream
import java.net.{HttpURLConnection, URL}
import scala.io.Source
import controllers.service.backbone.modelobj.execute.{ExecuteOutput, ExecuteInput}
import controllers.service.backbone.modelobj.Header
import play.Logger

/**
 *
 * @author Hongfei Zhou
 * @version 1.0, Jan. 09 2014
 */
object Executor {
    def execute(request: ExecuteInput): (String, ExecuteOutput) = request match {
        case ExecuteInput(method, url, headers, cookies, body) => {
            Network.byPassSSL()
            val connection = new URL(url).openConnection().asInstanceOf[HttpURLConnection]
            connection.setRequestMethod(method)

            headers.map(_.foreach(h => connection.setRequestProperty(h.name, h.value)))

            body.foreach(b => {
                connection.setDoOutput(true)
                connection.getOutputStream.write(b.getBytes)
            })

            var responseStream: InputStream = null
            try {
                responseStream = connection.getInputStream
            } catch {
                case e: Exception => responseStream = connection.getErrorStream
            }
            val responseBody = Source.fromInputStream(responseStream).getLines().mkString("\n").trim

            def readHeader(connection: HttpURLConnection, index: Int): List[Header] = {
                val headerName = connection.getHeaderFieldKey(index)
                val headerValue = connection.getHeaderField(index)

                if (headerName == null && headerValue == null)
                    List()
                else if (headerName == null)
                    Header("version", headerValue) :: readHeader(connection, index + 1)
                else
                    Header(headerName, headerValue) :: readHeader(connection, index + 1)

            }

            val resHeaders = Header("response-code", connection.getResponseCode.toString) :: readHeader(connection, 0)
            ("SUCCESS", ExecuteOutput(connection.getResponseCode.toString, Some(resHeaders), Some(responseBody)))
        }
    }
}
