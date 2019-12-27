package com.github.diegolovison.jgroups;

import com.github.diegolovison.base.ChaosConfig;

public class JGroupsChaosConfig extends ChaosConfig {

   private String clusterName;
   private boolean start;

   public JGroupsChaosConfig(String clusterName, boolean start) {
      this.clusterName = clusterName;
      this.start = start;
   }

   public String getClusterName() {
      return clusterName;
   }

   public boolean isStart() {
      return start;
   }
}
