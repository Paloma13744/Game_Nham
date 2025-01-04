package app.com.gamenhamnham

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.com.gamenhamnham.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
}
