package es.udc.ws.app.model.inscription;
import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class SqlInscriptionDaoFactory {
    private final static String CLASS_NAME_PARAMETER = "SqlInscriptionDaoFactory.className";
    private static SqlInscriptionDao instaciaDao = null;

    private SqlInscriptionDaoFactory() { }

    private static SqlInscriptionDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager.getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlInscriptionDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /*
     synchronized keyword means that only one thread is allowed
     at a particular time to complete a given task entirely
     */
    public synchronized static SqlInscriptionDao getDao() {
        if (instaciaDao == null) instaciaDao = getInstance();
        return instaciaDao;
    }
}
