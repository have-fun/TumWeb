package havefun.tumweb

import havefun.tumweb.Response.MimeType.HTML
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.HttpURLConnection.HTTP_OK

class Response (val status: Int, val mimeType: Response.MimeType, val body: InputStream, val size: Long) {

    enum class MimeType {
        HTML {
            override fun toString() = "text/html"
        },
        JSON {
            override fun toString() = "application/json"
        },
        TEXT {
            override fun toString() = "text/plain"
        }
    }

    companion object {
        operator fun invoke(status: Int) = Response(status, HTML, "")

        operator fun invoke(mimeType: MimeType, body: String) = Response(HTTP_OK, mimeType, body)

        operator fun invoke(status: Int, mimeType: MimeType, body: String): Response {
            val byteArray = body.toByteArray()
            return Response(status, mimeType, ByteArrayInputStream(byteArray), byteArray.size.toLong())
        }
    }
}