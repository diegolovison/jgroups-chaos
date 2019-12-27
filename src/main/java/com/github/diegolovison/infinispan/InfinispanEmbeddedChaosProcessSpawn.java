package com.github.diegolovison.infinispan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.protocols.TP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;
import org.jgroups.util.UUID;

import com.github.diegolovison.base.ChaosConfig;
import com.github.diegolovison.infinispan.cache.ChaosCache;
import com.github.diegolovison.jgroups.ProcessSpawn;
import com.github.diegolovison.jgroups.protocol.ProtocolAction;
import com.github.diegolovison.os.ChaosProcess;
import com.github.diegolovison.os.ChaosProcessFactory;
import com.github.diegolovison.os.SocketClient;
import com.github.diegolovison.os.Spawn;

public class InfinispanEmbeddedChaosProcessSpawn extends InfinispanChaosProcess {

   private static final Integer serverPort = Integer.getInteger("jgroups-chaos.SocketServer.port", 6666);
   private static final Boolean serverDebug = Boolean.getBoolean("jgroups-chaos.SocketServer.debug");

   private SocketClient client;

   @Override
   public ChaosProcess run(InfinispanChaosConfig chaosConfig) {
      int availableServerSocket = ChaosProcessFactory.getAvailableServerSocket(serverPort);
      String chaosConfigFile = ChaosConfig.ChaosConfigMarshaller.toStream(chaosConfig);
      List<String> jvmOpts = new ArrayList<>();
      if (serverDebug) {
         jvmOpts.add("-agentlib:jdwp=transport=dt_socket,address="+ (availableServerSocket + 1) +",server=y,suspend=n");
      }
      jvmOpts.add("-Djava.net.preferIPv4Stack=true"); // TODO really ?

      Process process = Spawn.exec(InfinispanEmbeddedChaosProcessSpawnServer.class, Arrays.asList(String.valueOf(availableServerSocket), chaosConfigFile), jvmOpts);

      this.client = new SocketClient();
      this.client.waitForTheServer(availableServerSocket);

      return this;
   }

   @Override
   public int getNumberOfMembers() {
      String response = this.client.sendMessage("getNumberOfMembers");
      return Integer.valueOf(response);
   }

   @Override
   public String getClusterName() {
      String response = this.client.sendMessage("getClusterName");
      return response;
   }

   @Override
   public boolean isRunning() {
      String response = this.client.sendMessage("isRunning");
      return Boolean.valueOf(response);
   }

   @Override
   public boolean isCoordinator() {
      String response = this.client.sendMessage("isCoordinator");
      return Boolean.valueOf(response);
   }

   @Override
   public void insertProtocol(Class<? extends ProtocolAction> protocolActionClass, ProtocolStack.Position above,
                              Class<TP> tpClass, Address[] ignored) {
      ProcessSpawn.insertProtocol(this.client, protocolActionClass, above, tpClass, ignored);
   }

   @Override
   public void removeProtocol(Class<? extends Protocol> protocolClass) {
      this.client.sendMessage("removeProtocol[" + protocolClass.getName() + "]");
   }

   @Override
   public void disconnect() {
      this.client.sendMessage("disconnect");
      this.client.stopConnection();
   }

   @Override
   public Address getAddress() {
      String response = this.client.sendMessage("getAddress");
      return UUID.fromString(response);
   }

   @Override
   public JChannel getJChannel() {
      throw new UnsupportedOperationException();
   }

   @Override
   public ChaosCache getCache(String cacheName) {
      String response = this.client.sendMessage("getCache[" + cacheName + "]");
      if (response != null) {
         return new ChaosCache(response) {
            InfinispanEmbeddedChaosProcessSpawn _this = InfinispanEmbeddedChaosProcessSpawn.this;
            @Override
            public String put(String key, String value) {
               return _this.client.sendMessage("getCachePut[" + cacheName + "," + key + "," + value + "]");
            }

            @Override
            public String get(String key) {
               return _this.client.sendMessage("getCacheGet[" + cacheName + "," + key + "]");
            }

            @Override
            public boolean isConflictResolutionInProgress() {
               return Boolean.valueOf(_this.client.sendMessage("getCacheIsConflictResolutionInProgress[" + cacheName + "]"));
            }

            @Override
            public boolean isStateTransferInProgress() {
               return Boolean.valueOf(_this.client.sendMessage("getCacheIsStateTransferInProgress[" + cacheName + "]"));
            }
         };
      } else {
         throw new NullPointerException("Cache cannot be null");
      }
   }

   @Override
   public void waitForClusterToForm(int numberOfNodes) {
      this.client.sendMessage("waitForClusterToForm[" + numberOfNodes + "]");
   }
}
