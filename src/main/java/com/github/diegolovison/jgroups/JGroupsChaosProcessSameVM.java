package com.github.diegolovison.jgroups;

import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.stack.Protocol;

import com.github.diegolovison.os.ChaosProcess;
import com.github.diegolovison.os.Eventually;

public class JGroupsChaosProcessSameVM extends JGroupsChaosProcess {

   private JChannel channel;
   private JGroupsChaosConfig chaosConfig;

   @Override
   public ChaosProcess run(JGroupsChaosConfig chaosConfig) {
      this.chaosConfig = chaosConfig;
      this.channel = createJChannel(this.chaosConfig.getjGroupsXmlConfig());
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

   @Override
   public JChannel getJChannel() {
      return this.channel;
   }

   private JChannel createJChannel(String jGroupsXmlConfig) {
      JChannel jChannel;
      try {
         if (jGroupsXmlConfig != null) {
            jChannel = new JChannel(jGroupsXmlConfig);
         } else {
            jChannel = new JChannel();
         }

      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
      return jChannel;
   }

   @Override
   public void waitForClusterToForm(int numberOfNodes) {
      Eventually.run(() -> {
         // TODO the wait shouldn't connect. it must only wait.
         if (!this.channel.isConnected()) {
            this.connect();
         }
         return this.channel.getView().getMembers().size() == numberOfNodes;
      });
   }

   private void connect() {
      try {
         this.channel.connect(chaosConfig.getClusterName());
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }
}
