package android.rushdroid.model;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    /*Cette activite est l'ecran principal et permet d'acceder aux autres activites
    Elle lit aussi le fichier contenant la musique et la joue s'il existe*/
    TheApplication app;
    public static MediaPlayer player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = MediaPlayer.create(this, R.raw.unstoppable);
        player.setLooping(true);
        app=(TheApplication)this.getApplication();
        app.setOptions(this);
        if (app.readSound(this)) {
            player.start();
        }
        app.inputSomePieceData(this);
        app.readXml();
    }

    public void onClickExit(View view) {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
        finish();
    }

    public void onClickStart(View button) throws NotFound {
        app = (TheApplication)this.getApplication();
        app.setGame(0);
        Intent ChooseIntent = new Intent(this, GameActivity.class);
        startActivity(ChooseIntent);
    }

    public void onClickChoose(View button){
        Intent ChooseIntent = new Intent(this, ChooseActivity.class);
        boolean cg =true;
        ChooseIntent.putExtra("boolean",cg);
        startActivity(ChooseIntent);
    }

    public void onClickHighscore (View button){
        Intent ChooseIntent = new Intent(this, ChooseActivity.class);
        boolean cg =false;
        ChooseIntent.putExtra("boolean",cg);
        startActivity(ChooseIntent);
    }

    public void onClickOption (View button) throws NotFound{
        app=(TheApplication)this.getApplication();
        app.setGame(0);
        Intent ChooseIntent = new Intent(this, OptionActivity.class);
        startActivity(ChooseIntent);
    }
}
