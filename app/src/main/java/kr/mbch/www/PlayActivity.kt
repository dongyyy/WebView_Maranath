package kr.mbch.www

import android.net.Uri
import android.os.Bundle
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_play.*


class PlayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)

        video.requestFocus()
        video.setMediaController(MediaController(this@PlayActivity))
        val videoUrl = intent.getStringExtra("vodUrl")
        video.setVideoURI(Uri.parse(videoUrl))
        video.start()
    }
}