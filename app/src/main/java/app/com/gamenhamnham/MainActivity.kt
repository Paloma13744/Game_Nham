package app.com.gamenhamnham

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.com.gamenhamnham.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inicializar e começar a música
        mediaPlayer = MediaPlayer.create(this, R.raw.background_music)
        mediaPlayer?.isLooping = true // Configura para repetir a música
        mediaPlayer?.start()

        // Configurar o clique do botão "Start Game"
        binding.startButton.setOnClickListener {
            val intent = Intent(this, JogoActivity::class.java)
            startActivity(intent)
        }

        // Configurar o clique do botão "Regras"
        binding.regrasButton.setOnClickListener {
            val intent = Intent(this, RegrasActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Parar e liberar os recursos do MediaPlayer
        mediaPlayer?.release()
        mediaPlayer = null
    }

    override fun onPause() {
        super.onPause()
        // Pausar a música quando a Activity estiver em pausa
        mediaPlayer?.pause()
    }

    override fun onResume() {
        super.onResume()
        // Continuar a música quando a Activity for retomada
        mediaPlayer?.start()
    }
}
