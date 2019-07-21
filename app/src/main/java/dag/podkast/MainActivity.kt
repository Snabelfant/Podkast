package dag.podkast

import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import dag.podkast.audioplayer.AudioPlayerCommands
import dag.podkast.audioplayer.AudioPlayerService
import dag.podkast.listener.ChannelUpdateListener
import dag.podkast.ui.PlayerUi
import dag.podkast.ui.PodcastListUi
import dag.podkast.util.Logger
import dag.podkast.viewmodel.PodcastViewModel

class MainActivity : AppCompatActivity() {
    private var serviceBound = false
    private lateinit var audioPlayerService: AudioPlayerService
    private lateinit var podcastViewModel: PodcastViewModel
    private lateinit var playerUi: PlayerUi

    private val currentPositionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            playerUi.updatePosition(intent.getIntExtra("currentposition", 0), intent.getIntExtra("duration", 0))
        }
    }

    private val playbackCompletedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            playerUi.disable()
            podcastViewModel.setPlayingCompleted()
            Logger.info("Ferdig spilt")
            podcastViewModel.save(this@MainActivity)
        }
    }

    private val playbackStoppedReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            playerUi.disable()
            podcastViewModel.stopPlaying()
            Logger.info("Stoppet")
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as AudioPlayerService.LocalBinder
            audioPlayerService = binder.service
            serviceBound = true
            Logger.info("OnServiceConnected $serviceBound")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Logger.info("OnCreate")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mainactivity)

        this.supportActionBar?.apply {
            setLogo(R.drawable.podkast)
            setDisplayUseLogoEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        AudioPlayerCommands(this).apply {
            registerCurrentPositionReceiver(currentPositionReceiver)
            registerPlaybackCompletedReceiver(playbackCompletedReceiver)
            registerPlaybackStoppedReceiver(playbackStoppedReceiver)
        }

        podcastViewModel = ViewModelProviders.of(this).get(PodcastViewModel::class.java)
        playerUi = PlayerUi(this)
        PodcastListUi.build(this, playerUi, podcastViewModel)
        val playerIntent = Intent(this, AudioPlayerService::class.java)
        val bound = bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        Logger.info("BindService $bound")
    }


    public override fun onStop() {
        podcastViewModel.save(this)
        super.onStop()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.mainactivitymenu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) =
            when (item.itemId) {
                R.id.mainactivitymenu_refresh -> {
                    podcastViewModel.updateAllChannels(ChannelUpdateListener.create(this))
                    true
                }

                R.id.mainactivitymenu_save -> {
                    podcastViewModel.save(this)
                    true
                }

                R.id.mainactivitymenu_channeloverview -> {
                    startActivity(Intent(this, ChannelOverviewActivity::class.java))
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        outState.putBoolean("serviceStatus", serviceBound)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        serviceBound = savedInstanceState.getBoolean("serviceStatus")
    }

    override fun onDestroy() {
        super.onDestroy()
        Logger.info("UnbindService $serviceBound")
        if (serviceBound) {
            unbindService(serviceConnection)
            audioPlayerService.stopSelf()
        }
    }
}
