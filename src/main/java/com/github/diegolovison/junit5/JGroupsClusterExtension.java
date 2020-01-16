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
   private final String jGroupsXmlConfig;

   public JGroupsClusterExtension(String clusterName, ChaosProcessType processType, String jGroupsXmlConfig) {
      this.clusterName = clusterName;
      this.processType = processType;
      this.jGroupsXmlConfig = jGroupsXmlConfig;
   }

   @Override
   public void afterEach(ExtensionContext extensionContext) throws Exception {
      if (this.jGroupsCluster != null) {
         this.jGroupsCluster.disconnectAll();
      }
   }

   public JGroupsCluster jGroupsCluster() {
      if (this.jGroupsCluster == null) {
         this.jGroupsCluster = new JGroupsCluster(this.clusterName, this.processType, this.jGroupsXmlConfig);
      }
      return this.jGroupsCluster;
   }

   public static class JGroupsClusterExtensionBuilder {

      private ChaosProcessType processType;
      private String clusterName;
      private String jGroupsXmlConfig;

      public JGroupsClusterExtensionBuilder() {
         this.clusterName = UUID.randomUUID().toString();
         this.processType = ChaosProcessType.SAME_VM;
      }

      public JGroupsClusterExtensionBuilder clusterName(String clusterName) {
         this.clusterName = clusterName;
         return this;
      }

      public JGroupsClusterExtensionBuilder processType(ChaosProcessType processType) {
         this.processType = processType;
         return this;
      }

      public JGroupsClusterExtensionBuilder jGroupsXmlConfig(String jGroupsXmlConfig) {
         this.jGroupsXmlConfig = jGroupsXmlConfig;
         return this;
      }

      public JGroupsClusterExtension build() {
         JGroupsClusterExtension clusterExtension = new JGroupsClusterExtension(this.clusterName, this.processType, this.jGroupsXmlConfig);
         return clusterExtension;
      }
   }
}
