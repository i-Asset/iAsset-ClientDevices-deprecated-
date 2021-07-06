package at.srfg.conveyorbelt.opcua;

import com.digitalpetri.opcua.sdk.client.OpcUaClient;
import com.digitalpetri.opcua.sdk.client.api.config.OpcUaClientConfig;
import com.digitalpetri.opcua.sdk.client.api.identity.AnonymousProvider;
import com.digitalpetri.opcua.sdk.client.api.identity.IdentityProvider;
import com.digitalpetri.opcua.stack.client.UaTcpStackClient;
import com.digitalpetri.opcua.stack.core.Stack;
import com.digitalpetri.opcua.stack.core.security.SecurityPolicy;
import com.digitalpetri.opcua.stack.core.types.builtin.LocalizedText;
import com.digitalpetri.opcua.stack.core.types.structured.EndpointDescription;
import at.srfg.conveyorbelt.ConveyorBelt;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

import static com.digitalpetri.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;

public class ClientBase {

    protected ConveyorBelt m_belt;
    protected OpcUaClient m_client;
    private final KeyStoreLoader loader = new KeyStoreLoader();
    private final String endpointUrl;

    /*********************************************************************************************************
     * CTOR
     ********************************************************************************************************/
    public ClientBase(String endpointUrl, ConveyorBelt belt) {

        this.endpointUrl = endpointUrl;
        this.m_belt = belt;
    }

    /*********************************************************************************************************
     * createClient
     ********************************************************************************************************/
    private OpcUaClient createClient() throws Exception {

        EndpointDescription[] endpoints = UaTcpStackClient.getEndpoints(endpointUrl).get();

        EndpointDescription endpoint = Arrays.stream(endpoints)
                .filter(e -> e.getSecurityPolicyUri().equals(getSecurityPolicy().getSecurityPolicyUri()))
                .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));


        //loader.load();

        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setApplicationName(LocalizedText.english("digitalpetri opc-ua client"))
                .setApplicationUri("urn:digitalpetri:opcua:client")
                //.setCertificate(loader.getClientCertificate())
                //.setKeyPair(loader.getClientKeyPair())
                .setEndpoint(endpoint)
                .setIdentityProvider(getIdentityProvider())
                .setRequestTimeout(uint(5000))
                .build();

        return new OpcUaClient(config);
    }

    /*********************************************************************************************************
     * ConnectClient
     ********************************************************************************************************/
    public void ConnectClient()
    {
        try {
            m_client = createClient();
        } catch (Throwable t) {
        }

        //try {
        //    Thread.sleep(999999999);
        //} catch (InterruptedException e) {
        //    e.printStackTrace();
        //}
    }

    /*********************************************************************************************************
     * DisconnectClient
     ********************************************************************************************************/
    public void DisconnectClient()
    {
        if (m_client != null) {
            try {
                m_client.disconnect().get();
                Stack.releaseSharedResources();
            } catch (InterruptedException | ExecutionException e) {
            }
        } else {
            Stack.releaseSharedResources();
        }
    }

    /*********************************************************************************************************
     * others...
     ********************************************************************************************************/
    public SecurityPolicy getSecurityPolicy() { return SecurityPolicy.None; }
    public IdentityProvider getIdentityProvider() {
        return new AnonymousProvider();
    }
}

