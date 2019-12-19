package com.github.diegolovison.jgroups;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public class JGroupsChaosConfig implements Serializable {

   private String clusterName;
   private boolean start;

   public JGroupsChaosConfig(String clusterName, boolean start) {
      this.clusterName = clusterName;
      this.start = start;
   }

   public String getClusterName() {
      return clusterName;
   }

   public boolean isStart() {
      return start;
   }

   public static class JGroupsChaosConfigMarshaller {
      public static JGroupsChaosConfig fromStream(InputStream inputStream) {
         try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            JGroupsChaosConfig object = (JGroupsChaosConfig) objectInputStream.readObject();
            return object;
         } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
         }
      }

      public static String toStream(JGroupsChaosConfig object) {
         try {
            File file = File.createTempFile("jgroups-chaos", JGroupsChaosConfig.class.getSimpleName());
            try (OutputStream outputStream = new FileOutputStream(file)) {
               ObjectOutput objectOutput = new ObjectOutputStream(outputStream);
               objectOutput.writeObject(object);
               objectOutput.flush();
            }
            return file.getAbsolutePath();
         } catch (IOException e) {
            throw new IllegalStateException(e);
         }
      }
   }
}
