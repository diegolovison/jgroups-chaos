package com.github.diegolovison.infinispan;

import java.util.Map;

import com.github.diegolovison.base.ChaosConfig;

public class InfinispanChaosConfig extends ChaosConfig {

   private int offset;
   private String configFile;
   private Map<String, String> arguments;

   public InfinispanChaosConfig(int offset, String configFile, Map<String, String> arguments) {
      this.offset = offset;
      this.configFile = configFile;
      this.arguments = arguments;
   }

   public String getConfigFile() {
      return configFile;
   }

   public Map<String, String> getArguments() {
      return arguments;
   }

   public int getOffset() {
      return offset;
   }
}
