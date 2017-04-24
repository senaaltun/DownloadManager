package download.example.com.internaldownloadmanager;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.novoda.downloadmanager.DownloadManagerBuilder;
import com.novoda.downloadmanager.lib.DownloadManager;
import com.novoda.downloadmanager.lib.Query;
import com.novoda.downloadmanager.lib.Request;
import com.novoda.downloadmanager.notifications.NotificationVisibility;
import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements QueryForDownloadsAsyncTask.Callback {

  private static final String BIG_FILE = "http://heybook.online/audio/engeregin-gozu.mp3";
  private static final String PENGUINS_IMAGE = "http://i.imgur.com/Y7pMO5Kb.jpg";

  private DownloadManager downloadManager;
  private RecyclerView recyclerView;
  private View emptyView;
  private long downloadReference;
  private MediaPlayer mpintro;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    emptyView = findViewById(R.id.main_no_downloads_view);
    recyclerView = (RecyclerView) findViewById(R.id.main_downloads_list);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    downloadManager = DownloadManagerBuilder.from(this)
        .build();

    setupDownloadingExample();
    setupQueryingExample();


    findViewById(R.id.reference).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        Log.d("ReferenceIdP",String.valueOf(downloadManager.getUriForDownloadedFile(downloadReference)));
      }
    });

    findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
        MediaPlayer mPlayer = new MediaPlayer();
        Uri myUri = Uri.parse("file:///data/user/0/download.example.com.internaldownloadmanager/files/Movies/heybook-1");
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
          mPlayer.setDataSource(getApplicationContext(), myUri);
          mPlayer.prepare();
          mPlayer.start();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    });

  }

  private void setupDownloadingExample() {
    Uri uri = Uri.parse(BIG_FILE);
    final Request request = new Request(uri)
        .setDestinationInInternalFilesDir(Environment.DIRECTORY_MOVIES, "heybook")
        .setNotificationVisibility(NotificationVisibility.ACTIVE_OR_COMPLETE)
        .setTitle("Family of Penguins")
        .setDescription("These are not the beards you're looking for")
        .setBigPictureUrl(PENGUINS_IMAGE);

    findViewById(R.id.main_download_button).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(@NonNull View v) {
            downloadManager.enqueue(request);
            downloadReference = downloadManager.enqueue(request);
            Log.d("downloadReference",String.valueOf(downloadReference));

          }
        });
  }

  private void setupQueryingExample() {
    queryForDownloads();
    findViewById(R.id.main_refresh_button).setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(@NonNull View v) {
            queryForDownloads();
          }
        }
    );
  }

  private void queryForDownloads() {
    QueryForDownloadsAsyncTask.newInstance(downloadManager, MainActivity.this).execute(new Query());
  }

  @Override
  public void onQueryResult(List<BeardDownload> beardDownloads) {
    recyclerView.setAdapter(new BeardDownloadAdapter(beardDownloads));
    emptyView.setVisibility(beardDownloads.isEmpty() ? View.VISIBLE : View.GONE);
  }
}