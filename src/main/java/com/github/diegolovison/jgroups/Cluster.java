package com.github.diegolovison.jgroups;

public class Cluster {

   private Nodes nodes;

   public int size() {
      return this.nodes.size();
   }

   public void discard(Node node) {
      this.nodes.remove(node);
   }

   public Nodes createNodes(int numberOfNodes) {
      this.nodes = new Nodes();
      for (int i=0; i<numberOfNodes; i++) {
         NodeConfig nodeConfig = new NodeConfig();
         this.nodes.createNode(nodeConfig);
      }
      return this.nodes;
   }

   public Node get(int index) {
      return this.nodes.get(index);
   }
}
