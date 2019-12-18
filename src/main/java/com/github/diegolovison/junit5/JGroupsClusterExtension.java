package com.github.diegolovison.junit5;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import com.github.diegolovison.jgroups.JGroupsCluster;

public class JGroupsClusterExtension implements BeforeEachCallback, AfterEachCallback {

   public static final JGroupsClusterExtensionBuilder builder() {
      return new JGroupsClusterExtensionBuilder();
   }

   @Override
   public void afterEach(ExtensionContext extensionContext) throws Exception {

   }

   @Override
   public void beforeEach(ExtensionContext extensionContext) throws Exception {

   }

   public JGroupsCluster createCluster() {
      return new JGroupsCluster();
   }
}
