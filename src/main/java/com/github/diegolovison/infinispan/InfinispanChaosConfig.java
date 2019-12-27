package com.github.diegolovison.infinispan;

import com.github.diegolovison.base.ChaosConfig;

public class InfinispanChaosConfig extends ChaosConfig {

   private String configFile;

   public InfinispanChaosConfig(String configFile) {
      this.configFile = configFile;
   }

   public String getConfigFile() {
      return configFile;
   }
}
