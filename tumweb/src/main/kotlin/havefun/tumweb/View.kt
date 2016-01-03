package havefun.tumweb

import com.google.gson.Gson
import havefun.tumweb.Response.MimeType.JSON

open class View(val model: Any) {
    open val json: Response
        get() = Response(JSON, model.toJson())

    companion object {
        val gson = Gson()

        fun Any.toJson(): String {
            return gson.toJson(this)
        }
    }

}