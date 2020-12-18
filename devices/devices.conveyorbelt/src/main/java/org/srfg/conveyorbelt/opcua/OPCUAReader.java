package org.srfg.conveyorbelt.opcua;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.digitalpetri.opcua.sdk.client.OpcUaClient;
import com.digitalpetri.opcua.stack.core.types.builtin.DataValue;
import com.digitalpetri.opcua.stack.core.types.builtin.NodeId;
import com.digitalpetri.opcua.stack.core.types.enumerated.TimestampsToReturn;
import com.google.common.collect.Lists;
import org.srfg.conveyorbelt.ConveyorBelt;

/*********************************************************************************************************
 * OPCUAReader
 ********************************************************************************************************/
public class OPCUAReader extends ClientBase {

    private final int m_fiNamespaceIndex = 2;
    //private final int m_fiServerTime = 2;
    private final int m_fiConBeltState = 5;
    private final int m_fiConBeltDist = 6;
    private final int m_fiConBeltMoving = 7;

    /*********************************************************************************************************
     * CTOR
     ********************************************************************************************************/
    public OPCUAReader(String endpointUrl, ConveyorBelt belt) {
        super(endpointUrl, belt);
    }

    /*********************************************************************************************************
     * ReadValues
     ********************************************************************************************************/
    public void ReadValues()
    {
        // asynchronous read request
        readServerStateAndTime(m_client).thenAccept(values -> {
            DataValue varConBeltState = values.get(0); // ConBeltState
            DataValue varConBeltDist = values.get(1); // ConBeltDist
            DataValue varConBeltMoving = values.get(2); // ConBeltMoving
            m_belt.setBeltState((String) varConBeltState.getValue().getValue());
            m_belt.setBeltDist((String) varConBeltDist.getValue().getValue());
            m_belt.setBeltMoving((boolean) varConBeltMoving.getValue().getValue());
        });
    }

    /*********************************************************************************************************
     * readServerStateAndTime
     ********************************************************************************************************/
    private CompletableFuture<List<DataValue>> readServerStateAndTime(OpcUaClient client) {
        List<NodeId> nodeIds = Lists.newArrayList(//new NodeId(m_fiNamespaceIndex, m_fiServerTime), // ServerTime - datetime
                                                  new NodeId(m_fiNamespaceIndex, m_fiConBeltState), // ConBeltState - string
                                                  new NodeId(m_fiNamespaceIndex, m_fiConBeltDist), // ConBeltDist - float
                                                  new NodeId(m_fiNamespaceIndex, m_fiConBeltMoving));  // ConBeltMoving - bool

        return m_client.readValues(0.0, TimestampsToReturn.Both, nodeIds);
    }
}
