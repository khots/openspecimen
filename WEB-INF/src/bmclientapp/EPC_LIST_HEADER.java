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
public class EPC_LIST_HEADER {
    // All fields should be considered unsigned 16-bit integers.
    // Java only implements signed integers, so use signed 16-bit short integers.
    public short u16IndexStart; /* 0 based index offset from start of list */
    public short u16IndexCount; /* number of list items in this message */
    public short u16CountTotal; /* total number if list items, may be too big for this message */
    public short u16CountRow;
    public short u16CountCol;
    public short u16MaxLenEPC;
    
    public void Clear() {
        // lacking memset, ...
        u16IndexStart = 0;
        u16IndexCount = 0;
        u16CountTotal = 100;
        u16CountRow = 10;
        u16CountCol = 10;
        u16MaxLenEPC = EPC_LIST_ITEM.LEN_EPC_LIST_EPC;
    }
}
