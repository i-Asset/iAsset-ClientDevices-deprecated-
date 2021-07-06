package at.srfg.conveyorbelt.opcua;

import at.srfg.conveyorbelt.ConveyorBelt;
import com.digitalpetri.opcua.stack.core.UaException;
import com.digitalpetri.opcua.stack.core.types.builtin.NodeId;
import com.digitalpetri.opcua.stack.core.types.builtin.StatusCode;
import com.digitalpetri.opcua.stack.core.types.builtin.Variant;
import com.digitalpetri.opcua.stack.core.types.structured.CallMethodRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/*********************************************************************************************************
 * OPCUAWriter
 ********************************************************************************************************/
public class OPCUAMethodCall extends ClientBase {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String CONVEYOR_BELT_NODEID = "ns=2;i=1";
    private final String SWITCH_BUSY_LIGHT_METHOD_NODEID = "ns=2;i=7";
    private final String MOVE_BELT_NODEID = "ns=2;i=3";

    /*********************************************************************************************************
     * CTOR
     ********************************************************************************************************/
    public OPCUAMethodCall(String endpointUrl, ConveyorBelt belt) {
        super(endpointUrl, belt);
    }

    /*********************************************************************************************************
     * callSwitchLight
     ********************************************************************************************************/
    public void callSwitchBusyLight(Variant val)
    {
        try {
            final CompletableFuture<Boolean> booleanCompletableFuture = pCallSwitchBusyLight(val);
            while (!booleanCompletableFuture.isDone()) {
                Thread.sleep(200);
                logger.debug("wait for method call finish");
            }
            logger.info("call result: {}", booleanCompletableFuture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*********************************************************************************************************
     * callMoveBelt
     ********************************************************************************************************/
    public void callMoveBelt(Variant direction, Variant distance)
    {
        try {
            final CompletableFuture<Boolean> booleanCompletableFuture = pCallMoveBelt(direction, distance);
            while (!booleanCompletableFuture.isDone()) {
                Thread.sleep(200);
                logger.debug("wait for method call finish");
            }
            logger.info("call result: {}", booleanCompletableFuture);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CompletableFuture<Boolean> pCallSwitchBusyLight(Variant input) {
        NodeId objectId = NodeId.parse(CONVEYOR_BELT_NODEID);
        NodeId methodId = NodeId.parse(SWITCH_BUSY_LIGHT_METHOD_NODEID);

        CallMethodRequest request = new CallMethodRequest(
                objectId, methodId, new Variant[]{input});
        logger.debug("request={}", request);

        return m_client.call(request).thenCompose(result -> {
            StatusCode statusCode = result.getStatusCode();

            logger.debug("statusCode={}", statusCode);
            if (statusCode.isGood()) {
                Boolean value = (Boolean) result.getOutputArguments()[0].getValue();
                logger.debug("value={}", value);
                return CompletableFuture.completedFuture(value);
            } else {
                CompletableFuture<Boolean> f = new CompletableFuture<>();
                logger.error("exception status={}", statusCode);
                f.completeExceptionally(new UaException(statusCode));
                return f;
            }
        });
    }

    private CompletableFuture<Boolean> pCallMoveBelt(Variant direction, Variant distance) {
        NodeId objectId = NodeId.parse(CONVEYOR_BELT_NODEID);
        NodeId methodId = NodeId.parse(MOVE_BELT_NODEID);

        CallMethodRequest request = new CallMethodRequest(
                objectId, methodId, new Variant[]{direction, distance});
        logger.debug("request={}", request);

        return m_client.call(request).thenCompose(result -> {
            StatusCode statusCode = result.getStatusCode();

            logger.debug("statusCode={}", statusCode);
            if (statusCode.isGood()) {
                Boolean value = (Boolean) result.getOutputArguments()[0].getValue();
                logger.debug("value={}", value);
                return CompletableFuture.completedFuture(value);
            } else {
                CompletableFuture<Boolean> f = new CompletableFuture<>();
                logger.error("exception status={}", statusCode);
                f.completeExceptionally(new UaException(statusCode));
                return f;
            }
        });
    }
}
