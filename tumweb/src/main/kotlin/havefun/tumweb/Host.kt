package havefun.tumweb

interface Host {
    enum class Method {
        GET, POST, PUT, DELETE
    }

    var handler: (Session) -> Response

    fun start()
    fun stop()
}
