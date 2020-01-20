package com.github.diegolovison.jgroups.failure.provider;

import java.util.List;

import com.github.diegolovison.jgroups.Node;
import com.github.diegolovison.jgroups.failure.FailureProvider;

/*
 * R  Running
 * S  Sleeping in an interruptible wait
 * D  Waiting in uninterruptible disk sleep
 * Z  Zombie
 * T  Stopped (on a signal) or (before Linux 2.6.33)
 *    trace stopped
 * t  Tracing stop (Linux 2.6.33 onward)
 * W  Paging (only before Linux 2.6.0)
 * X  Dead (from Linux 2.6.0 onward)
 * x  Dead (Linux 2.6.33 to 3.13 only)
 * K  Wakekill (Linux 2.6.33 to 3.13 only)
 * W  Waking (Linux 2.6.33 to 3.13 only)
 * P  Parked (Linux 3.9 to 3.13 only)
 */
public class GCStopWorldFailureProvider implements FailureProvider {
   @Override
   public void createFailure(Node[] nodes, List<Node> ignored) {
      for (Node node : nodes) {
         node.pause();
         waitForFailure(node);
      }
   }

   @Override
   public void solveFailure(Node... nodes) {
      for (Node node : nodes) {
         node.resume();
         waitForFailureBeSolved(node);
      }
   }

   @Override
   public void waitForFailure(Node... nodes) {
      String status = nodes[0].getPidStatus();
      if (!"T".equals(status)) {
         throw new IllegalStateException("The status must be 'S'. The SO call is sync");
      }
   }

   @Override
   public void waitForFailureBeSolved(Node... nodes) {
      String status = nodes[0].getPidStatus();
      boolean running = "S".equals(status) || "R".equals(status);
      if (!running) {
         throw new IllegalStateException("The status must be 'S' or 'R'. The SO call is sync");
      }
   }
}
