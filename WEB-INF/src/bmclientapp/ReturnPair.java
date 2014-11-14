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
public class ReturnPair <OType> {
    public int nRet; // return error/success code from a function
    public OType val; // returned object, type defined in function header
    public ReturnPair(int nRet, OType val) {
        this.nRet = nRet;
        this.val = val;
    }
}
