package com.github.diegolovison.infinispan;

import java.util.Map;

import com.github.diegolovison.base.ChaosConfig;

public class InfinispanChaosConfig extends ChaosConfig {

   private String configFile;
   private Map<String, String> arguments;

   public InfinispanChaosConfig(String configFile, Map<String, String> arguments) {
      this.configFile = configFile;
      this.arguments = arguments;
   }

   public String getConfigFile() {
      return configFile;
   }

   public Map<String, String> getArguments() {
      return arguments;
   }
}
