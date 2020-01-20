package com.github.diegolovison.os;

import static com.github.diegolovison.jgroups.Sleep.sleep;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Eventually {

   public static <T> T run(Supplier<T> r) {
      return run(r, 10);
   }

   public static <T> T run(Supplier<T> r, int seconds) {
      long failTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
      while (failTime > System.currentTimeMillis()) {
         try {
            T result = r.get();
            if (result != null) {
               return result;
            }
            sleep(1_000);
         } catch (Exception e) {
            // silent
         }
      }
      return null;
   }

   public static boolean runUntil(Supplier<Boolean> r, int seconds) {
      boolean run = false;
      long failTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(seconds);
      while (failTime > System.currentTimeMillis()) {
         try {
            Boolean result = r.get();
            if (result) {
               run = true;
               break;
            }
            sleep(1_000);
         } catch (Exception e) {
            // silent
         }
      }
      return run;
   }
}
