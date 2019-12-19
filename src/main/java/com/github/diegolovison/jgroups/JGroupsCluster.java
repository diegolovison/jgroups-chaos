package com.github.diegolovison.jgroups;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import com.github.diegolovison.os.ChaosProcessFactory;
import com.github.diegolovison.os.ChaosProcessFramework;
import com.github.diegolovison.os.ChaosProcessType;

public class JGroupsCluster extends Cluster<Node> {

   private final List<JGroupsChaosProcess> chaosProcesses;
   private final String clusterName;
   private final ChaosProcessType processType;

   public JGroupsCluster() {
      this(UUID.randomUUID().toString(), ChaosProcessType.SAME_VM);
   }

   public JGroupsCluster(String clusterName, ChaosProcessType processType) {
      this.chaosProcesses = new ArrayList<>();
      this.clusterName = clusterName;
      this.processType = processType;
   }

   public List<Node> createNodes(int numberOfNodes) {
      return createNodes(numberOfNodes, true);
   }

   public List<Node> createNodes(int numberOfNodes, boolean connect) {
      if (numberOfNodes <= 0) {
         throw new IllegalStateException("numberOfNodes must be greater than 0");
      }
      for (int i=0; i<numberOfNodes; i++) {
         JGroupsChaosProcess chaosProcess = (JGroupsChaosProcess) ChaosProcessFactory.createInstance(ChaosProcessFramework.JGROUPS, this.processType)
               .run(new JGroupsChaosConfig(this.clusterName, connect));
         this.chaosProcesses.add(chaosProcess);
         this.nodes.add(new Node(chaosProcess));
      }
      if (connect) {
         form(numberOfNodes);
      }
      return Collections.unmodifiableList(this.nodes);
   }

   public void form(int numberOfNodes) {
      for (JGroupsChaosProcess jGroupsChaosProcess : this.chaosProcesses) {
         jGroupsChaosProcess.waitForClusterToForm(numberOfNodes);
      }
   }

   public void disconnect(Node node) {
      node.disconnect();
   }

   @Override
   public int size() {
      int size = 0;
      for (JGroupsChaosProcess jGroupsChaosProcess : this.chaosProcesses) {
         if (jGroupsChaosProcess.isRunning() && jGroupsChaosProcess.getClusterName().equals(this.clusterName)) {
            size = jGroupsChaosProcess.getNumberOfMembers();
            break;
         }
      }
      return size;
   }
}
