package dag.podkast.ui

import android.app.Activity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import dag.podkast.R
import dag.podkast.model.Podcast
import dag.podkast.sort.Sort
import dag.podkast.util.EMPTY
import dag.podkast.util.MessageBox
import dag.podkast.util.ToastOnUiThread
import dag.podkast.util.YesNoCancel
import dag.podkast.viewmodel.PodcastViewModel
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

object PodcastListUi {
    fun build(activity: AppCompatActivity, playerUi: PlayerUi, viewModel: PodcastViewModel) {
        val podcastRegexFilterCheckBox = activity.findViewById<CheckBox>(R.id.podcastfilter_isselected)
        val podcastRegexFilterEditText = activity.findViewById<EditText>(R.id.podcastfilter_filtertext)

        podcastRegexFilterCheckBox.isChecked = viewModel.isPodcastRegexFiltering
        podcastRegexFilterCheckBox.setOnCheckedChangeListener { _, enabled ->
            val filterRegex = podcastRegexFilterEditText.text.toString()
            if (checkRegex(activity, filterRegex)) {
                viewModel.isPodcastRegexFiltering = enabled
            } else {
                viewModel.isPodcastRegexFiltering = false
            }
        }

        podcastRegexFilterEditText.setText(viewModel.podcastFilterRegex)
        podcastRegexFilterEditText.setOnEditorActionListener { _, i, _ ->
            if (i == 5) {
                val filterRegex = podcastRegexFilterEditText.text.toString()
                if (checkRegex(activity, filterRegex)) {
                    viewModel.podcastFilterRegex = filterRegex
                } else {
                    viewModel.isPodcastRegexFiltering = false
                }
            }
            true
        }

        val sortSpinner = activity.findViewById<Spinner>(R.id.sortspinner)
        val sortSpinnerAdapter = ArrayAdapter(activity,
                android.R.layout.simple_spinner_item, Sort.sorts)
        sortSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        sortSpinner.adapter = sortSpinnerAdapter
        sortSpinner.setSelection(Sort.getSortIndex(viewModel.sort))
        sortSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View, i: Int, l: Long) {
                viewModel.sort = Sort.sorts[i]
            }

            override fun onNothingSelected(adapterView: AdapterView<*>) {}
        }
        val podcastListView = activity.findViewById<ListView>(R.id.podcasts)
        val podcastListAdapter = PodcastListAdapter(activity)
        podcastListView.adapter = podcastListAdapter

        val podcastCountView = activity.findViewById<TextView>(R.id.podcastfilter_count)

        viewModel.liveSelectedPodcasts.observe(activity, Observer { podcasts ->
            podcastCountView.text = podcasts.size.toString()
            podcastListAdapter.setSelectedPodcasts(podcasts)
            podcastListAdapter.notifyDataSetChanged()
        })


        podcastListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            val podcast = podcastListAdapter.getItem(position)

            when (podcast.state) {
                Podcast.State.NEW -> viewModel.download(podcast)

                Podcast.State.COMPLETED, Podcast.State.DOWNLOADED -> {
                    val localFile = podcast.localFile!!
                    if (!localFile.exists()) {
                        ToastOnUiThread.show(activity, "Fant ikke fil " + localFile.absolutePath)
                        viewModel.deleteDownloadedFile(podcast)
                    } else {
                        playerUi.play(localFile)
                        viewModel.setPlaying(podcast)
                    }
                }
                else -> {
                }
            }
        }

        podcastListView.onItemLongClickListener = AdapterView.OnItemLongClickListener { _: AdapterView<*>, _: View, position: Int, _: Long ->
            val podcast = podcastListAdapter.getItem(position)

            if (podcast.isPlaying) {
                YesNoCancel.show(activity, "Slette " + podcast.title, "Stopp avspilling først", EMPTY, null, null)
                return@OnItemLongClickListener true
            }

            YesNoCancel.show(activity, "Slette", "Slette ${podcast.title}",
                    { viewModel.delete(podcast) },
                    {
                        YesNoCancel.show(activity, "Slette", "Slette nedlastet fil?", { viewModel.deleteDownloadedFile(podcast) },
                                EMPTY, null)
                    }
                    ,
                    null)
            true
        }
    }

    private fun checkRegex(activity: Activity, regex: String) =
            try {
                Pattern.compile(regex, Pattern.CASE_INSENSITIVE)
                true
            } catch (e: PatternSyntaxException) {
                MessageBox(activity, "Elendig regex", e.message!!).show()
                false
            }

}
