package ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.adapter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.EmailScreen
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.NewEmail
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.databinding.DraftEmailBinding
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.databinding.EmailBinding
import ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.model.Email


class SentEmailAdapter(
    private var ctx: Context,
    private var emailArrayList: ArrayList<Email>
) : Adapter<ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
       when (viewType){
           EmailType.DRAFT_EMAIL.value -> {
               val emailBinding = DraftEmailBinding.inflate(
                   LayoutInflater.from(parent.context), parent, false
               )
               DraftViewHolder(emailBinding, ctx)
           }
           else -> {
               val emailBinding = EmailBinding.inflate(
                       LayoutInflater.from(parent.context), parent, false
               )
               EmailViewHolder(emailBinding)
           }
       }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when(emailArrayList[position].isDraft) {
            1 -> {
                (holder as DraftViewHolder).bindDraft(emailArrayList[position])
            }
            0 -> {
                (holder as EmailViewHolder).bindEmail(emailArrayList[position])
            }
        }
    }

    fun removeEmail(position: Int) {
        emailArrayList.removeAt(position)
        notifyItemRemoved(position)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return emailArrayList.size
    }

    override fun getItemViewType(position: Int): Int =
        when (emailArrayList[position].isDraft) {
            1 -> {
                EmailType.DRAFT_EMAIL.value
            }
            else -> {
                EmailType.SENT_EMAIL.value
            }
        }
}

class EmailViewHolder(private val emailBinding: EmailBinding)
    : RecyclerView.ViewHolder(emailBinding.root) {

    fun bindEmail(email: Email) {
        emailBinding.emailRecipient.text = email.recipient
        emailBinding.emailSubject.text = email.subject
        emailBinding.emailBody.text = prepareTextForDisplay(email.body)
    }
}

class DraftViewHolder(private  val draftBinding: DraftEmailBinding, private var ctx: Context)
    : RecyclerView.ViewHolder(draftBinding.root), View.OnClickListener {
    private lateinit var draft: Email

    init {
        itemView.setOnClickListener(this)
    }

    fun bindDraft(email: Email) {
        draftBinding.draftRecipient.text = email.recipient
        draftBinding.draftSubject.text = email.subject
        draftBinding.draftBody.text = prepareTextForDisplay(email.body)
        draft = email
    }

    override fun onClick(p0: View?) {
        val bundle = Bundle()
        bundle.putInt("id", draft.id)
        bundle.putString("recipient", draft.recipient)
        bundle.putString("subject", draft.subject)
        bundle.putString("body", draft.body)
        // Go to Email Screen Activity
        val goToNewEmail = Intent(ctx, NewEmail::class.java)
        goToNewEmail.putExtras(bundle)
        goToNewEmail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(goToNewEmail)
    }
}

private fun prepareTextForDisplay(x: String): String {
    return x.replace("(\\n)+".toRegex(), " ")
}

enum class EmailType(val value:Int) {
    SENT_EMAIL(1),
    DRAFT_EMAIL(2)
}
