package com.github.diegolovison.infinispan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.github.diegolovison.jgroups.Cluster;
import com.github.diegolovison.os.ChaosProcessFactory;
import com.github.diegolovison.os.ChaosProcessFramework;
import com.github.diegolovison.os.ChaosProcessType;

public class InfinispanCluster extends Cluster<InfinispanNode> {

   private final List<InfinispanChaosProcess> chaosProcesses;
   private final ChaosProcessType processType;
   private String clusterName;

   public InfinispanCluster(ChaosProcessType processType) {
      this.chaosProcesses = new ArrayList<>();
      this.processType = processType;
   }

   public List<InfinispanNode> createNodes(int numberOfNodes) {
      return createNodes(null, numberOfNodes, null);
   }

   public InfinispanNode createNode(String configFile) {
      return createNodes(configFile, 1, null).get(0);
   }

   // GlobalConfigurationBuilder is not serializable. This is why we are using the config file
   public List<InfinispanNode> createNodes(String configFile, int numberOfNodes) {
      return createNodes(configFile, numberOfNodes, null);
   }

   public List<InfinispanNode> createNodes(String configFile, int numberOfNodes, Map<String, String> arguments) {
      // maybe an argument ?
      boolean connect = true;
      // create managers
      for (int i = 0; i < numberOfNodes; i++) {
         InfinispanChaosProcess chaosProcess = (InfinispanChaosProcess)
               ChaosProcessFactory.createInstance(ChaosProcessFramework.INFINISPAN, this.processType)
                     .run(new InfinispanChaosConfig(i, configFile, arguments));
         this.chaosProcesses.add(chaosProcess);
         this.nodes.add(new InfinispanNode(chaosProcess));
      }

      // we should have 2 arguments, start and form a cluster..
      if (connect) {
         form(numberOfNodes);
      }

      // wait until the nodes be connected to retrieve the cluster name
      this.clusterName = this.chaosProcesses.get(0).getClusterName();

      return Collections.unmodifiableList(this.nodes);
   }

   public void form(int numberOfNodes) {
      for (InfinispanChaosProcess infinispanChaosProcess : this.chaosProcesses) {
         infinispanChaosProcess.waitForClusterToForm(numberOfNodes);
      }
   }

   @Override
   public int size() {
      int size = 0;
      for (InfinispanChaosProcess chaosProcess : this.chaosProcesses) {
         if (chaosProcess.isRunning() && chaosProcess.getClusterName().equals(this.clusterName)) {
            size = chaosProcess.getNumberOfMembers();
            break;
         }
      }
      return size;
   }
}
