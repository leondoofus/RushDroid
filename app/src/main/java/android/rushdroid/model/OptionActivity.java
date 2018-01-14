package android.rushdroid.model;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

public class OptionActivity extends Activity{
    private TheApplication app;
    private int length=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app=(TheApplication)this.getApplication();
        setContentView(R.layout.activity_option);
        Switch check = (Switch)(this).findViewById(R.id.checkBox);
        if (app.readSound(this))
            check.setChecked(true);
        else
            check.setChecked(false);
    }

    //Mettre la musique en arriere plan
    public void onCheckBox (View checkbox){
        if (((Switch)checkbox).isChecked()) {
            app.writeSound(true, this);
            onClickPlay(checkbox);
        } else {
            app.writeSound(false, this);
            onClickStop(checkbox);
        }
    }

    public void onClickPlay(View button){
        if(MainActivity.player == null) {
            MainActivity.player = MediaPlayer.create(this, R.raw.unstoppable);
            MainActivity.player.setLooping(true);
            MainActivity.player.start();
        } else {
            MainActivity.player.stop();
            MainActivity.player.release();
            MainActivity.player = null;
            MainActivity.player = MediaPlayer.create(this, R.raw.unstoppable);
            MainActivity.player.setLooping(true);
            MainActivity.player.start();
        }
    }

    public void onClickPause(View button){
        if(MainActivity.player != null){
            if (MainActivity.player.isPlaying()) {
                MainActivity.player.pause();
                length = MainActivity.player.getCurrentPosition();
            } else {
                MainActivity.player.seekTo(length);
                MainActivity.player.start();
            }
        }
    }

    public void onClickStop (View button) {
        if (MainActivity.player != null) {
            MainActivity.player.stop();
        }
    }

    public void onClickBack(View button) {
        finish();
    }


    //Choisir parmi les styles
    public void onClickStyle1(View button){ app.writeStyle(1, this); }

    public void  onClickStyle2(View button){ app.writeStyle(2, this); }

    public void  onClickStyle3(View button){ app.writeStyle(3, this); }


}
