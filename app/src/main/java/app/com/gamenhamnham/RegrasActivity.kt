package app.com.gamenhamnham

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import app.com.gamenhamnham.databinding.ActivityRegrasBinding

class RegrasActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegrasBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegrasBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.backButton.setOnClickListener {
            finish()
        }
    }
}
