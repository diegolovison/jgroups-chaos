package com.github.diegolovison.jgroups;

import static com.github.diegolovison.jgroups.Sleep.sleep;

import java.util.concurrent.TimeUnit;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.protocols.TP;
import org.jgroups.stack.Protocol;
import org.jgroups.stack.ProtocolStack;

import com.github.diegolovison.jgroups.protocol.ProtocolAction;
import com.github.diegolovison.os.ChaosProcess;

public class JGroupsChaosProcessSameVM extends JGroupsChaosProcess<JGroupsChaosConfig> {

   private JChannel channel;
   private JGroupsChaosConfig chaosConfig;

   @Override
   public ChaosProcess run(JGroupsChaosConfig chaosConfig) {
      this.channel = createJChannel();
      this.chaosConfig = chaosConfig;
      try {
         if (chaosConfig.isStart()) {
            this.connect();
         }
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
      return this;
   }

   @Override
   public int getNumberOfMembers() {
      return this.channel.getView().getMembers().size();
   }

   @Override
   public String getClusterName() {
      return this.channel.getClusterName();
   }

   @Override
   public boolean isRunning() {
      return this.channel.isConnected();
   }

   @Override
   public boolean isCoordinator() {
      return this.channel.getView().getMembers().get(0).equals(getAddress());
   }

   @Override
   public void insertProtocol(ProtocolAction protocolAction, ProtocolStack.Position above, Class<TP> tpClass) {
      try {
         Protocol protocol = protocolAction.find(this.channel);
         if (protocol == null) {
            protocol = protocolAction.create();
         }
         if (protocolAction.shouldOverwriteSet()) {
            protocolAction.set(protocol);
         }
         this.channel.getProtocolStack().insertProtocol(protocol, above, tpClass);
      } catch (Exception e) {
         throw new IllegalStateException("Cannot insert protocol", e);
      }
   }

   @Override
   public void removeProtocol(Class<? extends Protocol> protocolClass) {
      this.channel.getProtocolStack().removeProtocol(protocolClass);
   }

   @Override
   public void disconnect() {
      this.channel.disconnect();
   }

   @Override
   public Address getAddress() {
      return this.channel.getAddress();
   }

   private JChannel createJChannel() {
      JChannel jChannel;
      try {
         jChannel = new JChannel();
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
      return jChannel;
   }

   @Override
   public void waitForClusterToForm(int numberOfNodes) {
      long failTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
      while (System.currentTimeMillis() < failTime) {
         if (!this.channel.isConnected()) {
            this.connect();
         }
         if (this.channel.getView().getMembers().size() == numberOfNodes) {
            return;
         }
         sleep(100);
      }
   }

   private void connect() {
      try {
         this.channel.connect(chaosConfig.getClusterName());
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }
}
