package android.rushdroid.model;

public interface IModel {
    public Integer getIdByPos(Position pos);
    public Direction getOrientation(int id) throws IdException;
    public Integer getLig(int id);
    public Integer getCol(int id);
    public Integer getSize(int id);
    public boolean endOfGame();
    public boolean moveForward(int id);
    public boolean moveBackward(int id);
}
