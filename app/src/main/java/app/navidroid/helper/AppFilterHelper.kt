package app.navidroid.helper

import app.navidroid.data.AppModel

interface AppFilterHelper {
    fun onAppFiltered(items:List<AppModel>)
}