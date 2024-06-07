
import android.content.Intent
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.mynewapp.ConnectWearActivity

class OpenActivity(context: ReactApplicationContext?) : ReactContextBaseJavaModule(context) {
    override fun getName(): String {
        return "connectWear"
    }

    @ReactMethod
    fun connectWearApp() {
        val intent = Intent(
            currentActivity,
            ConnectWearActivity::class.java
        )
        currentActivity!!.startActivity(intent)
    }
}
