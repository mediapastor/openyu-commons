package org.openyu.commons.druid;

import static org.junit.Assert.assertNotNull;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.BeforeClass;
import org.junit.Test;
import org.openyu.commons.commons.dbcp.BenchmarkDbcpTest;
import org.openyu.commons.junit.supporter.BenchmarkDatabaseTestSupporter;
import org.openyu.commons.lang.ArrayHelper;
import org.openyu.commons.lang.ByteHelper;
import org.openyu.commons.thread.ThreadHelper;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BenchmarkDruidTest extends BenchmarkDatabaseTestSupporter {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		applicationContext = new ClassPathXmlApplicationContext(new String[] { //
				"applicationContext-init.xml", //
				"org/openyu/commons/druid/testContext-druid.xml",//

		});
		// ---------------------------------------------------
		initialize();
		// ---------------------------------------------------
	}

	public static class BeanTest extends BenchmarkDruidTest {
		@Test
		public void dataSource() throws Exception {
			System.out.println(dataSource);
			assertNotNull(dataSource);
			//
			System.out.println(dataSource.getConnection());
		}

		@Test
		public void jdbcTemplate() {
			System.out.println(jdbcTemplate);
			assertNotNull(jdbcTemplate);
		}
	}

	// ---------------------------------------------------
	// optimized
	// ---------------------------------------------------
	public static class OptimizedTest extends BenchmarkDruidTest {

		@Test
		// insert: 10000 rows, 102400000 bytes / 29690 ms. = 3448.97 BYTES/MS,
		// 3368.14 K/S, 3.29 MB/S

		// 2015/10/09
		// insert: 10000 rows, 102628000 bytes / 93261 ms. = 1100.44 BYTES/MS,
		// 1074.65 K/S, 1.05 MB/S

		// 2016/06/13 pc
		// 10000 rows, 102628000 bytes / 35965 ms. = 2853.55 BYTES/MS, 2786.67
		// K/S, 2.72 MB/S
		public void insert() throws Exception {
			final int NUM_OF_THREADS = 100;
			final int NUM_OF_TIMES = 100;
			final int LENGTH_OF_BYTES = 10 * 1024;// 10k
			//
			final AtomicLong timesCounter = new AtomicLong(0);
			final AtomicLong byteCounter = new AtomicLong(0);
			// seq
			final AtomicLong seqCounter = new AtomicLong(1);
			//
			ExecutorService service = Executors.newFixedThreadPool(NUM_OF_THREADS);
			long beg = System.currentTimeMillis();
			for (int i = 0; i < NUM_OF_THREADS; i++) {
				//
				final String userId = "TEST_USER_" + i;
				service.submit(new Runnable() {
					//
					public void run() {
						try {
							//
							for (int i = 0; i < NUM_OF_TIMES; i++) {
								byte[] buff = ByteHelper.randomByteArray(LENGTH_OF_BYTES);
								try {
									StringBuilder sql = new StringBuilder();
									sql.append("insert into TEST_BENCHMARK (seq, id, info) ");
									sql.append("values (:seq, :id, :info)");

									long seq = seqCounter.getAndIncrement();
									// 0_0
									String newId = userId + "_" + i;
									// params
									Object[] params = new Object[] { seq, newId, new String(buff) };
									int inserted = jdbcTemplate.update(sql.toString(), params);

									System.out.println("I[" + userId + "] R[" + i + "], " + inserted);
									//
									if (inserted > 0) {
										timesCounter.incrementAndGet();
										byteCounter.addAndGet(ByteHelper.toByteArray(seq).length);
										byteCounter.addAndGet(ByteHelper.toByteArray(newId).length);
										byteCounter.addAndGet(buff.length);
									}
									//
									ThreadHelper.sleep(50);
								} catch (Exception ex) {
									ex.printStackTrace();
								} finally {
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
						}
						//
						ThreadHelper.sleep(3 * 1000);
					}
				});
				//
				ThreadHelper.sleep(50);
			}
			service.shutdown();
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
			//
			printResult(beg, byteCounter, timesCounter);
		}

		@Test
		// 10000 rows, 102400000 bytes / 20454 ms. = 5006.36 BYTES/MS,
		// 4889.02 K/S, 4.77 MB/S

		// 2015/10/09 nb
		// 10000 rows, 183462421 bytes / 35759 ms. = 5130.52 BYTES/MS,
		// 5010.28 K/S, 4.89 MB/S

		// 2015/10/12 pc
		// 10000 rows, 183473056 bytes / 16300 ms. = 11256.02 BYTES/MS, 10992.2
		// K/S, 10.73 MB/S

		// 2016/06/13 pc
		// 10000 rows, 183466793 bytes / 17713 ms. = 10357.75 BYTES/MS, 10114.99
		// K/S, 9.88 MB/S
		public void select() throws Exception {
			final int NUM_OF_THREADS = 100;
			final int NUM_OF_TIMES = 100;
			final int LENGTH_OF_BYTES = 10 * 1024;// 10k
			//
			final AtomicLong timesCounter = new AtomicLong(0);
			final AtomicLong byteCounter = new AtomicLong(0);
			// seq
			final AtomicLong seqCounter = new AtomicLong(1);
			//
			ExecutorService service = Executors.newFixedThreadPool(NUM_OF_THREADS);
			long beg = System.currentTimeMillis();
			for (int i = 0; i < NUM_OF_THREADS; i++) {
				//
				final String userId = "TEST_USER_" + i;
				service.submit(new Runnable() {
					//
					public void run() {
						try {
							//
							for (int i = 0; i < NUM_OF_TIMES; i++) {
								byte[] buff = ByteHelper.randomByteArray(LENGTH_OF_BYTES);
								try {
									StringBuilder sql = new StringBuilder();
									sql.append("select seq, id, info from TEST_BENCHMARK ");
									sql.append("where seq=:seq");

									long seq = seqCounter.getAndIncrement();
									// params
									Object[] params = new Object[] { seq };
									List<TestBenchmark> list = jdbcTemplate.query(sql.toString(), params,
											new TestBenchmarkRowMapper());
									//
									seq = 0;
									String id = null;
									String info = null;
									if (list.size() > 0) {
										TestBenchmark row = list.get(0);
										seq = row.getSeq();
										id = row.getId();
										info = row.getInfo();
									}
									System.out.println("I[" + id + "] R[" + i + "], " + seq);
									//
									if (seq > 0) {
										timesCounter.incrementAndGet();
										byteCounter.addAndGet(ByteHelper.toByteArray(seq).length);
										byteCounter.addAndGet(ByteHelper.toByteArray(id).length);
										byteCounter.addAndGet(ByteHelper.toByteArray(info).length);
									}
									//
									ThreadHelper.sleep(50);
								} catch (Exception ex) {
									ex.printStackTrace();
								} finally {
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
						}
						//
						ThreadHelper.sleep(3 * 1000);
					}
				});
				//
				ThreadHelper.sleep(50);
			}
			service.shutdown();
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
			//
			printResult(beg, byteCounter, timesCounter);
		}

		@Test
		// update: 10000 rows, 102400000 bytes / 34485 ms. = 2969.41 BYTES/MS,
		// 2899.81 K/S, 2.83 MB/S

		// 2015/10/09
		// update: 10000 rows, 102400000 bytes / 126711 ms. = 808.14 BYTES/MS,
		// 789.2 K/S, 0.77 MB/S

		// 2016/06/13 pc
		// 10000 rows, 102400000 bytes / 51571 ms. = 1985.61 BYTES/MS, 1939.07
		// K/S, 1.89 MB/S
		public void update() throws Exception {
			final int NUM_OF_THREADS = 100;
			final int NUM_OF_TIMES = 100;
			final int LENGTH_OF_BYTES = 10 * 1024;// 10k
			//
			final AtomicLong timesCounter = new AtomicLong(0);
			final AtomicLong byteCounter = new AtomicLong(0);
			// seq
			final AtomicLong seqCounter = new AtomicLong(1);
			//
			ExecutorService service = Executors.newFixedThreadPool(NUM_OF_THREADS);
			long beg = System.currentTimeMillis();
			for (int i = 0; i < NUM_OF_THREADS; i++) {
				//
				final String userId = "TEST_USER_" + i;
				service.submit(new Runnable() {
					//
					public void run() {
						try {
							//
							for (int i = 0; i < NUM_OF_TIMES; i++) {
								byte[] buff = ByteHelper.randomByteArray(LENGTH_OF_BYTES);
								byte[] prefix = ByteHelper.toByteArray("UPDATE_");
								buff = ArrayHelper.add(prefix,
										ByteHelper.getByteArray(buff, 0, buff.length - prefix.length));
								try {
									StringBuilder sql = new StringBuilder();
									sql.append("update TEST_BENCHMARK set info=:info ");
									sql.append("where seq=:seq");

									long seq = seqCounter.getAndIncrement();
									// params
									Object[] params = new Object[] { new String(buff), seq };
									int updated = jdbcTemplate.update(sql.toString(), params);

									System.out.println("I[" + userId + "] R[" + i + "], " + updated);
									//
									if (updated > 0) {
										timesCounter.incrementAndGet();
										byteCounter.addAndGet(buff.length);
									}
									//
									ThreadHelper.sleep(50);
								} catch (Exception ex) {
									ex.printStackTrace();
								} finally {
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
						}
						//
						ThreadHelper.sleep(3 * 1000);
					}
				});
				//
				ThreadHelper.sleep(50);
			}
			service.shutdown();
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
			//
			printResult(beg, byteCounter, timesCounter);
		}

		@Test
		// delete: 10000 rows, 102400000 bytes / 18315 ms. = 5591.05 BYTES/MS,
		// 5460.01 K/S, 5.33 MB/S

		// 2015/10/09
		// delete: 10000 rows, 102400000 bytes / 29794 ms. = 3436.93 BYTES/MS,
		// 3356.38 K/S, 3.28 MB/S

		// 2016/06/13 pc
		// 10000 rows, 102400000 bytes / 18816 ms. = 5442.18 BYTES/MS, 5314.63
		// K/S, 5.19 MB/S
		public void delete() throws Exception {
			final int NUM_OF_THREADS = 100;
			final int NUM_OF_TIMES = 100;
			final int LENGTH_OF_BYTES = 10 * 1024;// 10k
			//
			final AtomicLong timesCounter = new AtomicLong(0);
			final AtomicLong byteCounter = new AtomicLong(0);
			// seq
			final AtomicLong seqCounter = new AtomicLong(1);
			//
			ExecutorService service = Executors.newFixedThreadPool(NUM_OF_THREADS);
			long beg = System.currentTimeMillis();
			for (int i = 0; i < NUM_OF_THREADS; i++) {
				//
				final String userId = "TEST_USER_" + i;
				service.submit(new Runnable() {
					//
					public void run() {
						try {
							//
							for (int i = 0; i < NUM_OF_TIMES; i++) {
								byte[] buff = ByteHelper.randomByteArray(LENGTH_OF_BYTES);
								try {
									StringBuilder sql = new StringBuilder();
									sql.append("delete from TEST_BENCHMARK ");
									sql.append("where seq=:seq");

									long seq = seqCounter.getAndIncrement();
									// params
									Object[] params = new Object[] { seq };
									int deleted = jdbcTemplate.update(sql.toString(), params);

									System.out.println("I[" + userId + "] R[" + i + "], " + deleted);
									//
									if (deleted > 0) {
										timesCounter.incrementAndGet();
										byteCounter.addAndGet(buff.length);
									}
									//
									ThreadHelper.sleep(50);
								} catch (Exception ex) {
									ex.printStackTrace();
								} finally {
								}
							}
						} catch (Exception ex) {
							ex.printStackTrace();
						} finally {
						}
						//
						ThreadHelper.sleep(3 * 1000);
					}
				});
				//
				ThreadHelper.sleep(50);
			}
			service.shutdown();
			service.awaitTermination(Long.MAX_VALUE, TimeUnit.MINUTES);
			//
			printResult(beg, byteCounter, timesCounter);
		}
	}
}
