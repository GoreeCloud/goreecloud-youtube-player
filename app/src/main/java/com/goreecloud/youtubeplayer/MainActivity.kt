package com.goreecloud.youtubeplayer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.goreecloud.youtubeplayer.data.SQLiteLocalLibraryStore
import com.goreecloud.youtubeplayer.ui.GoreeCloudYouTubePlayerApp

class MainActivity : ComponentActivity() {
    private lateinit var localLibraryStore: SQLiteLocalLibraryStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        localLibraryStore = SQLiteLocalLibraryStore(applicationContext)
        localLibraryStore.ensureReady()
        val localLibrarySummary = localLibraryStore.summary()

        setContent {
            GoreeCloudYouTubePlayerApp(localLibrarySummary = localLibrarySummary)
        }
    }

    override fun onDestroy() {
        if (::localLibraryStore.isInitialized) {
            localLibraryStore.close()
        }
        super.onDestroy()
    }
}
