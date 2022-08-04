package ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.adapter.SentEmailAdapter
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.dao.EmailsDAOSharedPref
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.databinding.ActivityMainBinding
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.model.Email
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    protected lateinit var rvEmailList : RecyclerView
    private lateinit var emailAdapter : SentEmailAdapter
    private lateinit var dao : EmailsDAOSharedPref
    private lateinit var emails: ArrayList<Email>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        rvEmailList = binding.rvEmailList
        // Set dao
        dao = EmailsDAOSharedPref(applicationContext)
        // Populate emails
        emails = dao.getArrayListData()
        // Attach to adapter
        rvEmailList.setLayoutManager(LinearLayoutManager(applicationContext))
        emailAdapter = SentEmailAdapter(applicationContext, emails)
        rvEmailList.setAdapter(emailAdapter)
        // Latest Email Button
        binding.btnLatest.setOnClickListener {
            val emailSize = emails.size
            // No Latest Email
            if (emailSize == 0 || (emailSize == 1 && emails.get(0).isDraft == 1)) {
                alertEmpty()
            } else {
                var sentIdx = emailSize-1
                if (emails.get(sentIdx).isDraft == 1)
                    sentIdx -= 1
                // Get latest email
                val email : Email = emails.get(sentIdx)
                val bundle = Bundle()
                bundle.putString("recipient", email.recipient)
                bundle.putString("subject", email.subject)
                bundle.putString("body", email.body)
                // Go to Email Screen Activity
                val goToEmailScreen = Intent(this, EmailScreen::class.java)
                goToEmailScreen.putExtras(bundle)
                startActivity(goToEmailScreen)
            }
        }
        // Send new email
        binding.btnNewEmail.setOnClickListener {
            // Go to NewEmail activity if there's no draft
            if (dao.getDraft() == null) {
                val goToNewEmail = Intent(this, NewEmail::class.java)
                startActivity(goToNewEmail)
            } else {
                // Alert if there's a draft
                alertReplaceDraft()
            }
        }
    }

    private fun alertReplaceDraft() {
        // Delete draft
        val draftIdx = getDraftIndex(emails)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Draft Present")
        builder.setMessage("You have a draft in your mail. Do you want to replace this draft?")
        builder.setNegativeButton("No") {dialog, which: Int ->
            Toast.makeText(applicationContext,
                "Draft retained", Toast.LENGTH_SHORT).show()
        }
        builder.setPositiveButton("Yes") {dialog, which: Int ->
            dao.deleteDraft(dao.getDraft()!!)
            emailAdapter.removeEmail(draftIdx)
            // Switch to Email Screen
            val goToNewEmail = Intent(this, NewEmail::class.java)
            startActivity(goToNewEmail)
        }
        builder.show()
    }

    private fun alertEmpty() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Email Alert")
        builder.setMessage("You don't have any emails")
        builder.setPositiveButton("Okay") {dialog, which: Int ->
            Toast.makeText(applicationContext,
                R.string.alert_ok, Toast.LENGTH_SHORT).show()
        }
        builder.show()
    }

    private fun getDraftIndex(emails: ArrayList<Email>): Int {
        print("Finding...")
        for (item in emails) {
            if (item.isDraft == 1)
                return emails.indexOf(item)
        }
        return -1
    }

    override fun onResume() {
        super.onResume()
        emails = dao.getArrayListData()
        val draft = dao.getDraft()
        if (draft != null && emails.indexOf(draft) >= 0){
            emails.remove(draft)
            emails.add(0, draft)
        }
        emailAdapter = SentEmailAdapter(applicationContext, emails)
        rvEmailList.setAdapter(emailAdapter)
        println("New emails are updated")
    }
}

