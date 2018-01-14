package android.rushdroid.model;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;

public class PuzzleListListener implements AdapterView.OnItemClickListener {
    TheApplication app;
    ChooseActivity act;
    Context context;
    boolean cg; //true for choosegame, false for highscrores
    PuzzleListListener(TheApplication app, ChooseActivity act, boolean cg) {
        super(); this.app = app;
        this.act = act;
        context = act;
        this.cg=cg;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        try {
            app.setGame(position);
        } catch (NotFound notFound) {
            notFound.printStackTrace();
        }
        if(cg) {
            Intent gameIntent = new Intent(context, GameActivity.class);
            act.startActivityForResult(gameIntent, 1001);
        }
        else{
            Intent ChooseIntent = new Intent(context, HighscoreActivity.class);
            ChooseIntent.putExtra("level", position);
            act.startActivityForResult(ChooseIntent,position);
        }
    }
}