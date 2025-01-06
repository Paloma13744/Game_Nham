package app.com.gamenhamnham

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random

class tentarSorteActivity : AppCompatActivity() {

    private lateinit var resultadoText: TextView
    private lateinit var videoView: VideoView
    private lateinit var caraButton: Button
    private lateinit var coroaButton: Button
    private var escolhaJogador1: String = ""
    private var resultadoSorteio: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tentar_sorte)

        resultadoText = findViewById(R.id.resultadoText)
        videoView = findViewById(R.id.videoView)
        caraButton = findViewById(R.id.buttonCara)
        coroaButton = findViewById(R.id.buttonCoroa)


        caraButton.setOnClickListener {
            escolhaJogador1 = "Cara"
            caraButton.isEnabled = false
            coroaButton.isEnabled = false
            iniciarVideo()
        }


        coroaButton.setOnClickListener {
            escolhaJogador1 = "Coroa"
            caraButton.isEnabled = false
            coroaButton.isEnabled = false
            iniciarVideo()
        }



        videoView.setOnCompletionListener {

            resultadoSorteio = sortearResultado()


            resultadoText.text = "Você escolheu: $escolhaJogador1\nResultado: $resultadoSorteio"

            Handler().postDelayed({

                val intent = Intent(this, JogoActivity::class.java)
                intent.putExtra("escolhaJogador1", escolhaJogador1)
                intent.putExtra("resultadoSorteio", resultadoSorteio)
                startActivity(intent)
                finish()
            }, 3000)
        }
    }


    private fun sortearResultado(): String {
        return if (Random.nextBoolean()) "Cara" else "Coroa"
    }

    private fun iniciarVideo() {

        resultadoText.text = ""


        val videoUri = Uri.parse("android.resource://$packageName/${R.raw.cara_coroa}")
        videoView.setVideoURI(videoUri)


        videoView.start()
    }
}
