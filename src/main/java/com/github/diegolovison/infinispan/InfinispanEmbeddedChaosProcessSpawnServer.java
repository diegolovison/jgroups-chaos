package com.github.diegolovison.infinispan;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

import org.jgroups.stack.Protocol;
import org.jgroups.util.UUID;

import com.github.diegolovison.base.ChaosConfig;
import com.github.diegolovison.infinispan.cache.ChaosCache;
import com.github.diegolovison.jgroups.ProcessSpawn;
import com.github.diegolovison.os.Response;
import com.github.diegolovison.os.SocketClientHandler;
import com.github.diegolovison.os.SocketServer;

public class InfinispanEmbeddedChaosProcessSpawnServer extends SocketServer {

   static {
      instanceClass = InfinispanEmbeddedChaosProcessSpawnServer.class;
   }

   private InfinispanEmbeddedChaosProcessSameVM vm;

   public InfinispanEmbeddedChaosProcessSpawnServer() {
      this.vm = new InfinispanEmbeddedChaosProcessSameVM();
   }

   private String getNumberOfMembers(String[] args) {
      return String.valueOf(vm.getNumberOfMembers());
   }

   private String getClusterName(String[] args) {
      return this.vm.getClusterName();
   }

   private String isRunning(String[] args) {
      return String.valueOf(this.vm.isRunning());
   }

   private String isCoordinator(String[] args) {
      return String.valueOf(this.vm.isCoordinator());
   }

   private String insertProtocol(String[] args) {
      return ProcessSpawn.insertProtocol(this.vm, args);
   }

   private String removeProtocol(String[] args) {
      try {
         this.vm.removeProtocol((Class<? extends Protocol>) Class.forName(args[0]));
      } catch (ClassNotFoundException e) {
         throw new IllegalStateException(e);
      }
      return null;
   }

   private String disconnect(String[] args) {
      this.vm.disconnect();
      this.stop();
      return null;
   }

   private String getAddress(String[] args) {
      return ((UUID) vm.getAddress()).toStringLong();
   }

   private String getCache(String[] args) {
      String cacheName = args[0];
      ChaosCache cache = vm.getCache(cacheName);
      if (cache != null) {
         return cacheName;
      } else {
         return null;
      }
   }

   private String waitForClusterToForm(String[] args) {
      this.vm.waitForClusterToForm(Integer.valueOf(args[0]));
      return null;
   }

   private String getCachePut(String[] args) {
      String cacheName = args[0];
      ChaosCache cache = vm.getCache(cacheName);
      if (cache != null) {
         return cache.put(args[1], args[2]);
      } else {
         return null;
      }
   }

   private String getCacheGet(String[] args) {
      String cacheName = args[0];
      ChaosCache cache = vm.getCache(cacheName);
      if (cache != null) {
         return cache.get(args[1]);
      } else {
         return null;
      }
   }

   private String getCacheIsConflictResolutionInProgress(String[] args) {
      String cacheName = args[0];
      ChaosCache cache = vm.getCache(cacheName);
      if (cache != null) {
         return String.valueOf(cache.isConflictResolutionInProgress());
      } else {
         return null;
      }
   }

   private String getCacheIsStateTransferInProgress(String[] args) {
      String cacheName = args[0];
      ChaosCache cache = vm.getCache(cacheName);
      if (cache != null) {
         return String.valueOf(cache.isStateTransferInProgress());
      } else {
         return null;
      }
   }

   private String getCacheSize(String[] args) {
      String cacheName = args[0];
      ChaosCache cache = vm.getCache(cacheName);
      if (cache != null) {
         return String.valueOf(cache.size());
      } else {
         return null;
      }
   }

   @Override
   protected void onStart(String[] args) {
      File configFile = new File(args[1]);
      InfinispanChaosConfig config;
      try (FileInputStream inputStream = new FileInputStream(configFile)) {
         config = ChaosConfig.ChaosConfigMarshaller.fromStream(inputStream);
      } catch (FileNotFoundException e) {
         throw new IllegalStateException(e);
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }
      this.vm.run(config);
   }

   @Override
   protected SocketClientHandler createSocketClientHandler(Socket s) throws IOException {
      return new SocketClientHandler(s) {
         @Override
         protected Response doRequest(String method, String[] args) {
            Response response = super.doRequest(method, args);
            if (Response.EMPTY_RESPONSE.equals(response)) {
               if ("getNumberOfMembers".equals(method)) {
                  return new Response(getNumberOfMembers(args));
               } else if ("getClusterName".equals(method)) {
                  return new Response(getClusterName(args));
               } else if ("isRunning".equals(method)) {
                  return new Response(isRunning(args));
               } else if ("isCoordinator".equals(method)) {
                  return new Response(isCoordinator(args));
               } else if ("insertProtocol".equals(method)) {
                  return new Response(insertProtocol(args));
               } else if ("removeProtocol".equals(method)) {
                  return new Response(removeProtocol(args));
               } else if ("disconnect".equals(method)) {
                  return new Response(disconnect(args));
               } else if ("getAddress".equals(method)) {
                  return new Response(getAddress(args));
               } else if ("getCache".equals(method)) {
                  return new Response(getCache(args));
               } else if ("waitForClusterToForm".equals(method)) {
                  return new Response(waitForClusterToForm(args));
               } else if ("getCachePut".equals(method)) {
                  return new Response(getCachePut(args));
               } else if ("getCacheGet".equals(method)) {
                  return new Response(getCacheGet(args));
               } else if ("getCacheIsConflictResolutionInProgress".equals(method)) {
                  return new Response(getCacheIsConflictResolutionInProgress(args));
               } else if ("getCacheIsStateTransferInProgress".equals(method)) {
                  return new Response(getCacheIsStateTransferInProgress(args));
               } else if ("getCacheSize".equals(method)) {
                  return new Response(getCacheSize(args));
               }
            }
            return response;
         }
      };
   }
}
