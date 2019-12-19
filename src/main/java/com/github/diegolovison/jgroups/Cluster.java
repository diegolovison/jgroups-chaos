package com.github.diegolovison.jgroups;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.diegolovison.jgroups.failure.Failure;
import com.github.diegolovison.jgroups.failure.FailureProvider;

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
      provider.waitForFailure();
   }

   public void solveFailure(Failure failure, Node... nodes) {
      FailureProvider provider = FailureProvider.get(failure);
      provider.solveFailure(nodes);
      provider.waitForFailureBeSolved();
   }

   public void disconnectAll() {
      for (Node node : nodes) {
         node.disconnect();
      }
   }

   public abstract int size();
}
