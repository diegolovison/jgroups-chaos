package com.github.diegolovison.infinispan;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.infinispan.Version;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;

public class InfinispanServer {

   public static StartedProcess start(InfinispanServerConfig infinispanServer, String configFile,
                                      Collection<String> jvmArgs) throws IOException {
      List<String> args = new ArrayList<>();

      args.add(infinispanServer.folder + "/bin/server.sh");
      args.add("-p");
      args.add(String.valueOf(infinispanServer.getHotRodPort()));
      if (configFile != null) {
         File serverFolder = new File(infinispanServer.folder, "server");
         File confFolder = new File(serverFolder, "conf");
         File realConfiguration = new File(confFolder, "new-infinispan.xml");

         Path copied = Paths.get(realConfiguration.getAbsolutePath());
         Path originalPath;
         try {
            originalPath = Paths.get(InfinispanServer.class.getResource("/" + configFile).toURI());
         } catch (URISyntaxException e) {
            throw new IOException(e);
         }
         Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);

         args.add("-c");
         args.add(copied.getFileName().toString());
      }
      args.add("-Dcom.sun.management.jmxremote.port=" + (9999 + infinispanServer.offset));
      args.add("-Dcom.sun.management.jmxremote.authenticate=false");
      args.add("-Dcom.sun.management.jmxremote.ssl=false");
      args.addAll(jvmArgs);
      // TODO allow debug remote server
      Iterator<String> it = args.iterator();
      while (it.hasNext()) {
         String arg = it.next();
         if (arg.startsWith("-agentlib")) {
            it.remove();
         }
      }
      return new ProcessExecutor().command(args).redirectOutput(System.out).start();
   }
}
