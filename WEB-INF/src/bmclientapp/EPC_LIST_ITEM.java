/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bmclientapp;

import java.util.Arrays;

/**
 *
 * @author tomc
 */
public class EPC_LIST_ITEM {
    public final static int LEN_EPC_LIST_EPC = 128;
    public short u16RowIndex;
    public short u16ColIndex;
    public short u16LenEPC;
    public double dStrength;
    public byte abEPC[];

    public EPC_LIST_ITEM (){
     abEPC = new byte[LEN_EPC_LIST_EPC];
     // LEN_EPC_LIST_EPC]; /* allocate u16MaxLenEPC, actual length is u16LenEPC */
    }
    public void Clear() {
        // lacking memset, ...
        u16RowIndex = 0;
        u16ColIndex = 0;
        u16LenEPC = 0;
        dStrength = 1.0;
        Arrays.fill(abEPC,(byte)0);
    }
}