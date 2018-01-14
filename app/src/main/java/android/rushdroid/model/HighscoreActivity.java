package android.rushdroid.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class HighscoreActivity extends Activity{
    TheApplication app;
    Activity act;
    int level;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);
        app=(TheApplication)this.getApplication();
        Intent i = getIntent();
        level = i.getIntExtra("level",0);
        app.createTableByLevel(level, this);
        act = this;
    }


    public void onClickBack(View button) {
        finish();
    }

    //Supprimer les highscores
    public void onClickReset(View button) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder
                .setTitle("Clear highscores list?")
                .setMessage("Are you sure?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        app.clearXml(act);
                        app.writeXml();
                        app.inputSomePieceData(act);
                        Toast.makeText(HighscoreActivity.this, "The list has been cleared",
                                Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(getIntent());
                    }
                })
                .setNegativeButton("No", null)
                .show();

    }
}

