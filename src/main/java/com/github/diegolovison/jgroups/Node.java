package com.github.diegolovison.jgroups;

import org.jgroups.JChannel;

/**
 * Wrapper for {@link JChannel} interactions
 */
public class Node {

   private final NodeConfig nodeConfig;

   Node(NodeConfig nodeConfig) {
      this.nodeConfig = nodeConfig;
   }

   int getMembersSize() {
      return this.getChannel().getView().getMembers().size();
   }

   void connect() {
      try {
         this.getChannel().connect(this.nodeConfig.getClusterName());
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   boolean isRunning() {
      return this.getChannel().isConnected();
   }

   void disconnect() {
      this.getChannel().disconnect();
   }

   private JChannel getChannel() {
      return this.nodeConfig.getChannel();
   }
}
