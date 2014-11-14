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

import java.io.*;
import java.nio.*;
import java.net.*;
//import java.awt.*;
import BoxMapAPI.*;
import javax.xml.bind.DatatypeConverter;

import com.krishagni.catissueplus.core.administrative.events.ScanContainerSpecimenDetails;
import com.krishagni.catissueplus.core.administrative.events.ScanStorageContainerDetails;

public class BoxMapSocketClient {

    // member variables
    // public CLogger log;
    private Socket m_socket;
    private DataOutputStream m_outToServer;
    private DataInputStream m_inFromServer;
    private boolean m_bIsVerbose;

    private int m_uTimeDelayMilliseconds;
    private boolean m_bReceiveReady;

    public final static int RX_BUF_LEN = 8192;
    public final static int TX_BUF_LEN = 8192;

    BoxMapAPI.REQUEST_HEADER m_req;
    ByteBuffer m_req_buffer;
    ByteBuffer m_req_extra;

    BoxMapAPI.RESPONSE_HEADER m_rsp;
    ByteBuffer m_rsp_buffer;
    ByteBuffer m_rsp_extra;
    
    // decodes from m_rsp_extra, depending on value of m_rsp.u16Request
    private EPC_LIST m_listResponse;
    private float m_floatResponse;
    private String m_strResponse;
    private double m_dResponse;

    //int m_nRxBytesNeeded;
    //int m_nTxBytesNeeded;
    //int m_nRxNext;
    //int m_nTxNext;
    public enum eRxState {
        RX_IDLE, RX_GET_HEADER, RX_GET_EXTRA, RX_DID_RX
    };
    eRxState m_eRxState;

    public enum eTxState {
        TX_IDLE, TX_PUT_HEADER, TX_PUT_EXTRA, TX_DID_TX
    };
    eTxState m_eTxState;

    boolean m_bRxBufUnderrun;
    int m_uRxBufUsed;

//////////////////////////////////////////////////////////////////////
// Construction/Destruction
//////////////////////////////////////////////////////////////////////

    public BoxMapSocketClient() {
        m_bIsVerbose = true;
        m_eRxState = eRxState.RX_IDLE;
        m_eTxState = eTxState.TX_IDLE;
        
        m_req = new BoxMapAPI.REQUEST_HEADER();
        
        m_req_buffer = ByteBuffer.allocate(REQUEST_HEADER.SIZE);
        m_req_buffer.order(ByteOrder.LITTLE_ENDIAN);
        m_req_extra = ByteBuffer.allocate(TX_BUF_LEN);
        
        m_rsp = new BoxMapAPI.RESPONSE_HEADER();
        
        m_rsp_buffer = ByteBuffer.allocate(RESPONSE_HEADER.SIZE);
        m_rsp_buffer.order(ByteOrder.LITTLE_ENDIAN);
        m_rsp_extra = ByteBuffer.allocate(RX_BUF_LEN);
        m_listResponse = new EPC_LIST();
        
        //m_nRxBytesNeeded = 0;
        //m_nTxBytesNeeded = 0;
        //m_nRxNext = 0;
        //m_nTxNext = 0;
        
        m_bRxBufUnderrun = false;
        m_uRxBufUsed = 0;
        
        m_bReceiveReady = false;
        m_uTimeDelayMilliseconds = 1000;
        ClearResponse();
        m_listResponse.Clear();
	//log.LoggerSetTag("BMSock");
    }

public void unBoxMapSocketClient()
{
	Close();
}
public void Close()
{
	// ?? should be pure virtual function
}

public void SetVerbose(boolean bIsVerbose) {
    this.m_bIsVerbose = bIsVerbose;
}

public int DefaultPort() {
    return IBoxMapAPI.DEFAULT_PORT;
}

public int Connect(String strHost, int uPort) {
        String strLog = "";
        // echo "host: ".strHost."\n";
        try {
            InetAddress address = InetAddress.getByName(strHost);
            m_socket = new Socket(address, uPort);
            m_outToServer = new DataOutputStream(m_socket.getOutputStream());
            m_inFromServer = new DataInputStream(m_socket.getInputStream());
        } catch (IOException e) {
            LogString("Unable to create TCP socket");
            LogString(e.getLocalizedMessage());
            return -1;
        }
        //  socket_set_nonblock(m_socket)

        // echo "socket: ".m_socket."\n";
        // echo "address: ".$address."\n";
        // echo "port: ".$uPort."\n";

        return 0;
    }

//////////////////////////////////////////////////////////////////////
// public API functions
//////////////////////////////////////////////////////////////////////


    public int GetErrorReason(ret_string sReason) {
        sReason.str = "";
        if (eRxState.RX_DID_RX != m_eRxState) {
            sReason.str = "BoxMapSocket response timeout";
            return 0;
        }
        switch (m_rsp.u16ReturnCode) {
            case IBoxMapAPI.RT_SUCCESS:
                sReason.str = "Success";
                break;
            case IBoxMapAPI.RT_UNKNOWN_REQUEST:
                sReason.str = "BoxMapSocket unknown request";
                break;
            case IBoxMapAPI.RT_MAPPER_ERROR:
                if (0 == GetLastError(sReason)) {
                    break;
                }
                sReason.str = "BoxMapper error, failed GetLastError()";
                break;
            default:
                sReason.str = "BoxMapSocket unknown error";
                break;
        }
        return 0;
    }

    public int GetVersion(ret_u16 sVersion) {
        sVersion.u16 = 0;
        int ret = Request(IBoxMapAPI.RE_GET_VERSION);
        if (0 != ret) {
            return ret;
        }
        ret = GetQuickData(sVersion);
        if (0 != ret) {
            return ret;
        }
        return 0;
    }

    public int GetLastError(ret_string sReason) {
        sReason.str = "";
        if (0 != Request(IBoxMapAPI.RE_GET_LAST_ERROR)) {
            return -1;
        }
        return GetStringData(sReason);
    }

    public int PreselectAny() {
        if (0 != Request(IBoxMapAPI.RE_PRESELECT_ANY)) {
            return -1;
        }
        return GetResponse();
    }

    public int PreselectGeometry(int uNumRows, int uNumCols) {
        int uQuickData = (uNumCols << 8) | uNumRows;
        if (0 != RequestQ(IBoxMapAPI.RE_PRESELECT_GEOMETRY, uQuickData)) {
            return -1;
        }
        return GetResponse();
    }

    public int PreselectBoxReader(int uNumPositions) {
        int uQuickData = uNumPositions;
        if (0 != RequestQ(IBoxMapAPI.RE_PRESELECT_BOX_READER, uQuickData)) {
            return -1;
        }
        return GetResponse();
    }

    public int PreselectDevice(int uNumDevice) {
        if (0 != RequestQ(IBoxMapAPI.RE_PRESELECT_DEV, uNumDevice)) {
            return -1;
        }
        return GetResponse();
    }

// deprecated
// The client can no longer set these parameters. The server decides this.
// The server will ignore settings from old software.
    public int SetReaderScanCount(int uScanCount) {
        if (0 != RequestQ(IBoxMapAPI.RE_SET_SCAN_COUNT, uScanCount)) {
            return -1;
        }
        return GetResponse();
    }

// deprecated
// The client can no longer set these parameters. The server decides this.
// The server will ignore settings from old software.
    public int SetReaderScanRetries(int uScanRetries) {
        if (0 != RequestQ(IBoxMapAPI.RE_SET_SCAN_RETRIES, uScanRetries)) {
            return -1;
        }
        return GetResponse();
    }

// deprecated
// The client can no longer set these parameters. The server decides this.
// The server will ignore settings from old software.
    public int SetTagType(int uTagType) {
        if (0 != RequestQ(IBoxMapAPI.RE_SET_TAG_TYPE, uTagType)) {
            return -1;
        }
        return GetResponse();
    }

    public int GetGeometry(ret_geometry sGeometry) {
        ret_u16 sQuickData = new ret_u16();

        sGeometry.uNumRows = 0;
        sGeometry.uNumCols = 0;

        if (0 != Request(IBoxMapAPI.RE_GET_GEOMETRY)) {
            return -1;
        }
        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sGeometry.uNumRows = sQuickData.uLo();
        sGeometry.uNumCols = sQuickData.uHi();
        return 0;
    }

    public int GetPositions(ret_u16 sNumPositions) {
        ret_u16 sQuickData = new ret_u16();

        sNumPositions.u16 = 0;

        if (0 != Request(IBoxMapAPI.RE_GET_POSITIONS)) {
            return -1;
        }
        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sNumPositions.u16 = sQuickData.u16;
        return 0;
    }

    public int GetDevice(ret_u16 sNumDevice) {
        ret_u16 sQuickData = new ret_u16();

        sNumDevice.u16 = 0;

        if (0 != Request(IBoxMapAPI.RE_GET_DEV)) {
            return -1;
        }
        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sNumDevice.u16 = sQuickData.u16;
        return 0;
    }

    public int GetHardwareID(ret_string strID) {
        strID.str = "";
        if (0 != Request(IBoxMapAPI.RE_GET_HARDWARE_ID)) {
            return -1;
        }
        return GetStringData(strID);
    }

    public int GetFirmwareID(ret_string strID) {
        strID.str = "";
        if (0 != Request(IBoxMapAPI.RE_GET_FIRMWARE_ID)) {
            return -1;
        }
        return GetStringData(strID);
    }

    public int DoInventory(int uMilliseconds, ret_u16 sTagCount) {
        ret_u16 sQuickData = new ret_u16();
        sTagCount.u16 = 0;
        if (0 != RequestQ(IBoxMapAPI.RE_DO_INVENTORY, uMilliseconds)) {
            return -1;
        }
        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sTagCount.u16 = sQuickData.u16;
        return 0;
    }

    public int GetTagCount(ret_u16 sTagCount) {
        ret_u16 sQuickData = new ret_u16();
        sTagCount.u16 = 0;

        if (0 != Request(IBoxMapAPI.RE_GET_TAG_COUNT)) {
            return -1;
        }

        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sTagCount.u16 = sQuickData.u16;
        return 0;
    }

    public int GetTagInfo(int uTagIndex, ret_string strInfo) {
        strInfo.str = "";
        if (0 != RequestQ(IBoxMapAPI.RE_GET_TAG_INFO, uTagIndex)) {
            return -1;
        }
        return GetStringData(strInfo);
    }

    public int GetTagRSSI(int uTagIndex, ret_RSSI sRSSI) {
        ret_u16 sQuickData = new ret_u16();
        sRSSI.uI = 0;
        sRSSI.uQ = 0;

        if (0 != RequestQ(IBoxMapAPI.RE_GET_TAG_RSSI, uTagIndex)) {
            return -1;
        }
        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sRSSI.uI = sQuickData.uLo();
        sRSSI.uQ = sQuickData.uHi();
        return 0;
    }

    public int GetTagEPC(int uTagIndex, ret_EPC sEPC) {
        ret_u16 sQuickData = new ret_u16();
        ret_string strEPC = new ret_string();
        if (0 != RequestQ(IBoxMapAPI.RE_GET_TAG_EPC, uTagIndex)) {
            return -1;
        }
        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sEPC.uRow = sQuickData.uLo();
        sEPC.uCol = sQuickData.uHi();
        if (0 != GetStringData(strEPC)) {
            return -1;
        }
        sEPC.strEPC = strEPC.str;
        return 0;
    }

    public int ScanBox(int uMilliseconds, ret_u16 sVialCount) {
        ret_u16 sQuickData = new ret_u16();
        if (0 != RequestQ(IBoxMapAPI.RE_SCAN_BOX, uMilliseconds)) {
            return -1;
        }
        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sVialCount.u16 = sQuickData.u16;
        return 0;
    }

    public int ScanBoxTags(int uMilliseconds, ret_u16 sTagCount) {
        ret_u16 sQuickData = new ret_u16();
        if (0 != RequestQ(IBoxMapAPI.RE_SCAN_BOX_TAGS, uMilliseconds)) {
            return -1;
        }
        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sTagCount.u16 = sQuickData.u16;
        return 0;
    }

    public int GetVialCount(ret_u16 sVialCount) {
        ret_u16 sQuickData = new ret_u16();

        sVialCount.u16 = 0;

        if (0 != Request(IBoxMapAPI.RE_GET_VIAL_COUNT)) {
            return -1;
        }

        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sVialCount.u16 = sQuickData.u16;
        return 0;
    }

    public int GetVialEPC(int uVialIndex, ret_EPC sEPC) {
        ret_string strEPC = new ret_string();
        ret_u16 sQuickData = new ret_u16();

        if (0 != RequestQ(IBoxMapAPI.RE_GET_VIAL_EPC, uVialIndex)) {
            return -1;
        }
        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sEPC.uRow = sQuickData.uLo();
        sEPC.uCol = sQuickData.uHi();
        if (0 != GetStringData(strEPC)) {
            return -1;
        }
        sEPC.strEPC = strEPC.str;
        return 0;
    }

    public int GetVialStrength(int uVialIndex, ret_double sStrength) {
        ret_u16 sQuickData = new ret_u16();

        if (0 != RequestQ(IBoxMapAPI.RE_GET_VIAL_STRENGTH, uVialIndex)) {
            return -1;
        }
        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        return GetFloatData(sStrength);
    }

//	public int GetVialList($uVialIndexStart, &$uVialCount, EPC_LIST &$sVialList) {
    public int GetVialList(int uVialIndexStart, ret_VialList sVials) {
        ret_u16 sQuickData = new ret_u16();

        sVials.uCount = 0;

        if (0 == uVialIndexStart) {
            InitListMatrix();
        }

        if (0 != RequestQ(IBoxMapAPI.RE_GET_VIAL_LIST, uVialIndexStart)) {
            return -1;
        }

        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sVials.uCount = sQuickData.u16;
        return GetListData(sVials);
    }

    public int AddVialList(String strFileName) {
        ret_u16 sQuickData = new ret_u16();

        if (0 != RequestS(IBoxMapAPI.RE_ADD_VIAL_LIST, strFileName)) {
            return -1;
        }
        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        return 0;
    }
    
    private void ClearResponse() {
        m_eRxState = eRxState.RX_IDLE;
        m_rsp_buffer.clear();
        m_rsp_extra.clear();
        m_eRxState = eRxState.RX_GET_HEADER;
    }

    private int DoRequest() {
        ClearResponse();
        // transmit request header fields as little endian unsigned short ints
        m_req_buffer.clear();
        m_req_buffer.putShort(m_req.u16Request);
        m_req_buffer.putShort(m_req.u16ExtraByteCount);
        m_req_buffer.putShort(m_req.u16QuickData);
        m_eTxState = eTxState.TX_PUT_HEADER;
        return TrySend();
    }

    private int Request(int eRequest) {
        m_eTxState = eTxState.TX_IDLE;
        m_req.u16Request = (short) eRequest;
        m_req.u16ExtraByteCount = 0;
        m_req.u16QuickData = 0;
        m_req_extra.clear();
        return DoRequest();
    }

    private int RequestQ(int eRequest, int uQuickData) {
        m_eTxState = eTxState.TX_IDLE;
        m_req.u16Request = (short) eRequest;
        m_req.u16ExtraByteCount = 0;
        m_req.u16QuickData = (short) uQuickData;
        m_req_extra.clear();
        return DoRequest();
    }

    private int RequestS(int eRequest, String str) {
        m_eTxState = eTxState.TX_IDLE;
        m_req.u16Request = (short) eRequest;
        m_req.u16ExtraByteCount = (short) str.length();
        m_req.u16QuickData = 0;
        m_req_extra.clear();
        m_req_extra.put(str.getBytes());
        return DoRequest();
    }

    private int TrySend() {
        try {
            while (true) {
                switch (m_eTxState) {
                    case TX_PUT_HEADER:
                        m_outToServer.write(m_req_buffer.array());
                        if (0 != m_req.u16ExtraByteCount) {
                            m_eTxState = eTxState.TX_PUT_EXTRA;
                            break;
                        }
                        m_eTxState = eTxState.TX_DID_TX;
                        break;
                    case TX_PUT_EXTRA:
                        m_outToServer.write(m_req_extra.array());
                        m_eTxState = eTxState.TX_DID_TX;
                        break;
                    default:
                        return 0; // nothing to do
                }
            }
        } catch (IOException e) {
            return -1;
        }
    }

    private int GetQuickData(ret_u16 sQuickData) {
        sQuickData.u16 = 0;
        TryReceive();

        if (eRxState.RX_DID_RX != m_eRxState) {
            return -1;
        }
        if (0 != m_rsp.u16ReturnCode) {
            return -1;
        }
        sQuickData.u16 = m_rsp.u16QuickData;
        return 0;
    }

    private int GetStringData(ret_string strData) {
        strData.str = "";
        ret_u16 sQuickData = new ret_u16();

        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        strData.str = m_strResponse;
        return 0;
    }

    private int GetFloatData(ret_double sfloatData) {
        sfloatData.dval = 0.0;
        ret_u16 sQuickData = new ret_u16();

        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sfloatData.dval = m_floatResponse;
        return 0;
    }

    private int GetListData(ret_VialList sListData) {
        sListData.sList = new EPC_LIST();
        ret_u16 sQuickData = new ret_u16();

        if (0 != GetQuickData(sQuickData)) {
            return -1;
        }
        sListData.sList = m_listResponse;
        return 0;
    }

    private int TryReceive() {
        try {
            while (true) {
                switch (m_eRxState) {
                    case RX_GET_HEADER:
                        m_inFromServer.readFully(m_rsp_buffer.array(), 0, RESPONSE_HEADER.SIZE);
                        m_rsp.u16Request = m_rsp_buffer.getShort();
                        m_rsp.u16ExtraByteCount = m_rsp_buffer.getShort();
                        m_rsp.u16QuickData = m_rsp_buffer.getShort();
                        m_rsp.u16ReturnCode = m_rsp_buffer.getShort();
                        if (0 < m_rsp.u16ExtraByteCount) {
                            m_inFromServer.readFully(m_rsp_extra.array(), 0, (int) m_rsp.u16ExtraByteCount);
                        }
                        m_eRxState = eRxState.RX_DID_RX;
                        DoResponse();
                        break;
                    default:
                        return 0; // nothing to do
                }
            }
        } catch (IOException e) {
            return -1;
        }
    }

private void LogString(String strLog) {
    if (!m_bIsVerbose) {
        return;
    }
    System.out.println (strLog);
}

    private void ResetRxBuf() {
        m_bRxBufUnderrun = false;
        m_uRxBufUsed = 0;
        m_rsp_extra.rewind();
    }

    private int GetU8() {
        int u8;
        if (!m_rsp_extra.hasRemaining()) {
            m_bRxBufUnderrun = true;
            return 0;
        }
        u8 = m_rsp_extra.get();
        return u8;
    }

    // assemble an unsigned 16-bit integer
    // Java only supports signed integers, so use a signed 16-bit short int
    private short GetU16() {
        int u16Data;
        u16Data = GetU8();
        u16Data |= GetU8() << 8;
        return (short) u16Data;
    }

    private float GetCents() {
	// The general case of moving a double between architectures is complicated.
	// Here we are only moving a signed floating point number with two decimal places of precision.
        float floatData;
        floatData = 0.01f * GetU16(); // ?? should cast to signed int
        return floatData;
    }

    private void GetBlock(byte abData[], int uLen) {
        int ux;
        if (m_rsp_extra.remaining() < uLen) {
            m_bRxBufUnderrun = true;
            return;
        }
        for (ux = 0; ux < uLen; ux++) {
            abData[ux] = (byte) GetU8();
        }
    }

    private void InitListMatrix() {
        m_listResponse.InitMatrix();
    }

    private void GetListHeader(EPC_LIST_HEADER hdr) {
        hdr.u16IndexStart = GetU16();
        hdr.u16IndexCount = GetU16();
        hdr.u16CountTotal = GetU16();
        hdr.u16CountRow = GetU16();
        hdr.u16CountCol = GetU16();
        hdr.u16MaxLenEPC = GetU16();
    }

    private boolean GetListItem(EPC_LIST_ITEM item, String strLog) {

        item.u16RowIndex = GetU16();
        item.u16ColIndex = GetU16();
        item.dStrength = GetCents();
        item.u16LenEPC = GetU16();

        if (m_bRxBufUnderrun) {
            strLog = String.format(" truncated item, length %d", (int) m_rsp.u16ExtraByteCount
            );
            return false;
        }
        if (item.u16LenEPC > EPC_LIST_ITEM.LEN_EPC_LIST_EPC) {
            strLog = String.format(" item EPC length %d exceeds %d", (int) item.u16LenEPC, EPC_LIST_ITEM.LEN_EPC_LIST_EPC
            );
            return false;
        }
        GetBlock(item.abEPC, item.u16LenEPC);
        if (m_bRxBufUnderrun) {
            strLog = String.format(" truncated item, length %d", m_rsp.u16ExtraByteCount
            );
            return false;
        }
        return true;
    }

    private void GetList(EPC_LIST list, String strLog) {
        int ux;
        ResetRxBuf();
	// m_listResponse = new EPC_LIST;

        // get the header
        GetListHeader(list.sHdr);
        if (m_bRxBufUnderrun) {
            strLog = String.format(" truncated header, length %d", (int) m_rsp.u16ExtraByteCount
            );
            rspListError:
            list.sHdr = new EPC_LIST_HEADER();
            return;
        }
        if (list.sHdr.u16MaxLenEPC > EPC_LIST_ITEM.LEN_EPC_LIST_EPC) {
            strLog = String.format(" max EPC length %d exceeds %d", (int) list.sHdr.u16MaxLenEPC, EPC_LIST_ITEM.LEN_EPC_LIST_EPC
            );
            //goto rspListError;
            list.sHdr = new EPC_LIST_HEADER();
            return;
        }

        /* list.InitMatrix() should have been called once, when the first block is received */
        for (ux = 0; ux < list.sHdr.u16IndexCount; ux++) {
            int uIndex = 0 + ux + list.sHdr.u16IndexStart;
            if (uIndex >= EPC_LIST.LEN_EPC_LIST_LIST) {
                strLog = String.format(" item index %d exceeds allocation %d", uIndex, EPC_LIST.LEN_EPC_LIST_LIST
                );
                //goto rspListError;
                list.sHdr = new EPC_LIST_HEADER();
                return;
            }
            if (!GetListItem(list.asItem[uIndex], strLog)) {
                strLog = String.format(" item %d, " + strLog, uIndex
                );
                //goto rspListError;
                list.sHdr = new EPC_LIST_HEADER();
                return;
            }

            while (true) {
                int ri = list.asItem[uIndex].u16RowIndex;
                int ci = list.asItem[uIndex].u16ColIndex;
                if (ri < 1) {
                    break;
                }
                if (ri > EPC_LIST.MAX_ROW) {
                    break;
                }
                if (ci < 1) {
                    break;
                }
                if (ci > EPC_LIST.MAX_COL) {
                    break;
                }
                list.asMatrix[ri - 1][ci - 1] = uIndex;
                break;
            }

        }
    }

    private void DoResponse() {
        String strLog;
        String strLog2 = "";
        int uLen = 0;
        boolean doString = false;
        boolean doCents = false;
        boolean doList = false;
        m_strResponse = "";
        m_floatResponse = 0.0f;

        strLog = String.format("Rsp Return: %d", (int) m_rsp.u16ReturnCode);
        LogString(strLog);

        switch (m_rsp.u16Request) {
            case IBoxMapAPI.RE_GET_VERSION:
                strLog = String.format("Rsp Version: %x", (int) m_rsp.u16QuickData);
                break;
            case IBoxMapAPI.RE_GET_LAST_ERROR:
                strLog = String.format("Rsp LastError: ");
                doString = true;
                break;
            case IBoxMapAPI.RE_GET_HARDWARE_ID:
                strLog = String.format("Rsp Hardware ID: ");
                doString = true;
                break;
            case IBoxMapAPI.RE_GET_FIRMWARE_ID:
                strLog = String.format("Rsp Firmware ID: ");
                doString = true;
                break;
            case IBoxMapAPI.RE_DO_INVENTORY:
            case IBoxMapAPI.RE_GET_TAG_COUNT:
                strLog = String.format("Rsp Tag Count: %d", (int) m_rsp.u16QuickData);
                break;
            case IBoxMapAPI.RE_GET_TAG_INFO:
                strLog = String.format("Rsp Tag Info: ");
                doString = true;
                break;
            case IBoxMapAPI.RE_GET_TAG_RSSI:
                strLog = String.format("Rsp Tag RSSI: %x", (int) m_rsp.u16QuickData);
                break;
            case IBoxMapAPI.RE_GET_TAG_EPC:
                strLog = String.format("Rsp Tag Position: %x EPC: ", (int) m_rsp.u16QuickData);
                doString = true;
                break;
            case IBoxMapAPI.RE_SCAN_BOX:
            case IBoxMapAPI.RE_GET_VIAL_COUNT:
                strLog = String.format("Rsp Vial Count: %d", (int) m_rsp.u16QuickData);
                break;
            case IBoxMapAPI.RE_SCAN_BOX_TAGS:
                strLog = String.format("Rsp Tag Count: %d", (int) m_rsp.u16QuickData);
                break;
            case IBoxMapAPI.RE_GET_VIAL_EPC:
                strLog = String.format("Rsp Get Vial Position: %x EPC: ", (int) m_rsp.u16QuickData);
                doString = true;
                break;
            case IBoxMapAPI.RE_GET_VIAL_STRENGTH:
                strLog = String.format("Rsp Get Vial Stringth");
                doCents = true;
                break;
            case IBoxMapAPI.RE_GET_DEV:
                strLog = String.format("Rsp USB Device: %d", (int) m_rsp.u16QuickData);
                break;
            case IBoxMapAPI.RE_GET_GEOMETRY: {
                int uRows = 0xFF & (m_rsp.u16QuickData >> 0);
                int uCols = 0xFF & (m_rsp.u16QuickData >> 8);
                strLog = String.format("Rsp Get Geometry: %d rows, %d cols", uRows, uCols);
            }
            break;
            case IBoxMapAPI.RE_GET_POSITIONS:
                strLog = String.format("Rsp Box Tag Positions: %d", (int) m_rsp.u16QuickData);
                break;
            case IBoxMapAPI.RE_ADD_VIAL_LIST:
                strLog = String.format("Rsp Add Vial List");
                break;
            case IBoxMapAPI.RE_GET_VIAL_LIST:
                strLog = String.format("Rsp Get Vial List: %d", (int) m_rsp.u16QuickData);
                doList = true;
                break;
            default:
                break;
        }
        if (doString) {
            uLen = m_rsp.u16ExtraByteCount;
            if (uLen > 0) {
                m_strResponse = new String(m_rsp_extra.array());
            } else {
                m_strResponse = "null";
            }
            strLog = strLog + m_strResponse;
        }
        if (doCents) {
            m_floatResponse = 0.01f * m_rsp.u16QuickData; /* may break for negative values */

            strLog = String.format("%s: %f", strLog, m_floatResponse);
        }
        if (doList) {
            GetList(m_listResponse, strLog2);
            strLog = strLog + strLog2;
        }

        LogString(strLog);
    }


// Always get a response even if quick and extra data will be ignored.
// Otherwise the next request's response will be stuck behind the uncollected response.

    private int GetResponse() {
        ret_u16 sQuickData = new ret_u16();
        return GetQuickData(sQuickData);
    }

    // Test and demonstrate the API calls
    public ScanStorageContainerDetails getStorageContainerDetails() {

        int ret;
        
        String str;
        ret_u16 r1 = new ret_u16();
        ret_u16 r2 = new ret_u16();
        ret_string s1 = new ret_string();
        ret_geometry g1 = new ret_geometry();
        ret_EPC sEPC = new ret_EPC();
        ret_VialList v1 = new ret_VialList();
        int vx;
        
        // For selected API calls,
        // BoxMapper version 0.27 will abort with an exception if no hardware is connected.
        // Until that is fixed, avoid these API calls.
        boolean haveHardware = false; // assume for now

        SetVerbose(true); // emit debug messages as API exchanges requests and responses
        
        // Getting the version should always succeed, even if no hardware is attached to the server
        // The version will indicate which API features are supported.        
        ret = GetVersion(r1);
        if (0 != ret) {
            MapperError(ret);
            return null; // no point in proceeding if this fails
        }
        byte abVersion[] = new byte[2];
        abVersion[1] = (byte)(0xFF & (r1.u16 >> 0)); // minor
        abVersion[0] = (byte)(0xFF & (r1.u16 >> 8)); // major
        
        str = DatatypeConverter.printHexBinary(abVersion);
            
        // This will fail with no hardware present
        if (haveHardware) {
        ret = GetDevice(r1);
        }
        
        // this will fail with no hardware present
        if (haveHardware) {
        ret = GetHardwareID(s1);
        if (0 != ret) {
            MapperError(ret);
        } 
        }
        
        // this will fail with no hardware present
        if (haveHardware) {
        ret = GetFirmwareID(s1);
        if (0 != ret) {
            MapperError(ret);
        }
        }
        
        ret = GetGeometry(g1);
        if (0 != ret) {
            MapperError(ret);
        }
        ret = ScanBox(9000, r1);
        if (0 != ret) {
            MapperError(ret);
        }
       
        // The previous scan is probably done.
        // Get the number of vials found.
        // This should be the same as the count returned by ScanBox().
        // If still busy, we can poll later for completion.
        // The emulator responds immediately.
        ret = GetVialCount(r2); // use a different return object, for comparison to r1
        if (0 != ret) {
            MapperError(ret);
        }
        
        ScanStorageContainerDetails scanStorageContainerDetails = new ScanStorageContainerDetails();
        scanStorageContainerDetails.setContainerName("HeadAndNeck_WholeBloodBox_9x9");
        scanStorageContainerDetails.setOneDimensionCapacity(Integer.parseInt(Byte.toString(g1.uNumRows)));
        scanStorageContainerDetails.setTwoDimensionCapacity(Integer.parseInt(Byte.toString(g1.uNumCols)));
        for (vx = 0; vx < r1.u16; vx++) {
            ret = GetVialEPC(vx, sEPC);
            if (0 != ret) {
                MapperError(ret);
                break;
            } else {
                ScanContainerSpecimenDetails scanContainerSpecimenDetails = new ScanContainerSpecimenDetails();
                scanContainerSpecimenDetails.setBarCode(sEPC.strEPC.trim());
                scanContainerSpecimenDetails.setPosX(Byte.toString(sEPC.uRow));
                scanContainerSpecimenDetails.setPosY(Byte.toString(sEPC.uCol));
                scanStorageContainerDetails.getSpecimenList().add(scanContainerSpecimenDetails);
            }
        }
        return scanStorageContainerDetails;
   }

    public void MapperError(int ret) {
        String str;
        int ret2;
        ret_string rs = new ret_string();
        
        if (0 == ret) {
            return; // no error
        }
        str = String.format("Failed with error %d", ret);
        
        // try to get a string explaining the error
        ret2 = GetLastError(rs);
        if (0 != ret2) {
            str = String.format("Failed to get last error string, with error %d", ret2);
            return;            
        }
        
        return;
    }
}
