package es.udc.ws.app.client.service;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

import java.lang.reflect.InvocationTargetException;

public class ClientAppServiceFactory {

    private final static String CLASS_NAME_PARAMETER
            = "ClientAppServiceFactory.className";
    private static Class<ClientAppService> serviceClass = null;

    private ClientAppServiceFactory() {
    }

    @SuppressWarnings("unchecked")
    private synchronized static Class<ClientAppService> getServiceClass() {

        if (serviceClass == null) {
            try {
                String serviceClassName = ConfigurationParametersManager
                        .getParameter(CLASS_NAME_PARAMETER);
                serviceClass = (Class<ClientAppService>) Class.forName(serviceClassName);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return serviceClass;

    }

    public static ClientAppService getService() {

        try {
            return (ClientAppService) getServiceClass().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }

    }
}

