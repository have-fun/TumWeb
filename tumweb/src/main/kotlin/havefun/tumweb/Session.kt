package havefun.tumweb

import java.io.InputStream

class Session private constructor(builder: Session.Builder) {
    val uri: String
    val headers: Map<String, String?>
    val method: Host.Method
    val cookies: Map<String, String?>
    val params: Map<String, String?>
    var content: String
    val inputStream: InputStream

    init {
        uri = builder.uri
        method = builder.method
        headers = builder.headers
        cookies = builder.cookies
        params = builder.params
        content = builder.content
        inputStream = builder.inputStream
    }

    class Builder {
        lateinit var uri: String
        lateinit var headers: Map<String, String?>
        lateinit var method: Host.Method
        lateinit var cookies: Map<String, String?>
        lateinit var params: Map<String, String?>
        lateinit var content: String
        lateinit var inputStream: InputStream

        fun uri(init: Builder.() -> String) {
            uri = init()
        }

        fun headers(init: Builder.() -> Map<String, String?>) {
            headers = init()
        }

        fun method(init: Builder.() -> String) {
            method = Host.Method.valueOf(init())
        }

        fun cookies(init: Builder.() -> Map<String, String?>) {
            cookies = init()
        }

        fun params(init: Builder.() -> Map<String, String?>) {
            params = init()
        }

        fun content(init: Builder.() -> String) {
            content = init()
        }

        fun inputStream(init: Builder.() -> InputStream) {
            inputStream = init()
        }

        fun build() = Session(this)
    }
}

fun session(init: Session.Builder.() -> Unit) : Session {
    var builder = Session.Builder()
    builder.init()
    return builder.build()
}