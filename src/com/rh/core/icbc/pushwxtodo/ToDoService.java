package com.rh.core.icbc.pushwxtodo;

import java.net.MalformedURLException;
import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.WebServiceFeature;

import com.rh.core.base.Context;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.9-b130926.1035
 * Generated source version: 2.2
 * 
 */
@WebServiceClient(name = "ToDoService", targetNamespace = "http://webservice.iipa/")
public class ToDoService
    extends Service
{

    protected final static URL TODOSERVICE_WSDL_LOCATION;
    private final static WebServiceException TODOSERVICE_EXCEPTION;
    private final static QName TODOSERVICE_QNAME = new QName("http://webservice.iipa/", "ToDoService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL(Context.getSyConf("PE_WX_TODO_ADDRESS", "http://122.18.109.102:8000")+"/_layouts/iipa/personal/todoservice.asmx?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        TODOSERVICE_WSDL_LOCATION = url;
        TODOSERVICE_EXCEPTION = e;
    }

    public ToDoService() {
        super(__getWsdlLocation(), TODOSERVICE_QNAME);
    }

//    public ToDoService(WebServiceFeature... features) {
//        super(__getWsdlLocation(), TODOSERVICE_QNAME, features);
//    }

    public ToDoService(URL wsdlLocation) {
        super(wsdlLocation, TODOSERVICE_QNAME);
    }

//    public ToDoService(URL wsdlLocation, WebServiceFeature... features) {
//        super(wsdlLocation, TODOSERVICE_QNAME, features);
//    }

    public ToDoService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

//    public ToDoService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
//        super(wsdlLocation, serviceName, features);
//    }

    /**
     * 
     * @return
     *     returns ToDoServiceSoap
     */
    @WebEndpoint(name = "ToDoServiceSoap")
    public ToDoServiceSoap getToDoServiceSoap() {
        return super.getPort(new QName("http://webservice.iipa/", "ToDoServiceSoap"), ToDoServiceSoap.class);
    }

    /**
     * 
     * @param features
     *     A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return
     *     returns ToDoServiceSoap
     */
    @WebEndpoint(name = "ToDoServiceSoap")
    public ToDoServiceSoap getToDoServiceSoap(WebServiceFeature... features) {
        return super.getPort(new QName("http://webservice.iipa/", "ToDoServiceSoap"), ToDoServiceSoap.class, features);
    }

    private static URL __getWsdlLocation() {
        if (TODOSERVICE_EXCEPTION!= null) {
            throw TODOSERVICE_EXCEPTION;
        }
        return TODOSERVICE_WSDL_LOCATION;
    }

}
