package dag.podkast.repository;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dag.podkast.model.Channel;
import dag.podkast.model.Podcast;
import dag.podkast.util.Logger;

public class AsyncFileSaver {
    private final ExecutorService executorService;
    private AsyncFileSaverListener asyncFileSaverListener;


    public AsyncFileSaver(AsyncFileSaverListener asyncFileSaverListener) {
        this.asyncFileSaverListener = asyncFileSaverListener;
        this.executorService = Executors.newFixedThreadPool(1);
    }

    public void save(FileStorage fileStorage, Collection<Channel> channels, Collection<Podcast> podcasts) {
        FileSaverTask task = new FileSaverTask(fileStorage, channels, podcasts);
        task.executeOnExecutor(executorService);
    }

    private class FileSaverTask extends AsyncTask<Void, Integer, Void> {
        private final FileStorage fileStorage;
        private Collection<Channel> channels;
        private Collection<Podcast> podcasts;
        private long startTime;

        public FileSaverTask(FileStorage fileStorage, Collection<Channel> channels, Collection<Podcast> podcasts) {
            this.fileStorage = fileStorage;
            this.channels = channels;
            this.podcasts = podcasts;
        }

        @Override
        protected Void doInBackground(Void... params) {
            startTime = System.currentTimeMillis();
            try {
                fileStorage.writeChannels(channels);
                asyncFileSaverListener.channelsSaveResult(null);
            } catch (IOException e) {
                Logger.error(e.toString());
                asyncFileSaverListener.channelsSaveResult(e);
            }

            try {
                fileStorage.writePodcasts(podcasts);
                asyncFileSaverListener.podcastsSaveResult(null);
            } catch (IOException e) {
                Logger.error(e.toString());
                asyncFileSaverListener.podcastsSaveResult(e);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void v) {
            long totalTime = System.currentTimeMillis() - startTime;
            Logger.info("Lagret på " + totalTime);
        }
    }


}
