package com.github.diegolovison.os;

import java.io.Closeable;
import java.io.IOException;

public class ClosableUtil {

   public static void closeSilent(Closeable... list) {
      for (Closeable c : list) {
         try {
            if (c != null)
               c.close();
         } catch (IOException e) {
            // silent
         }
      }
   }
}
