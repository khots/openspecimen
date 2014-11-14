/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package BoxMapAPI;

/**
 *
 * @author tomc
 */
// transmitted on network as unsigned short 16-bit ints in little endian order
public class RESPONSE_HEADER {
    public static final int SIZE = 4*2;
    public short u16Request;
    public short u16ExtraByteCount;
    public short u16QuickData;    
    public short u16ReturnCode;    

    public RESPONSE_HEADER () {
        u16Request = 0;
        u16ExtraByteCount = 0;
        u16QuickData = 0;
        u16ReturnCode = 0;
    }
}
