package ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.dao.EmailsDAOSharedPref
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.databinding.ActivityNewEmailBinding
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.model.Email
import java.text.SimpleDateFormat
import java.util.*

class NewEmail : AppCompatActivity() {
    private lateinit var binding: ActivityNewEmailBinding
    private lateinit var dao: EmailsDAOSharedPref

    // All fields valid create an email
    private lateinit var recipient: String
    private lateinit var subject: String
    private lateinit var body: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Set dao
        dao = EmailsDAOSharedPref(applicationContext)
        // Receive bundle if applicable
        val bundle = intent.extras
        // Map bundle to fields
        if (bundle != null) {
            binding.newRecipient.setText(bundle.getString("recipient"))
            binding.newSubject.setText(bundle.getString("subject"))
            binding.newBody.setText(bundle.getString("body"))
        }
        // Clear fields on discard
        binding.btnDiscard.setOnClickListener {
            binding.newRecipient.getText().clear()
            binding.newSubject.getText().clear()
            binding.newBody.getText().clear()
            // Delete the draft
            if (bundle != null) {
                dao.deleteDraftID(bundle.getInt("id"))
            } else if (dao.getDraft() != null) {
                // Delete existing draft
                dao.deleteDraft(dao.getDraft()!!)
            }
            val goToMainActivity = Intent(this,MainActivity::class.java)
            startActivity(goToMainActivity)
        }

        // Send the email
        binding.btnSend.setOnClickListener {
            if (allFieldsValid(binding.newRecipient, binding.newSubject, binding.newBody)) {
                // All fields valid create an email
                getMainData()
                // Add updated date
                val updated_at = getUpdatedDate()
                // Update if it's a draft
                if (bundle != null) {
                    val email_id = bundle.getInt("id")
                    // Update draft status
                    val email = Email(email_id, recipient, subject, body, 0, updated_at)
                    dao.updateEmail(email)
                    println("Updated a draft!")
                } else {
                    if (dao.getDraft() == null) {
                        // Add to sent email
                        val email = Email(0, recipient, subject, body, 0, updated_at)
                        dao.addEmail(email)
                        println("Added a new email!")
                    } else {
                        // Update draft status
                        val email = Email(dao.getDraft()!!.id, recipient, subject, body, 0, updated_at)
                        dao.updateEmail(email)
                        println("Updated a draft!")
                    }

                }
                // Go to next activity
                val goToMainActivity = Intent(this, MainActivity::class.java)
                startActivity(goToMainActivity)
            } else {
                // Notify if incomplete
                Toast.makeText(
                    applicationContext,
                    "Incomplete email contents", Toast.LENGTH_LONG
                ).show()
            }
        }

        binding.newRecipient.addTextChangedListener {
            if (oneFieldValid(binding.newRecipient, binding.newSubject, binding.newBody)) {
                println("newSub: onefield is valid")
                // Get recipient, subject, and body
                getMainData()
                // Add the date
                val updated_at = getUpdatedDate()
                if (bundle != null) {
                    val email_id = bundle.getInt("id")
                    // Update draft status to sent email
                    val email = Email(email_id, recipient, subject, body, 1, updated_at)
                    dao.updateEmail(email)
                    println("Draft was edited and saved")
                } else {
                    // Create a draft if there is none
                    if (dao.getDraft() == null) {
                        val email = Email(0, recipient, subject, body, 1, updated_at)
                        dao.addEmail(email)
                        println("A new draft has been created")
                    } else if (dao.getDraft() != null) {
                        // Update existing draft
                        val email =
                            Email(dao.getDraft()!!.id, recipient, subject, body, 1, updated_at)
                        dao.updateEmail(email)
                    }
                }
            }
        }

        binding.newSubject.addTextChangedListener {
            if (oneFieldValid(binding.newRecipient, binding.newSubject, binding.newBody)) {
                println("newSub: onefield is valid")
                // Get recipient, subject, and body
                getMainData()
                // Add the date
                val updated_at = getUpdatedDate()
                if (bundle != null) {
                    val email_id = bundle.getInt("id")
                    // Update draft
                    val email = Email(email_id, recipient, subject, body, 1, updated_at)
                    dao.updateEmail(email)
                    println("Draft was edited and saved")
                } else {
                    // Create a draft if there is none
                    if (dao.getDraft() == null) {
                        val email = Email(0, recipient, subject, body, 1, updated_at)
                        dao.addEmail(email)
                        println("A new draft has been created")
                    } else if (dao.getDraft() != null) {
                        // Update existing draft
                        val email =
                            Email(dao.getDraft()!!.id, recipient, subject, body, 1, updated_at)
                        dao.updateEmail(email)
                    }
                }
            }
        }

        binding.newBody.addTextChangedListener {
            if (oneFieldValid(binding.newRecipient, binding.newSubject, binding.newBody)) {
                println("newBody: onefield is valid")
                // Get recipient, subject, and body
                getMainData()
                // Add the date
                val updated_at = getUpdatedDate()
                if (bundle != null) {
                    val email_id = bundle.getInt("id")
                    // Update draft
                    val email = Email(email_id, recipient, subject, body, 1, updated_at)
                    dao.updateEmail(email)
                    println("Draft was edited and saved")
                } else {
                    // Create a draft if there is none
                    if (dao.getDraft() == null) {
                        val email = Email(0, recipient, subject, body, 1, updated_at)
                        dao.addEmail(email)
                        println("A new draft has been created")
                    } else if (dao.getDraft() != null) {
                        // Update existing draft
                        val email =
                            Email(dao.getDraft()!!.id, recipient, subject, body, 1, updated_at)
                        dao.updateEmail(email)
                    }
                }
            }
        }

    }

    private fun getMainData() {
        recipient = binding.newRecipient.getText().toString()
        subject = binding.newSubject.getText().toString()
        body = binding.newBody.getText().toString()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getUpdatedDate(): String {
        val date = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val updated_at: String = formatter.format(date)
        return updated_at
    }

    private fun allFieldsValid(recipient: EditText, subject: EditText, newBody: EditText): Boolean {
        val flag = recipient.getText().toString().length >= 1 &&
                subject.getText().toString().length >= 1 &&
                newBody.getText().toString().length >= 1
        return flag
    }

    private fun oneFieldValid(recipient: EditText, subject: EditText, newBody: EditText): Boolean {
        val flag = recipient.getText().toString().length >= 1 ||
                subject.getText().toString().length >= 1 ||
                newBody.getText().toString().length >= 1
        return flag
    }
}