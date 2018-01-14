package android.rushdroid.model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class PuzzleAdaptor extends ArrayAdapter<PieceData> {
    PuzzleAdaptor(Context context, List<PieceData> itemDatas) {
        super(context, 0, itemDatas);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView =  LayoutInflater.from(getContext()).inflate(R.layout.puzzle_item, parent, false);
        }
        setData((TextView)convertView, getItem(position));
        return convertView;
    }
    void setData(TextView itemView, PieceData itemData) {
        if (itemData.getDone()){
            itemView.setText(itemData.getName()+" : DONE");
        } else {
            itemView.setText(itemData.getName());
        }
    }
}
