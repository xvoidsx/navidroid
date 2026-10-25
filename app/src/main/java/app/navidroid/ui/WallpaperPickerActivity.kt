package app.navidroid.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import app.navidroid.R
import app.navidroid.helper.saveCustomWallpaper
import app.navidroid.helper.showToast

// Trampoline activity for the wallpaper picker. MainActivity is singleTask,
// and singleTask activities never receive activity results: the system
// picker runs in its own task, so its result comes back canceled and
// GetContent yields null — no toast, no change, whatever image is picked.
// This standard-launch-mode activity hosts the picker instead, saves the
// image, and finishes; Settings refreshes its row on resume.
class WallpaperPickerActivity : ComponentActivity() {
    private val picker = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            if (saveCustomWallpaper(this, uri)) showToast(R.string.wallpaper_set)
            else showToast(R.string.wallpaper_failed)
        }
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Only launch fresh; a recreated instance waits for the pending
        // result instead of opening a second picker.
        if (savedInstanceState == null) picker.launch("image/*")
    }
}
