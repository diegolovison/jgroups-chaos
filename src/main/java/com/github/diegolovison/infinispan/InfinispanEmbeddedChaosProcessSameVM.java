package com.github.diegolovison.infinispan;

import java.io.IOException;
import java.util.Map;

import org.infinispan.Cache;
import org.infinispan.conflict.ConflictManager;
import org.infinispan.conflict.ConflictManagerFactory;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.stack.Protocol;

import com.github.diegolovison.infinispan.cache.ChaosCache;
import com.github.diegolovison.os.Eventually;

public class InfinispanEmbeddedChaosProcessSameVM extends InfinispanChaosProcess {

   private EmbeddedCacheManager cacheManager;
   private long pid;

   @Override
   public InfinispanEmbeddedChaosProcessSameVM run(InfinispanChaosConfig config) {
      try {
         Map<String, String> arguments = config.getArguments();
         if (arguments != null) {
            for (String key : arguments.keySet()) {
               System.setProperty(key, arguments.get(key));
            }
         }
         this.cacheManager = new DefaultCacheManager(config.getConfigFile());
         this.pid = ProcessHandle.current().pid();
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }
      return this;
   }

   @Override
   public int getNumberOfMembers() {
      return this.cacheManager.getMembers().size();
   }

   @Override
   public String getClusterName() {
      return this.cacheManager.getClusterName();
   }

   @Override
   public boolean isRunning() {
      return getJChannel().isConnected();
   }

   @Override
   public boolean isCoordinator() {
      return cacheManager.isCoordinator();
   }

   @Override
   public void removeProtocol(Class<? extends Protocol> protocolClass) {
      this.getJChannel().getProtocolStack().removeProtocol(protocolClass);
   }

   @Override
   public void disconnect() {
      this.cacheManager.stop();
   }

   @Override
   public ChaosCache getCache(String cacheName) {
      Cache<String, String> cache = this.cacheManager.getCache(cacheName);
      if (cache != null) {
         return new ChaosCache(cacheName) {
            @Override
            public String put(String key, String value) {
               return cache.put(key, value);
            }

            @Override
            public String get(String key) {
               return cache.get(key);
            }

            @Override
            public boolean isConflictResolutionInProgress() {
               ConflictManager cm = ConflictManagerFactory.get(cache.getAdvancedCache());
               return cm.isConflictResolutionInProgress();
            }

            @Override
            public boolean isStateTransferInProgress() {
               ConflictManager cm = ConflictManagerFactory.get(cache.getAdvancedCache());
               return cm.isStateTransferInProgress();
            }

            @Override
            public int size() {
               return cache.size();
            }
         };
      } else {
         throw new NullPointerException("Cache cannot be null");
      }
   }

   @Override
   public void waitForClusterToForm(int numberOfNodes) {
      Eventually.run(() -> {
         JChannel channel = this.getJChannel();
         return channel != null && channel.getView().getMembers().size() == numberOfNodes;
      });
   }

   @Override
   public Address getAddress() {
      return getJChannel().getAddress();
   }

   @Override
   public JChannel getJChannel() {
      JChannel channel = null;
      if (this.cacheManager != null) {
         channel = ((JGroupsTransport) this.cacheManager.getTransport()).getChannel();
      }
      return channel;
   }

   @Override
   public long getPid() {
      return pid;
   }
}
