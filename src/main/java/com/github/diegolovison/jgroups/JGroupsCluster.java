package com.github.diegolovison.jgroups;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.jgroups.JChannel;

public class JGroupsCluster extends Cluster<Node> {

   public List<Node> createNodes(int numberOfNodes) {
      return createNodes(numberOfNodes, true);
   }

   public List<Node> createNodes(int numberOfNodes, boolean connect) {
      if (numberOfNodes <= 0) {
         throw new IllegalStateException("numberOfNodes must be greater than 0");
      }
      String clusterName = UUID.randomUUID().toString();
      for (int i=0; i<numberOfNodes; i++) {
         JChannel channel = createJChannel();
         try {
            if (connect) {
               channel.connect(clusterName);
            }
         } catch (Exception e) {
            throw new IllegalStateException(e);
         }
         NodeConfig nodeConfig = new NodeConfig(channel, clusterName);
         this.nodes.add(new Node(i, nodeConfig));
      }
      if (connect) {
         waitForClusterToForm(this.getRunningNode());
      }
      return Collections.unmodifiableList(this.nodes);
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
}
