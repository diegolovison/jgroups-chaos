package com.github.diegolovison.infinispan;

import java.util.function.Supplier;

import org.infinispan.Cache;
import org.infinispan.configuration.cache.ConfigurationBuilder;
import org.infinispan.configuration.global.GlobalConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.infinispan.manager.EmbeddedCacheManager;
import org.infinispan.remoting.transport.jgroups.JGroupsTransport;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.protocols.TP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;

import com.github.diegolovison.jgroups.protocol.ProtocolAction;

public class InfinispanEmbeddedChaosProcessSameVM extends InfinispanChaosProcess<Supplier<GlobalConfigurationBuilder>> {

   private EmbeddedCacheManager cacheManager;

   @Override
   public InfinispanEmbeddedChaosProcessSameVM run(Supplier<GlobalConfigurationBuilder> globalConfigurationBuilderSupplier) {
      this.cacheManager = new DefaultCacheManager(globalConfigurationBuilderSupplier.get().build());
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
   public void insertProtocol(ProtocolAction protocolAction, ProtocolStack.Position above, Class<TP> tpClass) {
      try {
         Protocol protocol = protocolAction.find(getJChannel());
         if (protocol == null) {
            protocol = protocolAction.create();
         }
         if (protocolAction.shouldOverwriteSet()) {
            protocolAction.set(protocol);
         }
         this.getJChannel().getProtocolStack().insertProtocol(protocol, above, tpClass);
      } catch (Exception e) {
         throw new IllegalStateException("Cannot insert protocol", e);
      }
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
   public Address getAddress() {
      return getJChannel().getAddress();
   }

   @Override
   public void createCache(String cacheName, ConfigurationBuilder cacheConfigurationBuilder) {
      this.cacheManager.defineConfiguration(cacheName, cacheConfigurationBuilder.build());
   }

   @Override
   public Cache getCache(String cacheName) {
      return this.cacheManager.getCache(cacheName);
   }

   private JChannel getJChannel() {
      return ((JGroupsTransport) this.cacheManager.getTransport()).getChannel();
   }
}
