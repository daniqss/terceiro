package es.udc.ws.app.model.courseservice;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class CourseServiceFactory {
    private static final String CLASS_NAME_PARAMETER = "CourseServiceFactory.className";
    private static CourseService instanceDao = null;

    private CourseServiceFactory() { }

    public static CourseService getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);
            Class<?> daoClass = Class.forName(daoClassName);
            return (CourseService) daoClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("CourseService implementation not found", e);
        } catch (Exception e) {
            throw new RuntimeException("Error creating CourseService instance", e);
        }
    }

    public synchronized static CourseService getService() {
        if (instanceDao == null) {
            instanceDao = getInstance();
        }
        return instanceDao;
    }
}
