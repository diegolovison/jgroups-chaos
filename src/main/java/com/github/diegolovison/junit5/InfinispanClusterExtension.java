package com.github.diegolovison.junit5;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.diegolovison.infinispan.InfinispanCluster;

public class InfinispanClusterExtension implements BeforeEachCallback, AfterEachCallback {

   public static final InfinispanClusterExtensionBuilder builder() {
      return new InfinispanClusterExtensionBuilder();
   }

   @Override
   public void afterEach(ExtensionContext extensionContext) throws Exception {

   }

   @Override
   public void beforeEach(ExtensionContext extensionContext) throws Exception {

   }

   public InfinispanCluster createCluster() {
      return new InfinispanCluster();
   }
}
