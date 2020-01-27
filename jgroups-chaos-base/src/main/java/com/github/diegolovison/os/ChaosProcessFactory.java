package com.github.diegolovison.os;

import java.io.IOException;

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

   public static int getAvailableServerSocket(int port) {
      int availablePort = 0;
      // is 100 enough ?
      // i = i + 2 ( 1st socket server, 2nd maybe the debug port )
      for (int i = 0; i < 100; i = i + 2) {
         port = port + i;
         SocketClient socketClient = new SocketClient();
         try {
            socketClient.startConnection(port);
            socketClient.sendMessage("ping");
         } catch (IllegalStateException | IOException e) {
            // not running
            if (availablePort == 0) {
               availablePort = port;
               break;
            }
         } finally {
            socketClient.stopConnection();
         }
      }
      if (availablePort == 0) {
         throw new IllegalStateException("Maybe too many tests running?");
      }
      return availablePort;
   }
}
