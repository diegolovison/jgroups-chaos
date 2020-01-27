package com.github.diegolovison.base;

public class NodeOr {

   private Node[] nodes;

   public NodeOr(Node... nodes) {
      this.nodes = nodes;
   }

   public boolean isCoordinator() {
      int coordinator = 0;
      for (Node node : nodes) {
         if (node.isCoordinator()) {
            coordinator++;
         }
      }
      if (coordinator == 0 || coordinator > 1) {
         throw new IllegalStateException("It must have 1 coordinator. Total=" + coordinator);
      }
      return coordinator == 1;
   }
}
