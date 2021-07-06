package at.srfg.conveyorbelt.opcua;

import at.srfg.conveyorbelt.ConveyorBelt;
import com.digitalpetri.opcua.stack.core.types.builtin.Variant;

// see reference implementation:
// https://github.com/kevinherron/opc-ua-sdk-examples/tree/master/src/main/java/com/digitalpetri/opcua/sdk/examples/client

// see online SRFG wiki doku:
// https://secure.salzburgresearch.at/wiki/display/IoT/OPC-UA

/*********************************************************************************************************
 * OPCUAManager
 ********************************************************************************************************/
@SuppressWarnings("ALL")
public class OPCUAManager {

    public enum WriteLocation
    {
        Switchlight,
        MoveBelt
    }

    Thread m_reader_thread;
    OPCUAReader m_reader;
    OPCUAWriter m_writer;
    OPCUAMethodCall opcuaMethodCall;

    String endpointUrl = "opc.tcp://192.168.48.42:4840/";
    //String endpointIP = "192.168.48.42";

    /*********************************************************************************************************
     * CTOR
     ********************************************************************************************************/
    public OPCUAManager(ConveyorBelt belt)
    {
        this.m_reader = new OPCUAReader(endpointUrl, belt);
        this.m_writer = new OPCUAWriter(endpointUrl, belt);
        this.opcuaMethodCall = new OPCUAMethodCall(endpointUrl, belt);

        this.m_reader.ConnectClient();
        this.m_writer.ConnectClient();
        this.opcuaMethodCall.ConnectClient();
    }

    /*********************************************************************************************************
     * StartReadThread
     ********************************************************************************************************/
    public void startReadThread()
    {
        m_reader_thread = new Thread(new Runnable()
        {
            public void run()
            {
                while(true) {
                    try {
                        m_reader.ReadValues();
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        m_reader_thread.start();
    }

    /*********************************************************************************************************
     * WriteValue
     ********************************************************************************************************/
    public void WriteValue(WriteLocation loc, Variant val)
    {
        if(loc == WriteLocation.Switchlight)
        {
            m_writer.WriteSwitchLight(val);
        }
        else if(loc == WriteLocation.MoveBelt)
        {
            m_writer.WriteMoveBelt(val);
        }
    }

    /*********************************************************************************************************
     * callMethod
     ********************************************************************************************************/
    public void callMethod(WriteLocation method, Variant[] val)
    {
        if(method == WriteLocation.Switchlight)
        {
            opcuaMethodCall.callSwitchBusyLight(val[0]);
        }
        else if(method == WriteLocation.MoveBelt)
        {
            opcuaMethodCall.callMoveBelt(val[0], val[1]);
        }
    }
    /*********************************************************************************************************
     * StopThreads
     ********************************************************************************************************/
    public void stopReadThread()
    {
        m_reader_thread.stop();
        m_reader.DisconnectClient();
        m_writer.DisconnectClient();
        opcuaMethodCall.DisconnectClient();
    }
}
