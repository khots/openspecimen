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
public class ret_VialList {
    public short uCount;
    public EPC_LIST sList; 
    public ret_VialList() {
        uCount = 0;
        sList = new EPC_LIST();
        sList.Clear();
    }
}
