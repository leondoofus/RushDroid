package android.rushdroid.model;

public class Position implements IPosition {
    private int ncol, nlig;
    
    public Position (int col, int lig){
        ncol = col;
        nlig = lig;
    }
    
    public int getCol(){ return ncol; }
    public int getLig(){ return nlig; }
    public Position addCol(int d) { return new Position(getCol()+d, getLig()); }
    public Position addLig(int d) { return new Position(getCol(), getLig()+d); }

}
