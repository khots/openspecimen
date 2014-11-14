/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bmclientapp;
import BoxMapAPI.IBoxMapAPI;
import java.io.*;
import java.net.*;
import java.awt.*;
import java.lang.Integer;

import com.krishagni.catissueplus.core.administrative.events.ScanStorageContainerDetails;

/**
 *
 * @author tomc
 */
public class BMClientApp {
    
    /**
     * @param args the command line arguments
     */
    public static ScanStorageContainerDetails getScanContainerDetails() {
        int port;
        String host;
        int ret;
        port = IBoxMapAPI.DEFAULT_PORT;
        host = "LocalHost";
        
        BoxMapSocketClient client = new BoxMapSocketClient();

        ret = client.Connect(host, port);
        if (0 != ret) {
            return null;
        }
        ScanStorageContainerDetails scanStorageContainerDetails = client.getStorageContainerDetails();
        
        client.Close();
        
        return scanStorageContainerDetails;
    }
    
    
}
