package dag.podkast.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import dag.podkast.R
import dag.podkast.util.PodcastException
import dag.podkast.util.ToastOnUiThread
import dag.podkast.viewmodel.ChannelOverviewViewModel
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class ChannelEditorUi(private val channelOverviewViewModel: ChannelOverviewViewModel) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val channelEditorView = inflater.inflate(R.layout.channeleditor, null)

        val builder = AlertDialog.Builder(activity)
        builder.setView(channelEditorView)

        val channelIdView = channelEditorView.findViewById<EditText>(R.id.channeleditor_id)
        val channelNameView = channelEditorView.findViewById<EditText>(R.id.channeleditor_name)
        val rssUrlView = channelEditorView.findViewById<EditText>(R.id.channeleditor_rssurl)
        val earliestPubdateView = channelEditorView.findViewById<EditText>(R.id.channeleditor_earliestpubdate)
        earliestPubdateView.setText("01.01.1970")

        builder.setPositiveButton("OK") { _, _ ->
            try {
                val earliestPubDate: LocalDate
                try {
                    earliestPubDate = LocalDate.parse(earliestPubdateView.text.toString(), DateTimeFormatter.ofPattern("dd.MM.yyyy"))
                } catch (e: DateTimeParseException) {
                    throw PodcastException(e.message!!)
                }

                channelOverviewViewModel.newChannel(
                        channelIdView.text.toString(),
                        channelNameView.text.toString(),
                        rssUrlView.text.toString(),
                        LocalDateTime.of(earliestPubDate.year, earliestPubDate.monthValue, earliestPubDate.dayOfMonth, 0, 0))
            } catch (e: PodcastException) {
                ToastOnUiThread.show(this@ChannelEditorUi.activity!!, "Ugyldig ny kanal: ${e.message}")
            }
        }.setNegativeButton("Avbryt") { _, _ -> }

        return builder.create()
    }
}
