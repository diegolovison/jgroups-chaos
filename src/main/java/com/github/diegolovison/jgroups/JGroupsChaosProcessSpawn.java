package com.github.diegolovison.jgroups;

import java.io.IOException;
import java.util.Arrays;

import org.jgroups.Address;
import org.jgroups.protocols.TP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;

import com.github.diegolovison.jgroups.protocol.ProtocolAction;
import com.github.diegolovison.os.ChaosProcess;
import com.github.diegolovison.os.SocketClient;
import com.github.diegolovison.os.SocketServer;
import com.github.diegolovison.os.Spawn;

public class JGroupsChaosProcessSpawn extends JGroupsChaosProcess<JGroupsChaosConfig> {

   private static final Integer serverPort = Integer.getInteger("jgroups-chaos.SocketServer.port", 6666);
   private static final Boolean serverDebug = Boolean.getBoolean("jgroups-chaos.SocketServer.debug");

   private SocketClient client;

   @Override
   public ChaosProcess run(JGroupsChaosConfig chaosConfig) {
      Integer availablePort = SocketServer.getAvailablePort(serverPort);
      String chaosConfigFile = JGroupsChaosConfig.JGroupsChaosConfigMarshaller.toStream(chaosConfig);
      String debugServer = "";
      if (serverDebug) {
         int debugPort = availablePort + 1;
         debugServer = "-agentlib:jdwp=transport=dt_socket,address="+ debugPort +",server=y,suspend=n";
      }

      Process process = Spawn.exec(JGroupsChaosProcessSpawnServer.class, Arrays.asList(availablePort.toString(), chaosConfigFile), Arrays.asList(debugServer));

      this.client = new SocketClient();
      this.waitForTheServer(availablePort);

      return this;
   }

   private void waitForTheServer(Integer availablePort) {
      // is 10 enough ?
      Exception exception = null;
      for (int i = 0; i < 10; i++) {
         try {
            this.client.startConnection(availablePort);
            exception = null;
            break;
         } catch (IOException e) {
            try {
               Thread.sleep(1_000);
               exception = e;
            } catch (InterruptedException ex) {
               exception = e;
            }
         }
      }
      if (exception != null) {
         throw new IllegalStateException("The server is not running. The client was not able to connect.", exception);
      }
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
   public void insertProtocol(ProtocolAction protocolAction, ProtocolStack.Position above, Class<TP> tpClass) {
      this.client.sendMessage("insertProtocol[" + protocolAction.toString() + ", " + above.toString() + ", " + tpClass.toString() + "]");
   }

   @Override
   public void removeProtocol(Class<? extends Protocol> protocolClass) {
      this.client.sendMessage("removeProtocol[" + protocolClass.toString() + "]");
   }

   @Override
   public void disconnect() {
      this.client.sendMessage("stop");
      this.client.stopConnection();
   }

   @Override
   public Address getAddress() {
      String response = this.client.sendMessage("getAddress");
      return new FakeAddress(response);
   }

   @Override
   public void waitForClusterToForm(int numberOfNodes) {
      this.client.sendMessage("waitForClusterToForm[" + String.valueOf(numberOfNodes) + "]");
   }
}
