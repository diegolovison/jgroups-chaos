package com.github.diegolovison.infinispan;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;

import com.github.diegolovison.os.Eventually;

public class InfinispanServerConfig {
   StartedProcess startedProcess;
   String folder;
   int offset;
   int defaultHotRodPort = 11222;

   public Long getCommandLinePid() {
      return startedProcess.getProcess().pid();
   }

   public Long getPid() {
      Long pid = Eventually.run(() -> {
         try {
            String[] commandPGrep = new String[]{"pgrep", "-P", String.valueOf(getCommandLinePid())};
            String subProcessPidStr = new ProcessExecutor().command(commandPGrep).readOutput(true).execute().outputUTF8();
            Long subProcessPid = null;
            if (subProcessPidStr != null) {
               subProcessPidStr = subProcessPidStr.replaceAll("\\n", "").trim();
               if (subProcessPidStr.length() > 0) {
                  subProcessPid = Long.valueOf(subProcessPidStr);
               }
            }
            return subProcessPid;
         } catch (Exception e) {
            throw new IllegalStateException(e);
         }
      });
      return pid;
   }

   public int getHotRodPort() {
      return defaultHotRodPort + offset;
   }
}