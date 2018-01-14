package android.rushdroid.model;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class ChooseActivity extends Activity{
    TheApplication app;
    ListView puzzleListView;
    List<PieceData> itemDatas;
    boolean cg;

    //Dirige vers HighscoreActivity ou GameActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        app = (TheApplication)this.getApplication();
        Intent i = getIntent();
        cg = i.getBooleanExtra("boolean", true);
        if(!cg) {
            TextView textElement = (TextView) (this).findViewById(R.id.choose);
            textElement.setText("High scores");
        }
        itemDatas = app.getPuzzleList();
        puzzleListView = (ListView)findViewById(R.id.pieceList);
        puzzleListView.setAdapter(new PuzzleAdaptor(this, itemDatas));
        PuzzleListListener listener = new PuzzleListListener(app,this,cg);
        puzzleListView.setOnItemClickListener(listener);
    }

    public void onClickBack(View view) {
        finish();
    }

    //Attendre le resultat de la HighcoreActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (requestCode == 0){
                itemDatas = app.getPuzzleList();
                puzzleListView = (ListView)findViewById(R.id.pieceList);
                puzzleListView.setAdapter(new PuzzleAdaptor(this, itemDatas));
                PuzzleListListener listener = new PuzzleListListener(app,this,cg);
                puzzleListView.setOnItemClickListener(listener);
            }
        }
    }
}