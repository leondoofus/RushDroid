package android.rushdroid.model;

public class PieceData {
    private String name;
    private boolean done;
    private int id;
    PieceData(String name, int id, boolean done){
        this.name = name;
        this.id = id;
        this.done=done;
    }

    int getId(){
        return id;
    }

    boolean getDone(){
        return done;
    }

    void setDone(boolean b){
        done=b;
    }

    String getName(){
        return name;
    }
}
