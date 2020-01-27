package com.github.diegolovison.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

public abstract class ChaosConfig implements Serializable {

   public static class ChaosConfigMarshaller {
      public static <T> T fromStream(InputStream inputStream) {
         try (ObjectInputStream objectInputStream = new ObjectInputStream(inputStream)) {
            T object = (T) objectInputStream.readObject();
            return object;
         } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException(e);
         }
      }

      public static String toStream(ChaosConfig object) {
         try {
            File file = File.createTempFile("jgroups-chaos", ChaosConfig.class.getSimpleName());
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
