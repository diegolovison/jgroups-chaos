package com.github.diegolovison.infinispan.junit5;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.diegolovison.infinispan.InfinispanCluster;
import com.github.diegolovison.os.ChaosProcessType;

public class InfinispanClusterExtension implements AfterEachCallback {

   public static final InfinispanClusterExtensionBuilder builder() {
      return new InfinispanClusterExtensionBuilder();
   }

   private InfinispanCluster infinispanCluster;

   private final ChaosProcessType processType;

   public InfinispanClusterExtension(ChaosProcessType processType) {
      this.processType = processType;
   }

   @Override
   public void afterEach(ExtensionContext extensionContext) throws Exception {
      if (infinispanCluster != null) {
         infinispanCluster.disconnectAll();
      }
   }

   public InfinispanCluster infinispanCluster() {
      if (infinispanCluster == null) {
         infinispanCluster = new InfinispanCluster(this.processType);
      }
      return infinispanCluster;
   }

   public static class InfinispanClusterExtensionBuilder {

      private ChaosProcessType processType;

      public InfinispanClusterExtensionBuilder() {
         this.processType = ChaosProcessType.SAME_VM;
      }

      public InfinispanClusterExtensionBuilder processType(ChaosProcessType processType) {
         this.processType = processType;
         return this;
      }

      public InfinispanClusterExtension build() {
         InfinispanClusterExtension clusterExtension = new InfinispanClusterExtension(this.processType);
         return clusterExtension;
      }
   }
}
