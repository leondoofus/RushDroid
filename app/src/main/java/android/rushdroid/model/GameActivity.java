package android.rushdroid.model;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

public class GameActivity extends Activity {
    TheApplication app;
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app=(TheApplication)this.getApplication();
        setContentView(R.layout.activity_game);
    }

    /*Si on veut retourner apres avoir gagne, on enregistre le nom pour faire un highscore
    * si la partie est en cours on retourne au mainactivity simplement*/
    public void onClickBack(View view) throws NotFound{
        if (app.getGame().endOfGame()) {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.prompts, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);

            // Construire le dialogue
            alertDialogBuilder.setCancelable(false).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    // lire l'entree
                    app.insertHs(userInput.getText().toString());
                    app.writeXml();
                    finish();
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog,int id) {
                    dialog.cancel();
                    finish();
                }
            });

            // creation du dialogue d'alerte
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }else {
            finish();
        }
    }

    //On charge le puzzle suivant, on sauve le nom du joueur si la partie a ete gagnee
    public void onClickNext(View view) throws NotFound{
        //s'il n'y a pas de nom enregistre , on recupere le nom si la partie a ete gagne
        if (app.getName() == "") {
            LayoutInflater li = LayoutInflater.from(context);
            View promptsView = li.inflate(R.layout.prompts, null);

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            alertDialogBuilder.setView(promptsView);

            final EditText userInput = (EditText) promptsView.findViewById(R.id.editTextDialogUserInput);
            alertDialogBuilder.setCancelable(false).setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    app.insertHs(userInput.getText().toString());
                    app.setName(userInput.getText().toString());
                    app.writeXml();
                    try {
                        if(app.getNbPuzzlesApp() > (app.getCurrentId()+1)) {
                            app.setGame(app.getCurrentId() + 1);
                        }else{
                            Toast.makeText(GameActivity.this, "You reached the end!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (NotFound notFound) {
                        notFound.printStackTrace();
                    }
                }
            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.cancel();
                    try {
                        if(app.getNbPuzzlesApp() > (app.getCurrentId()+1)) {
                            app.setGame(app.getCurrentId() + 1);
                        }else{
                            Toast.makeText(GameActivity.this, "You reached the end!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    } catch (NotFound notFound) {
                        notFound.printStackTrace();
                    }
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.create();
            //si c'est la fin du jeu on affiche le dialogue
            if (app.getGame().endOfGame()) {
                alertDialog.show();
                alertDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            } else {
                // sinon on passe au puzzle suivant sans  enregistrer de nom
                if(app.getNbPuzzlesApp() > (app.getCurrentId()+1)) {
                    app.setGame(app.getCurrentId() + 1);
                }else{ // si c'est le dernier puzzle on retourne a l'activite precedente
                    Toast.makeText(GameActivity.this, "You reached the end!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }else{ //si un nom est enregistre
            if (app.getGame().endOfGame()){
                app.insertHs(app.getName()); //on enregistre le nom et le highscore
                app.writeXml();
            }
            if(app.getNbPuzzlesApp() > (app.getCurrentId()+1)) {
                app.setGame(app.getCurrentId() + 1);
            }else{
                Toast.makeText(GameActivity.this, "You reached the end!",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }


    //On redemarre la partie
    public void onClickReset(View view) throws NotFound {
        app.resetGame();
    }
}
