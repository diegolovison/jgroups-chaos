package com.github.diegolovison.jgroups;

import org.jgroups.JChannel;

public class NodeConfig {

   private final JChannel channel;
   private final String clusterName;

   public NodeConfig(JChannel channel, String clusterName) {
      this.channel = channel;
      this.clusterName = clusterName;
   }

   JChannel getChannel() {
      return this.channel;
   }

   String getClusterName() {
      return clusterName;
   }
}
