package com.github.diegolovison.junit5;

import java.util.UUID;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.diegolovison.jgroups.JGroupsCluster;
import com.github.diegolovison.os.ChaosProcessType;

public class JGroupsClusterExtension implements AfterEachCallback {

   public static final JGroupsClusterExtensionBuilder builder() {
      return new JGroupsClusterExtensionBuilder();
   }

   private JGroupsCluster jGroupsCluster;

   private final String clusterName;
   private final ChaosProcessType processType;

   public JGroupsClusterExtension(String clusterName, ChaosProcessType processType) {
      this.clusterName = clusterName;
      this.processType = processType;
   }

   @Override
   public void afterEach(ExtensionContext extensionContext) throws Exception {
      if (jGroupsCluster != null) {
         jGroupsCluster.disconnectAll();
      }
   }

   public JGroupsCluster jGroupsCluster() {
      if (jGroupsCluster == null) {
         jGroupsCluster = new JGroupsCluster(clusterName, this.processType);
      }
      return jGroupsCluster;
   }

   public static class JGroupsClusterExtensionBuilder {

      private ChaosProcessType processType;
      private String clusterName;

      public JGroupsClusterExtensionBuilder() {
         this.clusterName = UUID.randomUUID().toString();
      }

      public JGroupsClusterExtensionBuilder clusterName(String clusterName) {
         this.clusterName = clusterName;
         return this;
      }

      public JGroupsClusterExtensionBuilder processType(ChaosProcessType processType) {
         this.processType = processType;
         return this;
      }

      public JGroupsClusterExtension build() {
         JGroupsClusterExtension clusterExtension = new JGroupsClusterExtension(this.clusterName, this.processType);
         return clusterExtension;
      }
   }
}
