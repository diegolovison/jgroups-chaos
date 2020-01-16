package com.github.diegolovison.jgroups;

import com.github.diegolovison.base.ChaosConfig;

public class JGroupsChaosConfig extends ChaosConfig {

   private String clusterName;
   private boolean start;
   private String jGroupsXmlConfig;

   public JGroupsChaosConfig(String clusterName, boolean start, String jGroupsXmlConfig) {
      this.clusterName = clusterName;
      this.start = start;
      this.jGroupsXmlConfig = jGroupsXmlConfig;
   }

   public String getClusterName() {
      return clusterName;
   }

   public boolean isStart() {
      return start;
   }

   public String getjGroupsXmlConfig() {
      return jGroupsXmlConfig;
   }
}
