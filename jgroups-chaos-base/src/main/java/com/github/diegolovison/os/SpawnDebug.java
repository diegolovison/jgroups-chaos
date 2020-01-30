package com.github.diegolovison.os;

import java.util.List;

public class SpawnDebug {

   private static final Boolean suspendDebug = Boolean.getBoolean("jgroups-chaos.debug.suspend");

   public static void attacheDebuggerIfNeeded(List<String> jvmOpts) {

      boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean()
            .getInputArguments().toString().indexOf("-agentlib:jdwp") > 0;

      if (isDebug) {
         jvmOpts.add("-agentlib:jdwp=transport=dt_socket,address=0,server=y,suspend=" + (suspendDebug ? "y" : "n"));
      }
   }
}
