package android.rushdroid.model;
import android.graphics.Canvas;
import android.view.SurfaceHolder;

class GameViewThread extends Thread {
    SurfaceHolder holder;
    GameView view;
    boolean running = false;
    Canvas can;

    public GameViewThread(SurfaceHolder holder, GameView view) {
        this.holder = holder;
        this.view = view;
    }

    public void setRunning(boolean b) {
        running = b;
        if (b == false){
            can = holder.lockCanvas();
        }
    }

    @Override
    public void run() {
        while (this.running) {
            can = holder.lockCanvas();
            if (can != null) {
                this.view.onDraw(can);
                this.holder.unlockCanvasAndPost(can);
            }
        }
    }

    public Canvas getCanvas() { return  can; }

    /*@Override
    public void run() {
        super.run();
        Canvas canvas;
        while (running) {
            canvas=null;
            try {
                canvas = holder.lockCanvas(null);
                synchronized (holder) {
                    view.onDraw(canvas);
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
        }
    }*/

}