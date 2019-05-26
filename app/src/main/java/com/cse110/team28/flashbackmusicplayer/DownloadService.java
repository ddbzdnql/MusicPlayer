package com.cse110.team28.flashbackmusicplayer;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;

import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;

/**
 * Created by adityamullick on 3/10/18.
 */

public class DownloadService {

    // functionalities for downloading remote tracks are handled here

    // if there is no connection, pause the download
    public static boolean downloadState = true;
    public static boolean isPaused;
    public static Queue<Uri> downloadQueue = new ArrayDeque<>();
    public static DownloadManager manager;
    public static boolean setup = false;
    public static Queue<Long> idQueue = new ArrayDeque<>();
    public static boolean lost = false;
    public static boolean src;
    public static Context ctxt;

    static class ConnectionCallback extends ConnectivityManager.NetworkCallback{
        public void onAvailable(Network nt){
            if (lost){
                Queue<Uri> cacheDownload = downloadQueue;
                downloadQueue = new ArrayDeque<>();
                for (Uri uri : cacheDownload){
                    download(uri, src);
                }
                lost = false;
                System.out.println("Reconnected.");
            }
        }

        public void onLost(Network nt){
            lost = true;
            while (!idQueue.isEmpty()){
                Long id = idQueue.poll();
                manager.remove(id);
            }
            System.out.println("Lost");
        }
    }

    public static void setup(Context context){
        ctxt = context;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        cm.registerDefaultNetworkCallback(new ConnectionCallback());

        manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        BroadcastReceiver onComplete=new BroadcastReceiver() {
            public void onReceive(Context ctxt, Intent intent) {
                if (!lost){
                    idQueue.poll();
                    Uri uri = downloadQueue.poll();
                    Library.addDownload(ctxt, uri, src);
                }
            }
        };
        context.registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        setup = true;
    }

    public static void download(Uri uri, boolean outside){
        if (setup){
            src = outside;
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setDestinationInExternalFilesDir(ctxt,
                    Environment.DIRECTORY_DOWNLOADS, "vibe/temp");
            Long id = manager.enqueue(request);
            Log.d("download url: ", uri.toString());
            idQueue.add(id);
            downloadQueue.add(uri);
        }
        else{
            System.out.println("Download service not initialized.");
        }
    }


    //TODO: implement this method for getting track upon download
    public static Track getRecentlyDownloadedTrack() {
        return null;
    }





}
