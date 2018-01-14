package android.rushdroid.model;

public class Highscore {
    private String name;
    private int moves;
    private long time;
    private int level;
    public Highscore (int level, String name, int moves, long time){
        this.name=name;
        this.moves=moves;
        this.time=time;
        this.level=level;
    }

    public String getName(){
        return name;
    }

    public int getMoves(){
        return moves;
    }

    public long getTime(){
        return time;
    }

    public int getLevel(){
        return level;
    }

}
