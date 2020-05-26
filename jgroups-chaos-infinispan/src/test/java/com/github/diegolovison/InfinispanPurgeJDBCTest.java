package com.github.diegolovison;

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import com.github.diegolovison.base.Node;
import com.github.diegolovison.base.failure.Failure;
import com.github.diegolovison.infinispan.InfinispanCluster;
import com.github.diegolovison.infinispan.InfinispanNode;
import com.github.diegolovison.infinispan.cache.ChaosCache;
import com.github.diegolovison.infinispan.junit5.InfinispanClusterExtension;
import com.github.diegolovison.junit5.MySQLContainerExtension;
import com.github.diegolovison.os.ChaosProcessType;

public class InfinispanPurgeJDBCTest {

   @RegisterExtension
   InfinispanClusterExtension clusterExtension = InfinispanClusterExtension.builder().processType(ChaosProcessType.SPAWN).build();

   @RegisterExtension
   MySQLContainerExtension mySqlExtension = MySQLContainerExtension.builder().build();

   @Test
   public void testSimple() throws SQLException, InterruptedException {

      Map<String, String> arguments = new HashMap<>();
      arguments.put("InfinispanPurgeJDBCDeadlockTest.testSimple.jdbcUrl", mySqlExtension.getJdbcUrl());

      // Given: the configuration
      String cacheName = "jdbcCache";
      InfinispanCluster cluster = clusterExtension.infinispanCluster();
      DataSource ds = mySqlExtension.getDataSource();

      // When: the data was added to the cache
      List<InfinispanNode> nodes = cluster.createNodes("ispn-config/infinispan-library-jdbc-config.xml", 2, arguments);
      InfinispanNode node1 = nodes.get(0);
      InfinispanNode node2 = nodes.get(1);

      // ISPN000562: Invalid cache loader configuration for 'JdbcStringBasedStoreConfiguration'.  If a cache loader is configured with purgeOnStartup, the cache loader cannot be shared in a cluster!
      ChaosCache cache1 = node1.getCache(cacheName);
      ChaosCache cache2 = node2.getCache(cacheName);
      node1.getCache(cacheName).clear();
      assertEquals(0, cache1.size());
      assertEquals(0, cache2.size());
      // ----

      int cacheSize = 100;
      for (int i = 0; i < cacheSize; i++) {
         cache1.put("foo" + i, "bar" + i);
      }
      performQuery(ds, "SELECT count(*) from ISPN_STRING_TABLE_" + cacheName, (rs) -> {
         try {
            rs.next();
            assertEquals("A basic SELECT query succeeds", cacheSize, rs.getInt(1));
         } catch (Exception e) {
            throw new IllegalStateException(e);
         }
      });

      cluster.createFailure(Failure.GCStopWorld, new Node[]{node1});

      // greater than <expiration lifespan="10000"/>
      Thread.sleep(20_000);

      cluster.solveFailure(Failure.GCStopWorld, new Node[]{node1});

      // it must be after the database reaper
      Thread.sleep(70_000);

      assertEquals(0, cache1.size());
      performQuery(ds, "SELECT count(*) from ISPN_STRING_TABLE_" + cacheName, (rs) -> {
         try {
            rs.next();
            assertEquals("A basic SELECT query succeeds", 0, rs.getInt(1));
         } catch (Exception e) {
            throw new IllegalStateException(e);
         }
      });
   }

   void performQuery(DataSource ds, String sql, Consumer<ResultSet> fn) throws SQLException {
      ResultSet resultSet = ds.getConnection().prepareStatement(sql).executeQuery();
      fn.accept(resultSet);
      resultSet.close();
   }
}
