package dag.podkast.ui

import android.widget.ListView

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer

import dag.podkast.R
import dag.podkast.model.ChannelOverview
import dag.podkast.viewmodel.ChannelOverviewViewModel

object ChannelOverviewUi {

    fun build(activity: AppCompatActivity, viewModel: ChannelOverviewViewModel) {
        val channelOverviewView = activity.findViewById<ListView>(R.id.channeloverviewactivity_channels)
        val adapter = ChannelOverviewAdapter(activity, CheckboxChangeListener(viewModel), viewModel)
        channelOverviewView.adapter = adapter

        viewModel.liveChannelOverviews.observe(activity, Observer { channelOverviews: List<ChannelOverview> ->
            adapter.setChannelOverviews(channelOverviews)
            adapter.notifyDataSetChanged()
        })
    }

    class CheckboxChangeListener(private val viewModel: ChannelOverviewViewModel) {

        fun onCheckedChanged(channelId: String, isSelected: Boolean) {
            viewModel.setSelected(channelId, isSelected)
        }
    }
}
