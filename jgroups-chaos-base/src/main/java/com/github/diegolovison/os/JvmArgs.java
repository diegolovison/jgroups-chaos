package com.github.diegolovison.os;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class JvmArgs {

   private static final Boolean suspendDebug = Boolean.getBoolean("jgroups-chaos.debug.suspend");

   public static Collection<String> appendJvmArgs() {

      Set<String> jvmOpts = new HashSet<>();

      RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
      List<String> arguments = runtimeMxBean.getInputArguments();

      for (String arg : arguments) {
         if (arg.indexOf("-agentlib:jdwp") >= 0) {
            jvmOpts.add("-agentlib:jdwp=transport=dt_socket,address=0,server=y,suspend=" + (suspendDebug ? "y" : "n"));
         } else if (arg.startsWith("-Djgroups") || arg.startsWith("-Djava") || arg.startsWith("-Dfile")) {
            jvmOpts.add(arg);
         }
      }

      // https://github.com/belaban/JGroups/wiki/Multicast-routing-on-Mac-OS
      jvmOpts.add("-Djava.net.preferIPv4Stack=true");

      return jvmOpts;
   }
}
