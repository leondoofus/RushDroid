package android.rushdroid.model;

public class Grid implements IGrid{
    private Integer[][] grid; 
    
    public Grid(){
        grid = new Integer[6][6];
        for (int i = 0; i < 6; i++)
            for (int j = 0; j < 6; j++)
                grid[i][j]=null;
    }
    public boolean isEmpty(Position pos) {
        if(pos.getLig()>=6 || pos.getLig()<0 || pos.getCol()>=6 || pos.getCol()<0){
            return false;
        }
        return (grid[pos.getLig()][pos.getCol()] == null);
    }
    
    public Integer get(Position pos){
        if(!isEmpty(pos))
            return grid[pos.getLig()][pos.getCol()];
        else
            return null;
        
    }
    public void set(Position pos, int id){
        if(pos.getLig()<6 && pos.getLig()>=0 && pos.getCol()<6 || pos.getCol()>=0){
            grid[pos.getLig()][pos.getCol()]=(Integer)id;
        }
    }
    public void unset(Position pos){
        grid[pos.getLig()][pos.getCol()]=null;
    }

    
}
