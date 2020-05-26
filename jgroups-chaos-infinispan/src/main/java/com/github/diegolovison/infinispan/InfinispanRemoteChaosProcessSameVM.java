package com.github.diegolovison.infinispan;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.JMException;
import javax.management.MBeanServerConnection;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.infinispan.Version;
import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.Configuration;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.manager.DefaultCacheManager;
import org.jgroups.Address;
import org.jgroups.JChannel;
import org.jgroups.stack.Protocol;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.StartedProcess;
import org.zeroturnaround.zip.ZipUtil;

import com.github.diegolovison.base.BasicXsl;
import com.github.diegolovison.infinispan.cache.ChaosCache;
import com.github.diegolovison.os.ChaosProcess;
import com.github.diegolovison.os.Eventually;
import com.sun.tools.attach.VirtualMachine;

public class InfinispanRemoteChaosProcessSameVM extends InfinispanChaosProcess {

   private InfinispanServerConfig infinispanServer;
   private RemoteCacheManager cacheManager;
   private MBeanServerConnection connection;

   @Override
   public ChaosCache getCache(String cacheName) {
      // you can see 17:15:21,054 ERROR (HotRod-client-async-pool-1-13) [RetryOnFailureOperation]
      RemoteCache<String, String> remoteCache = Eventually.run(() -> {
         return cacheManager.getCache(cacheName);
      });
      ChaosCache chaosCache = null;
      if (remoteCache != null) {
         chaosCache = new ChaosCache(cacheName) {
            @Override
            public String put(String key, String value) {
               return remoteCache.put(key, value);
            }

            @Override
            public String get(String key) {
               return remoteCache.get(key);
            }

            @Override
            public boolean isConflictResolutionInProgress() {
               throw new UnsupportedOperationException();
            }

            @Override
            public boolean isStateTransferInProgress() {
               throw new UnsupportedOperationException();
            }

            @Override
            public int size() {
               return remoteCache.size();
            }

            @Override
            public void clear() {
               remoteCache.clear();
            }
         };
      }
      return chaosCache;

   }

   @Override
   public void waitForClusterToForm(int numberOfNodes) {

      Configuration hotRodConfig = new ConfigurationBuilder()
            .addServers("localhost:" + infinispanServer.getHotRodPort())
            .build();
      cacheManager = new RemoteCacheManager(hotRodConfig);

      JMXConnector connector = Eventually.run(() -> {
         return getConnector();
      }, 30);

      if (connector == null) {
         throw new IllegalStateException("connector not found");
      }

      try {
         connection = connector.getMBeanServerConnection();
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }

      Eventually.runUntil(() -> {
         return isRunning();
      }, 30);

      AtomicInteger atomicNumberOfMembers = new AtomicInteger();
      boolean run = Eventually.runUntil(() -> {
         atomicNumberOfMembers.set(getNumberOfMembers());
         return atomicNumberOfMembers.get() == numberOfNodes;
      }, 30);

      if (!run) {
         throw new IllegalStateException(String.format("Number of nodes %d is not the expected: %s", atomicNumberOfMembers.get(), numberOfNodes));
      }
   }

   @Override
   public ChaosProcess run(InfinispanChaosConfig supplier) {

      File tmpFolder = new File("/tmp/servers");
      tmpFolder.mkdirs();

      File serverZipPath;

      String zipPath = System.getProperty("HotRodExtension.serverZipPath");
      if (zipPath == null) {
         String infinispanVersion = System.getProperty("version.infinispan");
         String zipName = String.format("infinispan-server-%s.zip", infinispanVersion);

         serverZipPath = new File(tmpFolder, zipName);

         if (!serverZipPath.exists()) {
            String downloadUrl = String.format("https://downloads.jboss.org/infinispan/%s/%s", infinispanVersion, zipName);
            try (BufferedInputStream in = new BufferedInputStream(new URL(downloadUrl).openStream());
                 FileOutputStream fileOutputStream = new FileOutputStream(serverZipPath)) {
               byte dataBuffer[] = new byte[1024];
               int bytesRead;
               while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                  fileOutputStream.write(dataBuffer, 0, bytesRead);
               }
            } catch (IOException e) {
               throw new IllegalStateException("Cannot download the server", e);
            }
         }
      } else {
         serverZipPath = new File(zipPath);
      }

      File dest = new File(tmpFolder, "server-"+supplier.getOffset());
      if (!dest.getAbsolutePath().startsWith("/tmp")) {
         throw new IllegalStateException("We are using rm -fR so this prevent mistakes! :)");
      }
      try {
         new ProcessExecutor().command("rm", "-fR", dest.getAbsolutePath()).exitValues(0).execute();
         ZipUtil.unpack(serverZipPath, dest);
         dest = new File(dest, dest.list()[0]);
         if (!new File(dest, "bin").exists()) {
            throw new IllegalStateException("Cannot find the bin folder");
         }
         new ProcessExecutor().command("chmod", "-R", "777", dest.getAbsolutePath()).exitValues(0).execute();
         infinispanServer = new InfinispanServerConfig();
         infinispanServer.folder = dest.getAbsolutePath();
         infinispanServer.offset = supplier.getOffset();

         StartedProcess startedProcess;
         try {
            startedProcess = InfinispanServer.start(infinispanServer, supplier.getConfigFile(), getJvmStartupArgs());
         } catch (IOException e) {
            throw new IllegalStateException(e);
         }

         infinispanServer.startedProcess = startedProcess;

         return this;
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   @Override
   public int getNumberOfMembers() {
      try {
         ObjectName cacheManagerName = getCacheManagerObjectName(connection, "org.infinispan", "default");
         String clusterMembers = (String) connection.getAttribute(cacheManagerName, "clusterMembers");
         return clusterMembers.split(",").length;
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   @Override
   public String getClusterName() {
      // TODO ???? retrieve from jgroups.channel -> cluster_name ( MBean )
      return cacheManager.getChannelFactory().getCurrentClusterName();
   }

   @Override
   public boolean isRunning() {
      // check the client connection.. it can take some time to identify that the server stopped
      boolean clientOk = cacheManager.isStarted();
      // in this case, we do a server call to double check
      boolean serverConnectionOk;
      try {
         getCacheManagerObjectName(connection, "org.infinispan", "default");
         serverConnectionOk = true;
      } catch (Exception e) {
         serverConnectionOk = false;
      }

      return clientOk && serverConnectionOk;
   }

   @Override
   public boolean isCoordinator() {
      try {
         ObjectName cacheManagerName = getCacheManagerObjectName(connection, "org.infinispan", "default");
         String nodeAddress = (String) connection.getAttribute(cacheManagerName, "nodeAddress");
         String clusterMembers = (String) connection.getAttribute(cacheManagerName, "clusterMembers");
         return clusterMembers.startsWith("[" + nodeAddress);
      } catch (Exception e) {
         throw new IllegalStateException(e);
      }
   }

   @Override
   public void removeProtocol(Class<? extends Protocol> protocolClass) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void disconnect() {
      Long commandLinePid = infinispanServer.getCommandLinePid();
      Long pid = infinispanServer.getPid();
      kill(commandLinePid);
      kill(pid);
   }

   private void kill(Long pid) {
      if (pid != null) {
         try {
            String[] commandKill = new String[]{"kill", String.valueOf(pid)};
            new ProcessExecutor().command(commandKill).execute();
         } catch (IOException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
         }
      }
   }

   @Override
   public Address getAddress() {
      throw new UnsupportedOperationException();
   }

   @Override
   public JChannel getJChannel() {
      throw new UnsupportedOperationException();
   }

   @Override
   public long getPid() {
      return infinispanServer.getPid();
   }

   public JMXConnector getConnector() {
      try {
         JMXServiceURL url = new JMXServiceURL(String.format("service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi",
               "127.0.0.1", (9999 + this.infinispanServer.offset)));
         JMXConnector jmxConnector = JMXConnectorFactory.connect(url);
         return jmxConnector;
      } catch (IOException e) {
         throw new IllegalStateException(e);
      }
   }

   private ObjectName getCacheManagerObjectName(MBeanServerConnection connection, String jmxDomain, String managerName) throws IOException, JMException {
      // Find CacheManager MBean using parameters, if that fails use type
      ObjectName cacheManagerName = new ObjectName(
            String.format("%s:type=CacheManager,name=\"%s\",component=CacheManager", jmxDomain, managerName));
      try {
         connection.getMBeanInfo(cacheManagerName);
      } catch (InstanceNotFoundException | IntrospectionException | ReflectionException e) {
         cacheManagerName = null;
         Set<ObjectInstance> cacheManagers = connection.queryMBeans(null, javax.management.Query
               .isInstanceOf(javax.management.Query.value(DefaultCacheManager.class.getTypeName())));
         if (cacheManagers.size() == 0) {
            throw e;
         } else {
            cacheManagerName = cacheManagers.iterator().next().getObjectName();
         }
      }
      return cacheManagerName;
   }
}
