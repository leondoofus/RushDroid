package android.rushdroid.model;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class GameView extends SurfaceView implements SurfaceHolder.Callback{
    private TheApplication app; //L'application du jeu
    private GameViewThread th; //Thread pour dessiner sur SurfaceView
    private Model m; //Model courant

    private boolean once = true; //Condition de control qui permet d'ecrire le score qu'une fois

    private int x=0,y=0; //Coordonnes debut quand on touche l'ecran
    private int x2=0,y2=0; //Coodonnees de movement du doigt
    boolean move; //Condition pour augmenter le nombre de coups
    private Position posMove; //La postion originale de la piece touchee
    private int idmove; //Id de la piece touchee

    private int stylegm =1; //Style des pieces

    //Resolution d'ecran
    private final int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private final int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    //Taille par defaut de la SurfaceView, d'un carre et la marge (valeurs pour OptionActivity)
    private int surfaceW = dpToPx(300);
    private int carreW = dpToPx(50);
    private int decal = dpToPx(10);

    //Pourcentage de l'ecran utilise par la surfaceview
    private double pourcent=0.85;
    private double decal_pourcent=0.02;

    //Taille et coordonnees du texte affiche quand le joueur gagne
    private int textX= dpToPx(75) + decal;
    private int textY= dpToPx(175) + decal;
    private int textSize= dpToPx(75);

    //Images par defaut pour les pieces et le fond
    private Bitmap background = BitmapFactory.decodeResource(getResources(), R.drawable.design);
    private Bitmap road = BitmapFactory.decodeResource(getResources(), R.drawable.road);
    private Bitmap goal = BitmapFactory.decodeResource(getResources(), R.drawable.style1goal);
    private Bitmap hor2 = BitmapFactory.decodeResource(getResources(), R.drawable.style1hor2);
    private Bitmap hor3 = BitmapFactory.decodeResource(getResources(), R.drawable.style1hor3);
    private Bitmap ver2 = BitmapFactory.decodeResource(getResources(), R.drawable.style1ver2);
    private Bitmap ver3 = BitmapFactory.decodeResource(getResources(), R.drawable.style1ver3);
    private Bitmap bordery = BitmapFactory.decodeResource(getResources(), R.drawable.bordery);
    private Bitmap borderx = BitmapFactory.decodeResource(getResources(), R.drawable.borderx);

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getHolder().addCallback(this);
        app= ((TheApplication)(context.getApplicationContext()));
        m=((TheApplication)(context.getApplicationContext())).getGame();
        if (getContext() instanceof GameActivity) setValues();
        //Resize images
        background = Bitmap.createScaledBitmap(background, surfaceW + 2 * decal, surfaceW + 2 * decal, true);
        road = Bitmap.createScaledBitmap(road, carreW * 6 + 2*decal, carreW, true);
        goal = Bitmap.createScaledBitmap(goal, carreW * 2, carreW, true);
        hor2 = Bitmap.createScaledBitmap(hor2, carreW * 2, carreW, true);
        ver2 = Bitmap.createScaledBitmap(ver2, carreW, carreW * 2, true);
        hor3 = Bitmap.createScaledBitmap(hor3, carreW * 3, carreW, true);
        ver3 = Bitmap.createScaledBitmap(ver3, carreW, carreW * 3, true);
        bordery = Bitmap.createScaledBitmap(bordery, decal, surfaceW+decal, true);
        borderx = Bitmap.createScaledBitmap(bordery, surfaceW+2*decal, decal, true);
    }

    //Commencer a dessiner les pieces
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (getContext() instanceof GameActivity){ //Si on est dans GameActivity, resize la taille d'ecran
            android.view.ViewGroup.LayoutParams lp = this.getLayoutParams();
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                lp.height = (int)(screenHeight*pourcent);
                lp.width = lp.height;
            } else {
                lp.width = (int)(screenWidth*pourcent);
                lp.height = lp.width;
            }
            this.setLayoutParams(lp);
            th = new GameViewThread(getHolder(), this);
            th.setRunning(true);
            th.start();
            app.setActivity((GameActivity) (this.getContext()));
        } else {
            th = new GameViewThread(getHolder(), this);
            th.setRunning(true);
            th.start();
            app.setActivity((OptionActivity) (this.getContext()));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if (m.endOfGame()) return false;
        switch (event.getAction()) {
            //On recupere les coordonnees quand on touche
            case MotionEvent. ACTION_DOWN: {
                x=(int)event.getX();
                y=(int)event.getY();
                //Condition pour que x,y soient dans la grille, 5 à cause de l'imprecision du a la conversion de double en int
                if (x < decal || y < decal || x > (surfaceW+decal-5) || y > (surfaceW+decal-5)) return false;
                posMove = getPosOnSurface(x, y);
                if (m.getIdByPos(posMove) == null) return false;
                idmove=m.getIdByPos(posMove);
                break;
            }
            //Deplacer les pieces quand on bouge
            case MotionEvent. ACTION_MOVE: {
                x2=(int)event.getX();
                y2=(int)event.getY();
                try {
                    deplacerSurSurface();
                } catch (IdException e) {
                    e.printStackTrace();
                }
                break;
            }
            //Incrementer le nombre de coups
            case MotionEvent. ACTION_UP: {
                if (move){
                    move = false;
                    if (getContext() instanceof  GameActivity) //avoid error of setText
                        app.incrementMoves();
                }
                break;
            }
            default:
                break;
        }
        return true;
    }

    //Dessiner Surface
    @Override
    public void onDraw(final Canvas c){
        if (c == null)
            return ;
        super.onDraw(c);
        //on dessinne le fond
        c.drawBitmap(background, 0, 0, null);
        c.drawBitmap(bordery, 0, 0, null);
        c.drawBitmap(bordery, surfaceW + decal, 0, null);
        c.drawBitmap(borderx, 0, 0, null);
        c.drawBitmap(borderx, 0, surfaceW+decal, null);
        c.drawBitmap(road, decal, carreW*2+decal, null);

        if (app.getStyle() != stylegm){
            setCars();
        }

        //on dessine la quadrilature
        final Paint paint= new Paint();
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAlpha(100);

        for (int i = carreW; i<surfaceW; i+=carreW){
            c.drawLine(0+decal,i+decal, surfaceW+decal, i+decal, paint);
            c.drawLine(i+decal,0+decal, i+decal, surfaceW+decal, paint);
        }

        //On incremente le temps
        if (getContext() instanceof GameActivity){ //avoid error of setText
        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!m.endOfGame()) app.setTime();
            }
        });}
        try {
            if(m.endOfGame() == false) {
                dessinerPuzzle(c);
                once=true;
            }else{ //Dessiner Win au lieu des pieces et mettre DONE a cote du niveau gagne
                Paint paintWin= new Paint();
                paintWin.setColor(Color.YELLOW);
                paintWin.setTextSize(textSize);
                c.drawText("Win!", textX, textY, paintWin);
                if (once){
                    app.writeDone();
                    once=false;
                }
            }
        } catch (IdException ex) {
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder h) {
        boolean retry = true;
        th.setRunning(false);
        while (retry) {
            try {
                th.join();
                retry = false;
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void surfaceChanged (SurfaceHolder holder, int  format, int width, int height){
    }

    //Conversion de dp en pixel
    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    //Dessiner les pieces
    public void dessinerPuzzle(Canvas c) throws IdException {
        for (Piece k : m.getListepiece()) {
            if (m.getCol(k.getId()) == null) return;
            int posx = m.getCol(k.getId()) * carreW;
            int posy = m.getLig(k.getId()) * carreW;
            dessinerVoiture(k.getId(), posx, posy, c);
        }
    }

    //Recuperer la position d'une piece sur la surface
    private Position getPosOnSurface(int x, int y) {
        int col = (x - decal) / carreW;
        int lig = (y -decal) / carreW;
        col = Math.min(col,6);
        lig = Math.min(lig,6);
        Position pos = new Position(col, lig);
        return pos;
    }

    //Dessiner une voiture a partir de ses coordonnees
    private void dessinerVoiture(int id, int xl, int yt, Canvas c) throws IdException {
        if (id == 0) {
            c.drawBitmap(goal, xl + decal, yt + decal, null);
        } else {
            int size = m.getSize(id);
            Direction orientation = m.getOrientation(id);
            if(size==2){
                if (orientation == Direction.HORIZONTAL)
                    c.drawBitmap(hor2, xl + decal, yt + decal, null);
                else
                    c.drawBitmap(ver2, xl + decal, yt + decal, null);
            }else{
                if (orientation == Direction.HORIZONTAL)
                    c.drawBitmap(hor3, xl + decal, yt + decal, null);
                else
                    c.drawBitmap(ver3, xl + decal, yt + decal, null);
            }
        }
    }

    //Deplacer une piece
    public void deplacerSurSurface () throws IdException {
        int dx = x2/carreW - x/carreW;
        int dy = y2/carreW - y/carreW;
        if (m.getOrientation(idmove) == Direction.HORIZONTAL) {
            if (dx!=0){
                if (x2 > x) {
                    if (m.moveForward(idmove)){
                        x = x2;
                        move = true;
                    }
                }
                if (x2 < x) {
                    if(m.moveBackward(idmove)) {
                        x = x2;
                        move = true;
                    }
                }
            }
        } else {
            if (dy != 0) {
                if (y2 > y) {
                    if (m.moveForward(idmove)) {
                        y = y2;
                        move = true;
                    }
                }
                if (y2 < y) {
                    if (m.moveBackward(idmove)) {
                        y = y2;
                        move = true;
                    }
                }
            }
        }
    }

    //Changer les voitures
    public void setCars (){
        Bitmap goalsrc=null;
        Bitmap hor2src=null;
        Bitmap hor3src=null;
        Bitmap ver2src=null;
        Bitmap ver3src=null;
        switch (app.getStyle()){
            case 1:
                goalsrc = BitmapFactory.decodeResource(getResources(), R.drawable.style1goal);
                hor2src = BitmapFactory.decodeResource(getResources(), R.drawable.style1hor2);
                hor3src = BitmapFactory.decodeResource(getResources(), R.drawable.style1hor3);
                ver2src = BitmapFactory.decodeResource(getResources(), R.drawable.style1ver2);
                ver3src = BitmapFactory.decodeResource(getResources(), R.drawable.style1ver3);
                stylegm=1;
                break;
            case 2:
                goalsrc = BitmapFactory.decodeResource(getResources(), R.drawable.style2goal);
                hor2src = BitmapFactory.decodeResource(getResources(), R.drawable.style2hor2);
                hor3src = BitmapFactory.decodeResource(getResources(), R.drawable.style2hor3);
                ver2src = BitmapFactory.decodeResource(getResources(), R.drawable.style2ver2);
                ver3src = BitmapFactory.decodeResource(getResources(), R.drawable.style2ver3);
                stylegm=2;
                break;
            case 3:
                goalsrc = BitmapFactory.decodeResource(getResources(), R.drawable.style3goal);
                hor2src = BitmapFactory.decodeResource(getResources(), R.drawable.style3hor2);
                hor3src = BitmapFactory.decodeResource(getResources(), R.drawable.style3hor3);
                ver2src = BitmapFactory.decodeResource(getResources(), R.drawable.style3ver2);
                ver3src = BitmapFactory.decodeResource(getResources(), R.drawable.style3ver3);
                stylegm=3;
                break;
        }
        goalsrc = Bitmap.createScaledBitmap(goalsrc, carreW * 2, carreW, true);
        hor2src = Bitmap.createScaledBitmap(hor2src, carreW * 2, carreW, true);
        ver2src = Bitmap.createScaledBitmap(ver2src, carreW, carreW * 2, true);
        hor3src = Bitmap.createScaledBitmap(hor3src, carreW * 3, carreW, true);
        ver3src = Bitmap.createScaledBitmap(ver3src, carreW, carreW * 3, true);
        goal = goalsrc;
        hor2 = hor2src;
        ver2 = ver2src;
        hor3 = hor3src;
        ver3 = ver3src;
    }

    //Conversion les valeurs principales en fonction de la taille d'ecran
    public void setValues(){
        int resize;
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            resize = (int)(screenHeight * pourcent);
            decal = (int)(screenHeight * decal_pourcent);
        }else{
            resize = (int)(screenWidth * pourcent);
            decal = (int)(screenWidth * decal_pourcent);
        }
        surfaceW = resize - 2*decal;
        carreW = surfaceW / 6 ;
        textX = (int)(resize*0.23)+decal;
        textY = (int)(resize*0.54)+decal;
        textSize = textX;
    }
}