package org.openyu.commons.service.impl;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.openyu.commons.junit.supporter.BaseTestSupporter;
import org.openyu.commons.service.AsyncService;

import com.carrotsearch.junitbenchmarks.BenchmarkOptions;
import com.carrotsearch.junitbenchmarks.BenchmarkRule;

public class AsyncServiceImplTest extends BaseTestSupporter {

	@Rule
	public BenchmarkRule benchmarkRule = new BenchmarkRule();

	private static AsyncService asyncService;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { //
				"applicationContext-init.xml", //
				"applicationContext-bean.xml", //
				"applicationContext-i18n.xml", //
				"applicationContext-database.xml", //
				"org/openyu/commons/service/testContext-service.xml", //

		});
		asyncService = (AsyncService) applicationContext.getBean("asyncService");
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 2, warmupRounds = 0, concurrency = 1)
	public void asyncService() {
		System.out.println(asyncService);
		assertNotNull(asyncService);
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void close() {
		System.out.println(asyncService);
		assertNotNull(asyncService);
		applicationContext.close();
		// 多次,不會丟出ex
		applicationContext.close();
	}

	@Test
	@BenchmarkOptions(benchmarkRounds = 1, warmupRounds = 0, concurrency = 1)
	public void refresh() {
		System.out.println(asyncService);
		assertNotNull(asyncService);
		applicationContext.refresh();
		// 多次,不會丟出ex
		applicationContext.refresh();
	}

}