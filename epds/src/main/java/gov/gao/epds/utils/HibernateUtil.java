package gov.gao.epds.utils;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

public class HibernateUtil {

	private static SessionFactory sessionFactory = null;

	private static SessionFactory configureSessionFactory()
			throws HibernateException {
		
		Configuration conf = new Configuration()
	              .configure();

		conf.configure("epds_Tables.cfg.xml");

	    ServiceRegistry sr = new StandardServiceRegistryBuilder().applySettings(conf.getProperties()).build();


	    sessionFactory = conf.buildSessionFactory(sr);


	    return sessionFactory;
	}

	static {
		configureSessionFactory();
	}

	private HibernateUtil() {
	}

	public static Session getSession() {
		return sessionFactory.getCurrentSession();
	}

}
