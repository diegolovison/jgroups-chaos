package com.github.diegolovison.os;

import static com.github.diegolovison.jgroups.Sleep.sleep;

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class Eventually {

   public static void run(Supplier<Boolean> r) {
      long failTime = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10);
      while (failTime > System.currentTimeMillis()) {
         try {
            if (r.get()) {
               break;
            }
            sleep(500);
         } catch (Exception e) {
            // silent TODO really ?
         }
      }
   }
}
