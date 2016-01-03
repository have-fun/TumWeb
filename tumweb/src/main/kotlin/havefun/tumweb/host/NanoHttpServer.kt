package havefun.tumweb.host

import fi.iki.elonen.NanoHTTPD
import fi.iki.elonen.NanoHTTPD.Response.Status
import havefun.tumweb.Host
import havefun.tumweb.Session
import havefun.tumweb.session
import havefun.tumweb.Response as ServiceResponse

class NanoHttpServer(port: Int) : Host, NanoHTTPD(port) {
    override lateinit var handler: (Session) -> ServiceResponse

    override fun serve(httpSession: IHTTPSession): Response {
        val requestSession = toRequestSession(httpSession)
        val response = handler(requestSession)

        return newFixedLengthResponse(response.status.httpStatus, response.mimeType.toString(), response.body, response.size)
    }

    private fun toRequestSession(httpSession: IHTTPSession) =
        session {
            uri { httpSession.uri }
            headers { httpSession.headers }
            method { httpSession.method.toString() }
            cookies { httpSession.cookies.toMapBy({ it }, { httpSession.cookies.read(it.toString()) })}
            params { httpSession.parms }
            content {
                val data = hashMapOf<String, String>()
                httpSession.parseBody(data)
                data["postData"] ?: data["content"] ?: ""
            }
            inputStream { httpSession.inputStream }  // Read timeout bug in NanoHttpd (2.2). Use Content instead.
        }

    private val Int.httpStatus: Response.Status
        get() {
            val status = Status.values().firstOrNull { it.requestStatus == this }
            return status ?: Status.INTERNAL_ERROR
        }
}
