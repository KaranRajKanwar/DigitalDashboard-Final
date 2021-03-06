package com.example.digitaldashboard;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class MusicActivity extends Activity implements SeekBar.OnSeekBarChangeListener{
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private List<Song> songs = new ArrayList<>();
    RecyclerView songRecyclerView;
    TextView songCurrentPosition, songTotalDuration,song;
    SeekBar songProgress;
    ImageButton btnPlay, btnNext, btnPrevious;
    MediaPlayer mMediaPlayer = null;
    static int songPosition=0;
    SongAdapter songAdapter;
    Handler mHandler=new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musicplayeractivity);

        songTotalDuration=findViewById(R.id.songTotalDuration);
        songCurrentPosition=findViewById(R.id.songCurrentPosition);
        songProgress=(SeekBar)findViewById(R.id.songProgress);
        btnPlay=(ImageButton)findViewById(R.id.btnPlay);
        btnNext=(ImageButton)findViewById(R.id.btnNext);
        btnPrevious=(ImageButton)findViewById(R.id.btnPrevious);
        song = (TextView) findViewById(R.id.songTitle);


        songProgress.setOnSeekBarChangeListener(this);

        songRecyclerView=(RecyclerView) findViewById(R.id.songRecyclerView);
        songRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        songRecyclerView.addItemDecoration(new DividerItemDecoration(songRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        songRecyclerView.setHasFixedSize(true);

        loadSongs();

        songRecyclerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MusicActivity.this,"Song", Toast.LENGTH_LONG).show();
            }
        });

        //TODO Plays first song in list
        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                playMySong(songs.get(songPosition));
            }
        });

        //TODO goes to next song
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataChanged(songPosition,false);
                songPosition++;
                if(songPosition>=songs.size())
                {
                    songPosition=0;
                }

                if(mMediaPlayer!=null) {
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                playMySong(songs.get((songPosition)));
            }
        });

        //TODO Goes to previous song
        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                notifyDataChanged(songPosition,false);
                songPosition--;
                if(songPosition<=-1)
                {
                    songPosition=songs.size()-1;
                }

                if(mMediaPlayer!=null) {
                    mMediaPlayer.release();
                    mMediaPlayer = null;
                }
                playMySong(songs.get((songPosition)));
            }
        });


    }

    private void notifyDataChanged(int position, boolean playing) {
        Song song=songs.get(position);
        song.setPlaying(playing);
        songs.set(position,song);
        songAdapter.notifyDataSetChanged();
    }

    //TODO Grabs all songs from mobiles device
    private void loadSongs() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else
        {
            fetchAllSongs();
        }
    }

    private void fetchAllSongs() {
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DURATION
        };

        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);


        while(cursor.moveToNext()) {
            songs.add(cursorToSong(cursor));
        }

        songAdapter=new SongAdapter(MusicActivity.this,songs);
        songRecyclerView.setAdapter(songAdapter);
    }

    private Song cursorToSong(Cursor cursor)
    {
        Song song=new Song();
        song.setId(cursor.getString(0));
        song.setArtist(cursor.getString(1));
        song.setTitle(cursor.getString(2));
        song.setData(cursor.getString(3));
        song.setDisplayName(cursor.getString(4));
        song.setDuration(cursor.getString(5));
        song.setPlaying(false);
        return song;
    }

    //TODO Get permission to view all MP3 files on Mobile Phone
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    fetchAllSongs();


                } else {
                    Toast.makeText(MusicActivity.this, getString(R.string.no_permission), Toast.LENGTH_SHORT).show();

                }
                return;

        }
    }

    public void playRandomSong(int position)
    {
        notifyDataChanged(songPosition,false);
        songPosition=position;
        if(mMediaPlayer!=null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
        playMySong(songs.get(songPosition));
    }

    private void playMySong(Song song) {

        notifyDataChanged(songPosition,true);
        if (mMediaPlayer == null)
        {

            mMediaPlayer=MediaPlayer.create(getApplicationContext(), Uri.parse(song.getData()));
            mMediaPlayer.start();
            mMediaPlayer.setLooping(true);
            btnPlay.setImageResource(android.R.drawable.ic_media_pause);
            songTotalDuration.setText(milliSecondsToTimer(song.getDuration()));
            updateSongProgress();

        }else
        {
            if(mMediaPlayer.isPlaying())
            {
                notifyDataChanged(songPosition,false);
                mMediaPlayer.pause();
                btnPlay.setImageResource(android.R.drawable.ic_media_play);
            }else
            {
                notifyDataChanged(songPosition,true);
                mMediaPlayer.start();
                btnPlay.setImageResource(android.R.drawable.ic_media_pause);
            }

        }
    }

    private void updateSongProgress()
    {
        mHandler.postDelayed(runnable,1000);
    }

    Runnable runnable=new Runnable() {
        @Override
        public void run() {

            int currentDuration=mMediaPlayer.getCurrentPosition();
            int totalDuration=mMediaPlayer.getDuration();

            songCurrentPosition.setText(milliSecondsToTimer(String.valueOf(currentDuration)));

            songProgress.setProgress(getSongProgress(totalDuration,currentDuration));

            mHandler.postDelayed(this,1000);
        }
    };

    private int getSongProgress(int totalDuration, int currentDuration) {
        return (int)(currentDuration*100)/totalDuration;
    }

    private String milliSecondsToTimer(String songDuration) {
        int duration=Integer.parseInt(songDuration);
        int hour=(int)(duration/(1000*60*60));
        int minute=(int)((duration%(1000*60*60))/(1000*60));
        int seconds=(int)(((duration%(1000*60*60))%(1000*60))/(1000));
        String finalString="";
        if(hour<10)
            finalString+="0";
        finalString+=hour+":";
        if(minute<10)
            finalString+="0";
        finalString+=minute+":";
        if(seconds<10)
            finalString+="0";
        finalString+=seconds;

        return finalString;
    }

    //TODO allows you to seek forward or backwards on the seekbar
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if(b) {
            mMediaPlayer.seekTo(getTimeFromProgress(seekBar.getProgress(), mMediaPlayer.getDuration()));
        }
    }

    //TODO gets time for song duration in 00:00:00
    private int getTimeFromProgress(int progress, int duration) {
        int songDuration=(int)((duration*progress)/100);
        return songDuration;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onBackPressed()
    {
        mHandler.removeCallbacks(runnable);
        if (mMediaPlayer!= null)
        {
            if(mMediaPlayer.isPlaying()){
                mMediaPlayer.stop();
            }
            mMediaPlayer.release();
        }

        super.onBackPressed();
    }
}