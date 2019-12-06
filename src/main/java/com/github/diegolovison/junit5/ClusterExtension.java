package com.github.diegolovison.junit5;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.diegolovison.jgroups.Cluster;

public class ClusterExtension implements BeforeEachCallback, AfterEachCallback {

   public static final ClusterExtensionBuilder builder() {
      return new ClusterExtensionBuilder();
   }

   @Override
   public void afterEach(ExtensionContext extensionContext) throws Exception {
      System.out.println(extensionContext);
   }

   @Override
   public void beforeEach(ExtensionContext extensionContext) throws Exception {
      System.out.println(extensionContext);
   }

   public Cluster getCluster() {
      return new Cluster();
   }
}
