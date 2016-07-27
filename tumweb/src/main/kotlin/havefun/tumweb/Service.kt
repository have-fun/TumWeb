package havefun.tumweb

import havefun.tumweb.Response.MimeType
import havefun.tumweb.injection.ServerComponent
import havefun.tumweb.injection.ServerModule
import havefun.tumweb.injection.DaggerServerComponent
import java.net.HttpURLConnection.HTTP_INTERNAL_ERROR
import java.net.HttpURLConnection.HTTP_NOT_FOUND
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import javax.inject.Inject

abstract class Service(component: ServerComponent) {
    enum class State {
        BEING_STARTED, STARTED, BEING_STOPPED, STOPPED
    }

    var state: State = State.STOPPED
    private var routingTable: MutableList<RoutingEntry>

    @Inject lateinit var host: Host

    init {
        component.inject(this)
        host.handler = { session -> handle(session) }
        routingTable = LinkedList()
    }

    constructor(portNo: Int): this(defaultComponent(portNo))

    fun start() {
        if (state != State.BEING_STARTED && state != State.STARTED) {
            state = State.BEING_STARTED

            routingTable = LinkedList()
            initRouter()

            host.start()
            state = State.STARTED
        }
    }

    fun stop() {
        if (state != State.BEING_STOPPED && state != State.STOPPED) {
            host.stop()
            state = State.STOPPED
        }
    }

    abstract protected fun initRouter()

    protected fun route(method: Host.Method, path: String, to: (Request) -> Response) {
        routingTable.add(RoutingEntry(method, path.toRegex(), to))
    }

    private fun handle(session: Session): Response {
        var groups: MatchGroupCollection? = null

        val routingEntry = routingTable.firstOrNull {
            val result = it.path.matchEntire(session.uri)
            groups = result?.groups

            session.method == it.method && result != null
        }

        try {
            // TODO: Check this bug - java.lang.ClassCastException: havefun.tumweb.Service$RoutingEntry cannot be cast to kotlin.jvm.functions.Function1
            // Workaround for this issue:- https://youtrack.jetbrains.com/issue/KT-8252
            var response = (routingEntry?.to?.invoke(Request(session, groups)))
            return response ?: Response(HTTP_NOT_FOUND)
        } catch (e: Throwable) {
            log(Level.SEVERE, "${session.method} ${session.uri}", e)
            return Response(HTTP_INTERNAL_ERROR, MimeType.TEXT, "${e.message}\n\n${e.stackTrace.joinToString("\n")}")
        }
    }

    companion object {
        fun defaultComponent(portNo: Int) = DaggerServerComponent.builder().serverModule(ServerModule(portNo)).build()
        fun log(level: Level, message: String, error: Throwable) = {
            Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).log(level, message, error)
        }
    }

    private data class Mapper(val map: (Int) -> Int)

    private data class RoutingEntry(val method: Host.Method, val path: Regex, val to: (Request) -> Response)
}
