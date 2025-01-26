package es.udc.ws.app.model.course;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class SqlCourseDaoFactory {
    private final static String CLASS_NAME_PARAMETER = "SqlCourseDaoFactory.className";
    private static SqlCourseDao instaciaDao = null;

    private SqlCourseDaoFactory() { }

    private static SqlCourseDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlCourseDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     synchronized keyword means that only one thread is allowed
     at a particular time to complete a given task entirely
     */
    public synchronized static SqlCourseDao getDao() {
        if (instaciaDao == null) instaciaDao = getInstance();
        return instaciaDao;
    }
}
