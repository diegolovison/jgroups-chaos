package com.github.diegolovison.os;

import com.github.diegolovison.infinispan.InfinispanEmbeddedChaosProcessSameVM;
import com.github.diegolovison.jgroups.JGroupsChaosProcessSameVM;

public class ChaosProcessFactory {

   public static ChaosProcess createInstance(ChaosProcessFramework framework) {
      String typeProperty = System.getProperty("jgroups-chaos.ChaosProcessType", ChaosProcessType.SAME_VM.name());
      ChaosProcessType type = ChaosProcessType.valueOf(typeProperty);
      if (ChaosProcessFramework.JGROUPS.equals(framework)) {
         if (ChaosProcessType.SAME_VM.equals(type)) {
            return new JGroupsChaosProcessSameVM();
         } else {
            throw new UnsupportedOperationException();
         }
      } else if (ChaosProcessFramework.INFINISPAN.equals(framework)) {
         if (ChaosProcessType.SAME_VM.equals(type)) {
            return new InfinispanEmbeddedChaosProcessSameVM();
         } else {
            throw new UnsupportedOperationException();
         }
      } else {
         throw new UnsupportedOperationException();
      }
   }
}
