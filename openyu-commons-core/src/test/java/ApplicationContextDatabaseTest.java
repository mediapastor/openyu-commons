import static org.junit.Assert.assertNotNull;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.jdbc.Work;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.orm.hibernate4.HibernateTransactionManager;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

import org.openyu.commons.dao.supporter.CommonDaoSupporter;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.commons.service.CommonService;

public class ApplicationContextDatabaseTest extends BaseTestSupporter {

	@Rule
	public BenchmarkRule benchmarkRule = new BenchmarkRule();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { //
				"applicationContext-init.xml", //
				"applicationContext-bean.xml", //
				"applicationContext-database.xml",//
		});
	}

	@Test
	public void commonDataSource() throws Exception {
		DataSource bean = (DataSource) applicationContext.getBean("commonDataSource");
		System.out.println(bean);
		assertNotNull(bean);
		//
		System.out.println("connection: " + bean.getConnection());
		System.out.println("autoCommit: " + bean.getConnection().getAutoCommit());
		System.out.println("transactionIsolation: " + bean.getConnection().getTransactionIsolation());
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 100, warmupRounds = 0, concurrency = 100)
	public void openConnection() throws Exception {
		DataSource bean = (DataSource) applicationContext.getBean("commonDataSource");
		//
		try {
			Connection conn = bean.getConnection();
			if (conn != null) {
				System.out.println("counter: " + counter.incrementAndGet() + ", " + conn);
			}
		} catch (Exception ex) {
			// System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	@Test
	public void commonSessionFactory() throws Exception {
		SessionFactory bean = (SessionFactory) applicationContext.getBean("commonSessionFactory");
		System.out.println(bean);
		assertNotNull(bean);
		//
		Session session = bean.openSession();
		session.doWork(new Work() {
			public void execute(Connection connection) throws SQLException {
				System.out.println("connection: " + connection);
				System.out.println("autoCommit: " + connection.getAutoCommit());
				System.out.println("transactionIsolation: " + connection.getTransactionIsolation());
			}
		});
	}

	@Test
	public void commonHibernateTemplate() {
		HibernateTemplate bean = (HibernateTemplate) applicationContext.getBean("commonHibernateTemplate");
		System.out.println(bean);
		assertNotNull(bean);
	}

	// @Test
	// public void commonTxAdvice() {
	// TransactionInterceptor bean = (TransactionInterceptor)
	// applicationContext.getBean("commonTxAdvice");
	// System.out.println(bean);
	// assertNotNull(bean);
	// }

	@Test
	public void commonTx() {
		HibernateTransactionManager bean = (HibernateTransactionManager) applicationContext.getBean("commonTx");
		System.out.println(bean);
		assertNotNull(bean);
	}

	@Test
	public void commonDaoSupporter() {
		CommonDaoSupporter bean = (CommonDaoSupporter) applicationContext.getBean("commonDaoSupporter");
		System.out.println(bean);
		assertNotNull(bean);
	}

	@Test
	public void commonServiceSupporter() {
		CommonService bean = (CommonService) applicationContext.getBean("commonServiceSupporter");
		System.out.println(bean);
		assertNotNull(bean);
	}
}
