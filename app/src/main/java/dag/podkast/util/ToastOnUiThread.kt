package dag.podkast.util

import android.app.Activity
import android.widget.Toast

object ToastOnUiThread {
    @JvmOverloads
    fun show(activity: Activity, s: CharSequence, length: Int = Toast.LENGTH_SHORT) {
        activity.runOnUiThread { Toast.makeText(activity, s, length).show() }

    }
}
