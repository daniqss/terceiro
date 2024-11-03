package es.udc.ws.app.model.curso;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class SqlCursoDaoFactory {
    private final static String CLASS_NAME_PARAMETER = "SqlCursoDaoFactory.className";
    private static SqlCursoDao instaciaDao = null;

    private SqlCursoDaoFactory() { }

    private static SqlCursoDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlCursoDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /*
     synchronized keyword means that only one thread is allowed
     at a particular time to complete a given task entirely
     */
    public synchronized static SqlCursoDao getDao() {
        if (instaciaDao == null) instaciaDao = getInstance();
        return instaciaDao;
    }
}
