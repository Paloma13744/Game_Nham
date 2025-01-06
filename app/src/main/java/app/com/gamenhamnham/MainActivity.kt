package app.com.gamenhamnham

import android.content.Intent
import android.media.MediaPlayer
import android.media.AudioManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.com.gamenhamnham.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null
    private var currentTrackIndex = 0
    private val tracks = listOf(
        R.raw.sound_background1,
        R.raw.sound_background2,
        R.raw.sound_background3
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mediaPlayer = MediaPlayer.create(this, tracks[currentTrackIndex])
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()

        binding.startButton.setOnClickListener {
            val intent = Intent(this, tentarSorteActivity::class.java)
            startActivity(intent)
        }

        binding.regrasButton.setOnClickListener {
            val intent = Intent(this, RegrasActivity::class.java)
            startActivity(intent)
        }


        binding.btnPlayAndPause.setOnClickListener {
            if (mediaPlayer?.isPlaying == true) {
                mediaPlayer?.pause()
                binding.btnPlayAndPause.setImageResource(R.drawable.pausar)
            } else {
                mediaPlayer?.start()
                binding.btnPlayAndPause.setImageResource(R.drawable.pausar)
            }
        }

        binding.btnNext.setOnClickListener {
            if (currentTrackIndex < tracks.size - 1) {
                currentTrackIndex++
            } else {
                currentTrackIndex = 0
            }
            playNewTrack()
        }

        val audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        binding.btnSilenciar.setOnClickListener {
            if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) > 0) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
            } else {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2,
                    0
                )
            }
        }
    }

    private fun playNewTrack() {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(this, tracks[currentTrackIndex])
        mediaPlayer?.isLooping = true
        mediaPlayer?.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        mediaPlayer?.start()
    }
}
