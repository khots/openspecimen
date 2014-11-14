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
public interface IBoxMapAPI {
//
// This is a TCP/IP stream API
//////////////////////////////////////////////////////////////////////

	public final static int VERSION = 0x105;
	public final static int DEFAULT_PORT = 1992;

	//public enum RETURN_ENUM {
		public final static int RT_SUCCESS = 0;
		public final static int RT_UNKNOWN_REQUEST = 1;
		public final static int RT_MAPPER_ERROR = 2;
        //}

	// public enum REQUEST_ENUM {
		public final static int RE_GET_VERSION = 0;
			// req - none
			// rsp - quick = version
		public final static int RE_GET_LAST_ERROR = 1;
			// req - none
			// rsp - extra = string - reason for mapper error
		public final static int RE_GET_HARDWARE_ID = 2;
			// req - none
			// rsp - extra = string
		public final static int RE_GET_FIRMWARE_ID = 3;
			// req - none
			// rsp - extra = string
		public final static int RE_DO_INVENTORY = 4;
			// req - quick = timeout milliseconds
			// rsp - quick = tag count
		public final static int RE_GET_TAG_COUNT = 5;
			// req - none
			// rsp - quick = tag count = from inventory)
		public final static int RE_GET_TAG_INFO = 6;
			// req - quick = tag index (0..TAG_COUNT-1)
			// rsp - extra = string
		public final static int RE_GET_TAG_RSSI = 7;
			// req - quick = tag index (0..TAG_COUNT-1)
			// rsp - quick = I (lo byte) and Q (hi byte)
		public final static int RE_GET_TAG_EPC = 8;
			// req - quick = tag index (0..TAG_COUNT-1)
			// rsp - quick = ROW (lo byte) and COL (hi byte)
			// rsp - extra = string
		public final static int RE_SCAN_BOX = 9;
			// req - quick = timeout milliseconds
			// rsp - quick = vial count
		public final static int RE_GET_VIAL_COUNT = 10;
			// req - none
			// rsp - quick = vial count (from scan box)
		public final static int RE_GET_VIAL_EPC = 11;
			// req - quick = vial index (0..VIAL_COUNT-1)
			// rsp - quick = ROW (lo byte) and COL (hi byte)
			// rsp - extra = string
		public final static int RE_SET_SCAN_COUNT = 12;
			// deprecated, server side controlled
			// req - quick = minimum number of stable vial reads per ScanBox
			// rsp - none
		public final static int RE_SET_TAG_TYPE = 13;
			// deprecated, server side controlled
			// req - quick = tag type to be scanned (TAG_TYPE_ENUM)
			// rsp - none
		public final static int RE_GET_VIAL_STRENGTH = 14;
			// req - quick = vial index (0..VIAL_COUNT-1)
			// rsp - quick = 16-bit signed strength, units of 0.01 dBm (cents)
		public final static int RE_SET_SCAN_RETRIES = 15;
			// deprecated, server side controlled
			// req - quick = number of partial box reads per ScanBox, to satisfy RE_SET_SCAN_COUNT
			// rsp - none
		public final static int RE_SCAN_BOX_TAGS = 16;
			// req - quick = timeout milliseconds
			// rsp - quick = box tag count

		// PRESELECTS restrict which device is used for later actions
		public final static int RE_PRESELECT_ANY = 20;
			// req - none
			// rsp - none
		public final static int RE_PRESELECT_DEV = 21;
			// req - quick = device number (1..N)
			// rsp - none
		public final static int RE_PRESELECT_GEOMETRY = 22;
			// req - quick = ROWS (lo byte) and COLS (hi byte)
			// rsp - none
		public final static int RE_PRESELECT_BOX_READER = 23;
			// req - quick = POSITIONS (lo byte)
			// rsp - none

		public final static int RE_GET_DEV = 31;
			// req - none
			// rsp - quick = device number (1..N)
		public final static int RE_GET_GEOMETRY = 32;
			// req - none
			// rsp - quick = ROWS (lo byte) and COLS (hi byte)
		public final static int RE_GET_POSITIONS = 33;
			// req - none
			// rsp - quick = POSITIONS (lo byte)

		public final static int RE_ADD_VIAL_LIST = 51;
			// req - extra - file name
			// rsp - none
		public final static int RE_GET_VIAL_LIST = 52;
			// req - quick = vial list index start
			// rsp - quick = vial count (from scan box)
			// rsp - extra = sorted array of vial EPCs in EPC_LIST format
                // }

	//public final static int REQUEST_HEADER_LEN = 6;
	//public final static int RESPONSE_HEADER_LEN = 8;

	// enum TAG_TYPE_ENUM {
		public final static int TTE_UNKNOWN = 0;
		public final static int TTE_FR4 = 1;
		public final static int TTE_PET = 2;
        // }
}
