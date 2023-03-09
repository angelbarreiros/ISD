package es.udc.ws.app.model.reply;

import es.udc.ws.util.configuration.ConfigurationParametersManager;

public class SqlReplyDaoFactory {
    private final static String CLASS_NAME_PARAMETER = "SqlReplyDaoFactory.className";
    private static SqlReplyDao dao = null;

    private SqlReplyDaoFactory() {
    }

    @SuppressWarnings("rawtypes")
    private static SqlReplyDao getInstance() {
        try {
            String daoClassName = ConfigurationParametersManager
                    .getParameter(CLASS_NAME_PARAMETER);
            Class daoClass = Class.forName(daoClassName);
            return (SqlReplyDao) daoClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public synchronized static SqlReplyDao getDao() {

        if (dao == null) {
            dao = getInstance();
        }
        return dao;

    }
}
