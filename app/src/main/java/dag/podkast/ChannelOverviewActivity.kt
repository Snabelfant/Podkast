package dag.podkast

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders

import dag.podkast.ui.ChannelEditorUi
import dag.podkast.ui.ChannelOverviewUi
import dag.podkast.util.Logger
import dag.podkast.viewmodel.ChannelOverviewViewModel

class ChannelOverviewActivity : AppCompatActivity() {
    private lateinit var channelOverviewViewModel: ChannelOverviewViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.info("OnCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channeloverviewactivity)

        this.supportActionBar?.apply {
            setLogo(R.drawable.podkast)
            setDisplayUseLogoEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        channelOverviewViewModel = ViewModelProviders.of(this).get(ChannelOverviewViewModel::class.java)
        ChannelOverviewUi.build(this, channelOverviewViewModel)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.channeloverviewmenu, menu)
        return true
    }

    public override fun onPause() {
        channelOverviewViewModel.save(this)
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.channeloverviewmenu_refresh -> {
                    channelOverviewViewModel.updateAllChannels(this)
                    true
                }

                R.id.channeloverviewmenu_save -> {
                    channelOverviewViewModel.save(this)
                    true
                }

                R.id.channeloverviewmenu_newchannel -> {
                    ChannelEditorUi(channelOverviewViewModel).show(supportFragmentManager, "")
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }
}
