package android.rushdroid.model;

public class Piece implements IPiece{
    private int id, size, ncol, nlig;
    private Direction dir;
    
    public Piece (int id, int size, Direction dir, int ncol, int nlig){
        this.id = id;
        this.size = size;
        this.dir = dir;
        this.ncol = ncol;
        this.nlig = nlig;
    }
    
    public int getId() { return id; }
    public Direction getOrientation() { return dir; }
    public int getSize() {return size; }
    public Position getPos() { return new Position(ncol, nlig); }
    public void setPos(Position pos){
        ncol = pos.getCol();
        nlig = pos.getLig();
    }
}
