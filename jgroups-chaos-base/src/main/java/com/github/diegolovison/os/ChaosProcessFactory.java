package com.github.diegolovison.os;

import java.io.IOException;
import java.net.ServerSocket;

public class ChaosProcessFactory {

   public static ChaosProcess createInstance(ChaosProcessFramework framework, ChaosProcessType processType) {
      String typeProperty = System.getProperty("jgroups-chaos.ChaosProcessType");
      if (typeProperty != null && !typeProperty.isBlank()) {
         processType = ChaosProcessType.valueOf(typeProperty);
      }
      if (ChaosProcessFramework.JGROUPS.equals(framework)) {
         if (ChaosProcessType.SAME_VM.equals(processType)) {
            return instance("com.github.diegolovison.jgroups.JGroupsChaosProcessSameVM");
         } else if (ChaosProcessType.SPAWN.equals(processType)) {
            return instance("com.github.diegolovison.jgroups.JGroupsChaosProcessSpawn");
         } else {
            throw new UnsupportedOperationException();
         }
      } else if (ChaosProcessFramework.INFINISPAN.equals(framework)) {
         if (ChaosProcessType.SAME_VM.equals(processType)) {
            return instance("com.github.diegolovison.infinispan.InfinispanEmbeddedChaosProcessSameVM");
         } else if (ChaosProcessType.SPAWN.equals(processType)) {
            return instance("com.github.diegolovison.infinispan.InfinispanEmbeddedChaosProcessSpawn");
         } else if (ChaosProcessType.LOCAL_SERVER.equals(processType)) {
            return instance("com.github.diegolovison.infinispan.InfinispanRemoteChaosProcessSameVM");
         } else {
            throw new UnsupportedOperationException();
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }

   private static ChaosProcess instance(String className) {
      try {
         return (ChaosProcess) Class.forName(className).newInstance();
      } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
         throw new IllegalStateException(e);
      }
   }

   public static int getAvailableServerSocket() {
      try {
         int availablePort;
         try (ServerSocket s = new ServerSocket(0)) {
            availablePort = s.getLocalPort();
         }
         return availablePort;
      } catch (IOException e) {
         throw new IllegalStateException("Cannot find available port", e);
      }
   }
}
