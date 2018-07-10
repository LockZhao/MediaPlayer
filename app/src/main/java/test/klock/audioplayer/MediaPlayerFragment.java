package test.klock.audioplayer;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by ZhaoRongZhi on 2018-02-02
 *
 * @descr
 */

public class MediaPlayerFragment extends Fragment {

    private static final int UPDATE_DURATION = 500;
    private static final int SEEK_BAR_MAX    = 100;

    @InjectView(R.id.sb_progress)
    SeekBar sbProgress;
    @InjectView(R.id.btn_play)
    Button  btnPlay;
    @InjectView(R.id.btn_pause)
    Button  btnPause;
    @InjectView(R.id.btn_replay)
    Button  btnReplay;
    @InjectView(R.id.btn_stop)
    Button  btnStop;
    @InjectView(R.id.sb_volume)
    SeekBar sbVolume;

    private MediaPlayer mediaPlayer;
    private Handler           handler  = new Handler();
    private Runnable          runnable = new Runnable() {
        @Override
        public void run () {
            if (sbProgress != null && mediaPlayer != null && mediaPlayer.isPlaying()) {
                sbProgress.setProgress(mediaPlayer.getCurrentPosition() * SEEK_BAR_MAX / mediaPlayer.getDuration());
            }
            handler.postDelayed(runnable, UPDATE_DURATION);
        }
    };
    private ArrayList<String> pathList = new ArrayList<String>() {{
        add("https://raw.githubusercontent.com/LockZhao/MediaPlayer/master/testMp3.mp3");
//        add("http://abv.cn/music/%e5%8d%83%e5%8d%83%e9%98%99%e6%ad%8c.mp3");
//        add("http://abv.cn/music/%e7%ba%a2%e8%b1%86.mp3");
    }};

    @Nullable
    @Override
    public View onCreateView (LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_media_player, null);
        ButterKnife.inject(this, view);
        initAudio();
        initSeekbars();
        handler.postDelayed(runnable, UPDATE_DURATION);
        return view;
    }

    private void initSeekbars () {
        sbProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && mediaPlayer != null) {
                    mediaPlayer.seekTo(mediaPlayer.getDuration() * progress / SEEK_BAR_MAX);
                }
            }

            @Override
            public void onStartTrackingTouch (SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch (SeekBar seekBar) {

            }
        });

        sbVolume.setProgress(SEEK_BAR_MAX);
        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null) {
                    float volume = progress / (float) MediaPlayerFragment.SEEK_BAR_MAX;
                    mediaPlayer.setVolume(volume, volume);
                }
            }

            @Override
            public void onStartTrackingTouch (SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch (SeekBar seekBar) {

            }
        });
    }

    private void initAudio () {
        btnPlay.setEnabled(false);
        btnPause.setEnabled(false);
        btnReplay.setEnabled(false);
        btnStop.setEnabled(false);
        sbProgress.setEnabled(false);

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(pathList.get((int) (System.currentTimeMillis() % pathList.size())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.prepareAsync();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared (MediaPlayer mp) {
                btnPlay.setEnabled(true);
                sbProgress.setEnabled(true);
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion (MediaPlayer mp) {
                btnPlay.setEnabled(true);
                btnPause.setEnabled(false);
                btnReplay.setEnabled(false);
                btnStop.setEnabled(false);
                sbProgress.setProgress(0);
            }
        });
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError (MediaPlayer mp, int what, int extra) {
                Toast.makeText(getActivity(), " 播放失败！", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate (MediaPlayer mp, int percent) {
                sbProgress.setSecondaryProgress(percent);
            }
        });
    }

    @OnClick(R.id.btn_play)
    public void onPlayClicked () {
        mediaPlayer.start();
        btnPlay.setEnabled(false);
        btnPause.setEnabled(true);
        btnReplay.setEnabled(true);
        btnStop.setEnabled(true);
    }

    @OnClick(R.id.btn_pause)
    public void onPauseClicked () {
        mediaPlayer.pause();
        btnPlay.setEnabled(true);
        btnPause.setEnabled(false);
        btnReplay.setEnabled(true);
        btnStop.setEnabled(true);
    }

    @OnClick(R.id.btn_replay)
    public void onReplayClicked () {
        mediaPlayer.seekTo(0);
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        btnPlay.setEnabled(false);
        btnPause.setEnabled(true);
        btnReplay.setEnabled(true);
        btnStop.setEnabled(true);
        sbProgress.setProgress(0);
    }

    @OnClick(R.id.btn_stop)
    public void onStopClicked () {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        initAudio();
        sbProgress.setProgress(0);
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();
        ButterKnife.reset(this);
        handler.removeCallbacksAndMessages(null);
    }
}
