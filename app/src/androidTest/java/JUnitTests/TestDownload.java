package JUnitTests;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Environment;
import android.support.test.InstrumentationRegistry;
import android.test.mock.MockContext;
import android.util.Log;

import com.cse110.team28.flashbackmusicplayer.DownloadService;
import com.cse110.team28.flashbackmusicplayer.MediaViewActivity;

import org.junit.Test;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import static junit.framework.Assert.assertEquals;


/**
 * Created by adityamullick on 3/16/18.
 */

public class TestDownload {

    private Uri mockURI = Uri.parse("https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3");
    private ArrayList<Uri> mockURIs;
    public static DownloadManager manager;
    public static Context mockContext = InstrumentationRegistry.getTargetContext();

    public void prepare() {
        DownloadService.setup = true;
        DownloadService.ctxt = mockContext;
        DownloadService.manager = (DownloadManager)DownloadService.ctxt.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    @Test
    public void testDownload() {
        prepare();
        DownloadService.download(mockURI, true);

        // pull the contents of Environment.Downloads and make sure the file contents are there
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Log.d("testDownload: ", dir.listFiles().length + "");

        // there is a directory
        assert dir != null;


    }


}
