package com.github.diegolovison.jgroups;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.jgroups.JChannel;

public class Cluster {

   private List<Node> nodes;

   public Cluster() {
      this.nodes = new ArrayList<>(2);
   }

   public int size() {
      int size = this.nodes.size();
      if (size > 0) {
         return this.getRunningNode().getMembersSize();
      } else {
         return 0;
      }
   }

   public void disconnect(Node node) {
      node.disconnect();
   }

   public void createNodes(int numberOfNodes) {
      createNodes(numberOfNodes, true);
   }

   public void createNodes(int numberOfNodes, boolean connect) {
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
         this.nodes.add(new Node(nodeConfig));
      }
      if (this.nodes.size() == 0) {
         throw new IllegalStateException("numberOfNodes must be greater than 0");
      }

      if (connect) {
         waitForClusterToForm(this.getRunningNode());
      }
   }

   public Node get(int index) {
      return this.nodes.get(index);
   }

   public void form() {
      int size = this.nodes.size();
      for (int i=0; i<size; i++) {
         this.nodes.get(i).connect();
      }
      waitForClusterToForm(this.getRunningNode());
   }

   // ====== PRIVATE
   private Node getRunningNode() {
      Node runningNode = null;
      int size = this.nodes.size();
      if (size > 0) {
         for (int i=0; i<size; i++) {
            Node node = this.nodes.get(0);
            if (node.isRunning()) {
               runningNode = node;
               break;
            }
         }
      }
      if (runningNode == null) {
         throw new NullPointerException("You are requesting a running node");
      }
      return runningNode;
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

   private void waitForClusterToForm(Node nodeBase) {
      long failTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
      while (System.currentTimeMillis() < failTime) {
         if (nodeBase.getMembersSize() == this.nodes.size()) {
            return;
         }
         try {
            Thread.sleep(100);
         } catch (InterruptedException e) {
            throw new IllegalStateException("Cannot sleep when trying to wait for cluster formation", e);
         }
      }
   }
}
