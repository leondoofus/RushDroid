package android.rushdroid.model;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class Model implements IModel {

    private Grid grid;

    private ArrayList<Piece> listepiece;
    private ArrayList<Piece> lpsec; //les autres listes pieces
    private ArrayList<ArrayList<Piece>> superlp; //LA LISTE des listes des pieces


    public Model() {
        grid = new Grid();
        listepiece = new ArrayList<Piece>();

        listepiece.add(new Piece(0, 2, Direction.HORIZONTAL, 1, 2));
        listepiece.add(new Piece(1, 2, Direction.HORIZONTAL, 0, 0));
        listepiece.add(new Piece(2, 3, Direction.VERTICAL, 0, 1));
        listepiece.add(new Piece(3, 2, Direction.VERTICAL, 0, 4));
        listepiece.add(new Piece(4, 3, Direction.HORIZONTAL, 2, 5));
        listepiece.add(new Piece(5, 3, Direction.VERTICAL, 3, 1));
        listepiece.add(new Piece(6, 2, Direction.HORIZONTAL, 4, 4));
        listepiece.add(new Piece(7, 3, Direction.VERTICAL, 5, 0));

        for (Piece k : listepiece) {
            for (int i = 0; i < k.getSize(); i++) {
                if (k.getOrientation() == Direction.HORIZONTAL)
                    grid.set(k.getPos().addCol(i), k.getId());
                else
                    grid.set(k.getPos().addLig(i), k.getId());
            }
        }
    }

    public Document readXMLFile(String fname, Context context) {
        try {
            int id = context.getResources().getIdentifier(fname, "raw", context.getPackageName());
            InputStream is = context.getResources().openRawResource(id);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(is);
        } catch (Exception ex) {
            return null;
        }
    }

    public void createListePiece(String fname, Context context) throws NotFound {
        superlp = new ArrayList<ArrayList<Piece>>();
        Document d = readXMLFile(fname, context);
        NodeList vList0 = d.getElementsByTagName("puzzle");

        if (vList0 == null) {
            throw new NotFound("Id puzzle non trouvé");
        }

        for (int numV = 0; numV < vList0.getLength(); numV++) {
            NodeList vList = vList0.item(numV).getChildNodes();
            lpsec = new ArrayList<Piece>();
            for (int temp = 0; temp < vList.getLength(); temp++) {
                Node voiture = vList.item(temp);

                if (voiture.getNodeType() == Node.ELEMENT_NODE) {
                    NamedNodeMap atts = voiture.getAttributes();
                    Direction dir;
                    if (atts == null) {
                        System.out.println(" atts == null");
                    }
                    if (atts.getNamedItem("dir").getTextContent().equals("H")) {
                        dir = Direction.HORIZONTAL;
                    } else {
                        dir = Direction.VERTICAL;
                    }
                    if (dir == null) {
                        System.out.println(" dir == null");
                    }

                    lpsec.add(new Piece(Integer.parseInt(atts.getNamedItem("id").getTextContent()),
                            Integer.parseInt(atts.getNamedItem("len").getTextContent()),
                            dir, Integer.parseInt(atts.getNamedItem("col").getTextContent()),
                            Integer.parseInt(atts.getNamedItem("lig").getTextContent())));
                }
            }
            superlp.add(cloneListe(lpsec));
            lpsec.clear();
        }
    }

    public Model(String fname, Context context) {

        grid = new Grid();
        listepiece = new ArrayList<Piece>();

        try {
            createListePiece(fname, context);
            selectPuzzle(0);
        } catch (NotFound e) {
        }
    }


    private ArrayList<Piece> cloneListe(ArrayList<Piece> listeOrig) {
        ArrayList<Piece> listeCour = new ArrayList<Piece>();

        for (int i = 0; i < listeOrig.size(); i++) {
            Piece pOrig = listeOrig.get(i);
            Piece pCour = new Piece(pOrig.getId(), pOrig.getSize(), pOrig.getOrientation(),
                    pOrig.getPos().getCol(), pOrig.getPos().getLig());
            listeCour.add(pCour);
        }
        return listeCour;
    }

    public void selectPuzzle(int i) throws NotFound {
        for (int a = 0; a < 6; a++) {
            for (int b = 0; b < 6; b++) {
                grid.unset(new Position(a, b));
            }
        }
        listepiece = cloneListe(superlp.get(i));

        for (Piece k : listepiece) {
            for (int num = 0; num < k.getSize(); num++) {
                if (k.getOrientation() == Direction.HORIZONTAL)
                    grid.set(k.getPos().addCol(num), k.getId());
                else
                    grid.set(k.getPos().addLig(num), k.getId());
            }
        }

    }

    public Integer getIdByPos(Position pos) {
        return grid.get(pos);
    }

    public Integer getSize(int id) {
        for (Piece k : listepiece) {
            if (k.getId() == id) return k.getSize();
        }
        return null;
    }

    public Direction getOrientation(int id) throws IdException {
        for (Piece k : listepiece) {
            if (k.getId() == id) return k.getOrientation();
        }
        throw new IdException("Id non trouvé");
    }

    public Integer getLig(int id) {
        for (Piece k : listepiece) {
            if (k.getId() == id) return k.getPos().getLig();
        }
        return null;
    }

    public Integer getCol(int id) {
        for (Piece k : listepiece) {
            if (k.getId() == id) return k.getPos().getCol();
        }
        return null;
    }

    public ArrayList<Piece> getListepiece (){ return listepiece; }

    public int getNbPuzzle(){
        return superlp.size();
    }

    public boolean endOfGame() {
        for (Piece k : listepiece) {
            if (k.getId() == 0)
                if (k.getPos().getCol() == 4 && k.getPos().getLig() == 2)
                    return true;
                else return false;
        }
        return false;
    }

    public boolean moveForward(int id) {
        for (Piece k : listepiece) {
            if (k.getId() == id) {

                if (k.getOrientation() == Direction.HORIZONTAL) {
                    if (grid.isEmpty(new Position(k.getPos().getCol() + k.getSize(), k.getPos().getLig()))) {
                        grid.unset(k.getPos());
                        grid.set(new Position(k.getPos().getCol() + k.getSize(), k.getPos().getLig()), id);
                        k.setPos(new Position(k.getPos().getCol() + 1, k.getPos().getLig()));
                        return true;
                    }
                } else {
                    if (grid.isEmpty(new Position(k.getPos().getCol(), k.getPos().getLig() + k.getSize()))) {
                        grid.unset(k.getPos());
                        grid.set(new Position(k.getPos().getCol(), k.getPos().getLig() + k.getSize()), id);
                        k.setPos(new Position(k.getPos().getCol(), k.getPos().getLig() + 1));
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean moveBackward(int id) {
        for (Piece k : listepiece) {
            if (k.getId() == id) {
                if (k.getOrientation() == Direction.HORIZONTAL) {
                    if (grid.isEmpty(new Position(k.getPos().getCol() - 1, k.getPos().getLig()))) {
                        grid.unset(new Position(k.getPos().getCol() + k.getSize() - 1, k.getPos().getLig()));
                        grid.set(new Position(k.getPos().getCol() - 1, k.getPos().getLig()), id);
                        k.setPos(new Position(k.getPos().getCol() - 1, k.getPos().getLig()));
                        return true;
                    }
                } else {
                    if (grid.isEmpty(new Position(k.getPos().getCol(), k.getPos().getLig() - 1))) {
                        grid.unset(new Position(k.getPos().getCol(), k.getPos().getLig() + k.getSize() - 1));
                        grid.set(new Position(k.getPos().getCol(), k.getPos().getLig() - 1), id);
                        k.setPos(new Position(k.getPos().getCol(), k.getPos().getLig() - 1));
                        return true;
                    }
                }
            }
        }
        return false;
    }
}