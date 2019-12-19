package com.github.diegolovison.os;

import com.github.diegolovison.infinispan.InfinispanEmbeddedChaosProcessSameVM;
import com.github.diegolovison.jgroups.JGroupsChaosProcessSameVM;
import com.github.diegolovison.jgroups.JGroupsChaosProcessSpawn;

public class ChaosProcessFactory {

   public static ChaosProcess createInstance(ChaosProcessFramework framework, ChaosProcessType processType) {
      String typeProperty = System.getProperty("jgroups-chaos.ChaosProcessType");
      if (typeProperty != null && !typeProperty.isBlank()) {
         processType = ChaosProcessType.valueOf(typeProperty);
      }
      if (ChaosProcessFramework.JGROUPS.equals(framework)) {
         if (ChaosProcessType.SAME_VM.equals(processType)) {
            return new JGroupsChaosProcessSameVM();
         } else if (ChaosProcessType.SPAWN.equals(processType)) {
            return new JGroupsChaosProcessSpawn();
         } else {
            throw new UnsupportedOperationException();
         }
      } else if (ChaosProcessFramework.INFINISPAN.equals(framework)) {
         if (ChaosProcessType.SAME_VM.equals(processType)) {
            return new InfinispanEmbeddedChaosProcessSameVM();
         } else {
            throw new UnsupportedOperationException();
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }
}
