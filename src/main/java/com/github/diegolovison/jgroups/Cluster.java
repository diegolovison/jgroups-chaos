package com.github.diegolovison.jgroups;

import static com.github.diegolovison.jgroups.Sleep.sleep;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.github.diegolovison.jgroups.failure.Failure;
import com.github.diegolovison.jgroups.failure.FailureProvider;

public abstract class Cluster<N extends Node> {

   protected List<N> nodes;

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

   public void close(Node node) {
      node.close();
   }

   public void form() {
      int size = this.nodes.size();
      for (int i=0; i<size; i++) {
         this.nodes.get(i).connect();
      }
      waitForClusterToForm(this.getRunningNode());
   }

   protected void waitForClusterToForm(Node nodeBase) {
      long failTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
      while (System.currentTimeMillis() < failTime) {
         if (nodeBase.getMembersSize() == this.nodes.size()) {
            return;
         }
         sleep(100);
      }
   }

   protected Node getRunningNode() {
      Node runningNode = null;
      int size = this.nodes.size();
      if (size > 0) {
         for (int i=0; i<size; i++) {
            Node node = this.nodes.get(i);
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
}
