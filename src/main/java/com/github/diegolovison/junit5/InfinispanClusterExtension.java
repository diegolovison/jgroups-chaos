package com.github.diegolovison.junit5;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.diegolovison.infinispan.InfinispanCluster;
import com.github.diegolovison.os.ChaosProcessType;

public class InfinispanClusterExtension implements AfterEachCallback {

   public static final InfinispanClusterExtensionBuilder builder() {
      return new InfinispanClusterExtensionBuilder();
   }

   private InfinispanCluster infinispanCluster;

   @Override
   public void afterEach(ExtensionContext extensionContext) throws Exception {
      if (infinispanCluster != null) {
         infinispanCluster.disconnectAll();
      }
   }

   public InfinispanCluster infinispanCluster() {
      if (infinispanCluster == null) {
         infinispanCluster = new InfinispanCluster("ISPN", ChaosProcessType.SAME_VM);
      }
      return infinispanCluster;
   }

   public static class InfinispanClusterExtensionBuilder {

      public InfinispanClusterExtension build() {
         InfinispanClusterExtension clusterExtension = new InfinispanClusterExtension();
         return clusterExtension;
      }
   }
}
