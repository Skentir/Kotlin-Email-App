package ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.databinding.ActivityEmailScreenBinding

class EmailScreen : AppCompatActivity() {
    private lateinit var binding : ActivityEmailScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEmailScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Receive bundle
        val bundle = intent.extras
        // Map bundle to fields
        binding.latestRecipient.text = bundle!!.getString("recipient")
        binding.latestSubject.text = bundle.getString("subject")
        binding.latestBody.text = bundle.getString("body")
    }
}