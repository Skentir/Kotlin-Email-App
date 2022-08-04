package ph.edu.dlsu.mobdeve.sison.kirsten.androidchallenge.model

data class Email (
    var id: Int,
    var recipient: String,
    var subject: String,
    var body: String,
    var isDraft: Int = 0,
    var updated_at : String
)
