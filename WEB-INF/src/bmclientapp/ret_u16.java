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
// return by reference
public class ret_u16 {
    public short u16; // Java stores only signed integers, use a 16-bit signed short integer
    ret_u16() {
        u16 = 0;
    }
    // return upper and lower octets, use signed 8-bit integers
    public byte uLo() { return (byte) (0xFF & u16); }
    public byte uHi() { return (byte) (0xFF & (u16 >> 8)); }
}
