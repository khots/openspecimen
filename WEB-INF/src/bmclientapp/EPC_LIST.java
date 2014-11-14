/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bmclientapp;

/**
 *
 * @author tomc
 */
public class EPC_LIST {
    public final static int LEN_EPC_LIST_LIST = 200;
    public final static int MAX_ROW = 10;
    public final static int MAX_COL = 10;
    
    public EPC_LIST_HEADER sHdr;
    public EPC_LIST_ITEM asItem[];
    public int asMatrix[][]; /* 2D array of asItem index */ 
    
    public EPC_LIST() {
        int ix;
        sHdr = new EPC_LIST_HEADER();
        asItem = new EPC_LIST_ITEM[LEN_EPC_LIST_LIST]; /* actual length is u16CountEPC */
        for (ix = 0; ix < LEN_EPC_LIST_LIST; ix++) {
            asItem[ix] = new EPC_LIST_ITEM();
        }
        asMatrix = new int [MAX_ROW][MAX_COL];
    }
    
    public void Clear() {
        int ix;
        sHdr.Clear();
        for (ix = 0; ix < LEN_EPC_LIST_LIST; ix++) {
            asItem[ix].Clear();
        }
        InitMatrix();
    }
    
    public void InitMatrix() {
        int ri;
        int ci;
        for (ri = 0; ri < MAX_ROW; ri++) {
            for (ci = 0; ci < MAX_COL; ci++) {
                asMatrix[ri][ci] = -1;
            }
        }
    }
}
