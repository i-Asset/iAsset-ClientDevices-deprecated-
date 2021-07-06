package at.srfg.conveyorbelt.opcua;

import java.util.List;

import at.srfg.conveyorbelt.ConveyorBelt;
import com.digitalpetri.opcua.stack.core.types.builtin.DataValue;
import com.digitalpetri.opcua.stack.core.types.builtin.NodeId;
import com.digitalpetri.opcua.stack.core.types.builtin.StatusCode;
import com.digitalpetri.opcua.stack.core.types.builtin.Variant;
import com.google.common.collect.Lists;

/*********************************************************************************************************
 * OPCUAWriter
 ********************************************************************************************************/
public class OPCUAWriter extends ClientBase {

    private final int m_fiNamespaceIndex = 2;
    private final int m_fiMoveBelt = 3;
    private final int m_fiSwitchBusyLight = 7;

    /*********************************************************************************************************
     * CTOR
     ********************************************************************************************************/
    public OPCUAWriter(String endpointUrl, ConveyorBelt belt) {
        super(endpointUrl, belt);
    }

    /*********************************************************************************************************
     * WriteSwitchLight
     ********************************************************************************************************/
    public void WriteSwitchLight(Variant val)
    {
        try {
            List<NodeId> nodeSwitchBusyLight = Lists.newArrayList(new NodeId(m_fiNamespaceIndex, m_fiSwitchBusyLight)); // SwitchBusyLight - bool
            List<DataValue> valueSwitchBusyLight = Lists.newArrayList(new DataValue(val, StatusCode.GOOD, null));
            m_client.writeValues(nodeSwitchBusyLight, valueSwitchBusyLight).thenAccept(status_codes -> {

                StatusCode status = status_codes.get(0);
                if (status.isGood()) {
                    // TODO
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*********************************************************************************************************
     * WriteMoveBelt
     ********************************************************************************************************/
    public void WriteMoveBelt(Variant val)
    {
        try {
            List<NodeId> nodeMoveBelt = Lists.newArrayList(new NodeId(m_fiNamespaceIndex, m_fiMoveBelt));  // MoveBelt - float
            List<DataValue> valueMoveBelt = Lists.newArrayList(new DataValue(new Variant("left"), StatusCode.GOOD, null),
                                                               new DataValue(val, StatusCode.GOOD, null));
            m_client.writeValues(nodeMoveBelt, valueMoveBelt).thenAccept(status_codes -> {

                    StatusCode status = status_codes.get(0);
                    if (status.isGood()) {
                        // TODO
                    }

            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
