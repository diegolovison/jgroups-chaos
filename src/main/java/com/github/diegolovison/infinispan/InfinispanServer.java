package com.github.diegolovison.infinispan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.infinispan.Version;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

public class InfinispanServer {

   public static StartedProcess start(InfinispanServerConfig infinispanServer, String configFile) throws IOException {
      String infinispanVersion = Version.getMajorMinor();
      List<String> args = new ArrayList<>();
      if(infinispanVersion.equalsIgnoreCase("9.4")) {
         args.add(infinispanServer.folder + "/bin/standalone.sh");
         args.add("-Djboss.socket.binding.port-offset=" + infinispanServer.offset);
         args.add("-c");
         if (configFile != null) {
            args.add(configFile);
         } else {
            args.add("clustered.xml");
         }
      } else {
         args.add(infinispanServer.folder + "/bin/server.sh");
         args.add("-b");
         args.add(String.valueOf(infinispanServer.getHotRodPort()));
      }
      return new ProcessExecutor().command(args).readOutput(true).redirectOutput(Slf4jStream.ofCaller().asInfo()).start();
   }
}
