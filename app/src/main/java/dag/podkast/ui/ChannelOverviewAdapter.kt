package dag.podkast.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.TextView
import dag.podkast.R
import dag.podkast.model.ChannelOverview
import dag.podkast.util.DateUtil
import dag.podkast.util.EMPTY
import dag.podkast.util.YesNoCancel
import dag.podkast.viewmodel.ChannelOverviewViewModel
import java.util.*

class ChannelOverviewAdapter(context: Context, private val checkboxChangeListener: ChannelOverviewUi.CheckboxChangeListener, private val viewModel: ChannelOverviewViewModel) : ArrayAdapter<ChannelOverview>(context, R.layout.podcast) {
    private val inflater: LayoutInflater = super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    private var channelOverviews: List<ChannelOverview> = ArrayList()

    override fun getCount(): Int {
        return channelOverviews.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val channelOverview = getItem(position)!!
        val view = inflater.inflate(R.layout.channeloverview, parent, false)
        val checkBox = view.findViewById<CheckBox>(R.id.channel_isselected)
        val nameView = view.findViewById<TextView>(R.id.channel_name)
        val remainingCountView = view.findViewById<TextView>(R.id.channel_remainingcount)
        val remainingTimeView = view.findViewById<TextView>(R.id.channel_remainingtime)

        checkBox.isChecked = channelOverview.isSelected
        checkBox.setOnCheckedChangeListener { _, b -> checkboxChangeListener.onCheckedChanged(channelOverview.channelId, b) }

        nameView.text = channelOverview.channelName
        nameView.setOnClickListener {
            YesNoCancel.show(context, "", "Kun velge ${channelOverview.channelName}?",
                    { viewModel.selectOnly(channelOverview.channelId) }, EMPTY, EMPTY)
        }

        nameView.setOnLongClickListener {
            YesNoCancel.show(context, "", "Slette ${channelOverview.channelName}?",
                    { viewModel.deleteChannel(channelOverview.channelId) }, EMPTY, EMPTY)
            true
        }

        remainingCountView.text = channelOverview.remainingPodcasts.toString()
        remainingTimeView.text = DateUtil.toHhMmSs(channelOverview.remainingSeconds)
        return view
    }

    internal fun setChannelOverviews(channelOverviews: List<ChannelOverview>) {
        this.channelOverviews = channelOverviews
    }

    override fun getItem(position: Int): ChannelOverview? {
        return channelOverviews[position]
    }
}
