package android.rushdroid.model;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Xml;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlSerializer;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class TheApplication extends Application {
    private static Context context;     // on recupere le context de l'application
    private static Model m;             // le modele du jeu
    private ArrayList<PieceData> apd;   // la liste des puzzles
    private int currentId =0;           // le niveau courrant
    private int moves=0;                // le nombre de mouvements
    private TextView textElement;       // text du nombre de mouvements
    private TextView textElement2;      // text de l'horloge
    private Activity activity;          // activite courrante
    private long startTime;             // le temps au debut de la partie
    private long currentTime;           // le temps courant
    private ArrayList<Highscore> hs;    // la liste des highscores
    private String name="";             // le nom du joueurs
    private int style=1;                // le style en cours

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        m = new Model("rushpuzzles", context);
        hs = new ArrayList<>();
    }

    public Context getContext(){ return context;}

    public String getName(){ return name; }

    public void setName(String name){
        this.name=name;
    }

    public int getCurrentId(){
        return currentId;
    }

    public ArrayList<PieceData> getPuzzleList(){
        return apd;
    }

    public Model getGame() {
        return m;
    }

    public int getNbPuzzlesApp(){
        return m.getNbPuzzle();
    }

    public int getStyle (){ return style; }

    //on recupere le gameactivity et le text
    public void setActivity(Activity activity){
        this.activity = activity;
        textElement = (TextView)((this.activity)).findViewById(R.id.move);
        textElement2 = (TextView)((this.activity)).findViewById(R.id.clock);
    }

    //on incremente le nb de coups
    public void incrementMoves(){
        moves++;
        if (moves == 1) textElement.setText("Move : "+ moves);
        else textElement.setText("Moves : "+ moves);
    }

    public void setTime(){
        currentTime= System.currentTimeMillis() - startTime;
        long min = currentTime/60000;
        long sec = (currentTime - min*60000)/1000;
        long milli = currentTime - min*60000 - sec*1000;
        String smin = String.format("%02d",min);
        String ssec = String.format("%02d", sec);
        String smilli = String.format("%03d", milli);
        textElement2.setText("Time : " + smin + " : " + ssec + " : " + smilli);
    }

    void setGame(int id) throws NotFound {
        m.selectPuzzle(id);
        currentId =id;
        moves = 0;
        startTime= System.currentTimeMillis();
    }

    public void resetGame() throws NotFound {
        moves = 0;
        textElement.setText("Move : " + moves);
        startTime = System.currentTimeMillis();
        m.selectPuzzle(currentId);
    }

    //populer la liste des puzzle
    public ArrayList<PieceData> inputSomePieceData(Activity activity2){
        apd = new ArrayList<>();
        SharedPreferences sharedPref = activity2.getSharedPreferences("PREF_NAME",Context.MODE_PRIVATE);
        int k = getNbPuzzlesApp();
        int i = 0;
        int res =0;
        while (i<k){
            res=i+1;
            apd.add(new PieceData("Puzzle " + String.valueOf(res), i,sharedPref.getBoolean("Puzzle " + String.valueOf(res), false)));
            i++;
        }
        return apd;
    }




    //on insere un highscore
    public void insertHs(String name){
        hs.add(new Highscore(currentId, name,moves,currentTime));
        //Sort the last element of the list
        for (int i = hs.size()-1; i>0; i--){
            if (hs.get(i).getMoves() < hs.get(i-1).getMoves()){
                Highscore temp = hs.get(i);
                hs.set(i, hs.get(i-1));
                hs.set(i-1, temp);
            } else if (hs.get(i).getMoves() == hs.get(i-1).getMoves()) {
                if (hs.get(i).getTime() < hs.get(i-1).getTime()){
                    Highscore temp = hs.get(i);
                    hs.set(i, hs.get(i-1));
                    hs.set(i-1, temp);
                }
            } else {
                return;
            }
        }
    }

    //on ecrit les highscores dans un fichier xml
    public void writeXml(){
        try {
            XmlSerializer serializer = Xml.newSerializer();
            StringWriter writer = new StringWriter();
            FileOutputStream fileos= getApplicationContext().openFileOutput("highscore", Context.MODE_PRIVATE);
            serializer.setOutput(writer);
            serializer.startDocument("UTF-8", true);
            serializer.startTag("", "rushpuzzles");
            for (Highscore k : hs) {
                serializer.startTag("", "game");
                serializer.attribute("", "name", k.getName());
                serializer.attribute("", "level", Integer.toString(k.getLevel()));
                serializer.attribute("", "move", Integer.toString(k.getMoves()));
                serializer.attribute("", "time", Long.toString((int)k.getTime()));
                serializer.endTag("", "game");
            }
            serializer.endTag("", "rushpuzzles");
            serializer.endDocument();
            serializer.flush();
            String dataWrite = writer.toString();
            fileos.write(dataWrite.getBytes());
            fileos.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    //on efface l'historique des jeux, et les highscores
    public void clearXml(Activity activity3){
        hs.clear();
        SharedPreferences sharedPref = activity3.getSharedPreferences("PREF_NAME2",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }

    //on cree un tableau dans lequel on met les highscores pour le niveau choisi
    public void createTableByLevel(int level, Activity hsactivity) {
        //on recupere le tableau
        TableLayout stk = (TableLayout) ((hsactivity)).findViewById(R.id.table_main);
        int i=1;
        TableRow tbrow0 = new TableRow(hsactivity);
        //on cree chaque colonne
        TextView tv0 = new TextView(hsactivity);
        tv0.setText("Rank\t\t\t\t");
        tv0.setTextColor(Color.YELLOW);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(hsactivity);
        tv1.setText("Name\t\t\t\t");
        tv1.setTextColor(Color.YELLOW);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(hsactivity);
        tv2.setText("Moves\t\t\t\t");
        tv2.setTextColor(Color.YELLOW);
        tbrow0.addView(tv2);
        TextView tv3 = new TextView(hsactivity);
        tv3.setText("Time");
        tv3.setTextColor(Color.YELLOW);
        tbrow0.addView(tv3);
        stk.addView(tbrow0);
        for (Highscore k : hs) {
            if (level == k.getLevel()) {
                long time=k.getTime();
                long min = time/60000;
                long sec = (time - min*60000)/1000;
                long milli = (time - min*60000 - sec*1000)/10;
                String smin = String.format("%02d",min);
                String ssec = String.format("%02d", sec);
                String smilli = String.format("%02d", milli);
                //on cree les lignes
                TableRow tbrow = new TableRow(hsactivity);
                TextView t1v = new TextView(hsactivity);
                t1v.setText("\t\t" + i);
                t1v.setTextColor(Color.WHITE);
                tbrow.addView(t1v);
                TextView t2v = new TextView(hsactivity);
                t2v.setText(k.getName());
                t2v.setTextColor(Color.WHITE);
                tbrow.addView(t2v);
                TextView t3v = new TextView(hsactivity);
                t3v.setText("\t\t" + Integer.toString(k.getMoves()));
                t3v.setTextColor(Color.WHITE);
                tbrow.addView(t3v);
                TextView t4v = new TextView(hsactivity);
                t4v.setText(smin + ":" + ssec + ":" + smilli);
                t4v.setTextColor(Color.WHITE);
                tbrow.addView(t4v);
                stk.addView(tbrow);
                i++;
            }
        }
    }

    //on lit le fichier xml et on importe la liste des highscores dans une liste
    public void readXml(){
        try {
            hs.clear();
            InputStream instream = getApplicationContext().openFileInput("highscore");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document d =  builder.parse(instream);
            NodeList vList0 = d.getElementsByTagName("game");
            if (vList0 == null) {
                throw new NotFound("Id puzzle non trouvé");
            }
            for (int numV = 0; numV < vList0.getLength(); numV++) {
                Node voiture = vList0.item(numV);
                    if (voiture.getNodeType() == Node.ELEMENT_NODE) {
                        NamedNodeMap atts = voiture.getAttributes();
                        hs.add(new Highscore(Integer.parseInt(atts.getNamedItem("level").getTextContent()),
                                atts.getNamedItem("name").getTextContent(),
                                Integer.parseInt(atts.getNamedItem("move").getTextContent()),
                                Long.parseLong(atts.getNamedItem("time").getTextContent())));
                    }
                }
        } catch (Exception ex) {
        }
    }

    //on sauve le style des voitures
    public void writeStyle(int stylename, Activity act){
        if (style == stylename) return;
        SharedPreferences sharedPref = act.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("Style", stylename);
        editor.commit();
        style = stylename;
    }

    //on lit le style des voitures
    public int readStyle(Activity act){
        SharedPreferences sharedPref = act.getSharedPreferences("PREF_NAME",Context.MODE_PRIVATE);
        return sharedPref.getInt("Style",1);
    }


    //on garde en memoire les parties gagnees
    public void writeDone() {
        getPuzzleList().get(getCurrentId()).setDone(true);
        SharedPreferences sharedPref = activity.getSharedPreferences("PREF_NAME2", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("Puzzle " + String.valueOf(currentId + 1), true);
        editor.commit();
    }

    //on sauve l'option du son
    public void writeSound(boolean b, Activity act){
        SharedPreferences sharedPref = act.getSharedPreferences("PREF_NAME", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean("Sound", b);
        editor.commit();
    }
    //on lit l'option du son
    public boolean readSound(Activity act){
        SharedPreferences sharedPref = act.getSharedPreferences("PREF_NAME",Context.MODE_PRIVATE);
        return sharedPref.getBoolean("Sound", true);
    }

    //on initialise style
    public void setOptions(Activity act){
        style = readStyle(act);
    }
}