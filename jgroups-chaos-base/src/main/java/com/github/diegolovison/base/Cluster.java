package com.github.diegolovison.base;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.diegolovison.base.failure.Failure;
import com.github.diegolovison.base.failure.FailureProvider;

public abstract class Cluster<N extends Node> {

   protected List<N> nodes;

   public Cluster() {
      this.nodes = new ArrayList<>(2);
   }

   public void createFailure(Failure failure, Node[]... groups) {
      FailureProvider provider = FailureProvider.get(failure);
      List<Node> allNodes = new ArrayList<>();
      for (Node[] nodes : groups) {
         for (Node node : nodes) {
            allNodes.add(node);
         }
      }
      // split per group
      for (Node[] nodes : groups) {
         List<Node> ignored = new ArrayList<>(allNodes);
         ignored.removeAll(Arrays.asList(nodes));
         provider.createFailure(nodes, ignored);
      }
   }

   public void solveFailure(Failure failure, Node... nodes) {
      FailureProvider provider = FailureProvider.get(failure);
      provider.solveFailure(nodes);
   }

   public void disconnectAll() {
      for (Node node : nodes) {
         node.disconnect();
      }
   }

   public void disconnect(Node node) {
      node.disconnect();
   }

   public abstract int size();
}
