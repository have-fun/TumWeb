package havefun.tumweb

import com.google.gson.Gson

data class Request(val session: Session, val groups: MatchGroupCollection?) {
    val params = session.params

    fun <T> model(modelClass: Class<T>) = gson.fromJson(session.content, modelClass)

    companion object {
        val gson = Gson()
    }
}