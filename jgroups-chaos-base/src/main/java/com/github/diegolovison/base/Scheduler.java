package com.github.diegolovison.base;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Scheduler {

   private static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

   public static void schedule(Runnable run, long delayMs) {
      scheduler.schedule(() -> {
         try {
            run.run();
         } catch (Exception e) {
            // silent
         }
      }, delayMs, TimeUnit.MILLISECONDS);
   }

   public static void schedule(Runnable run, long delay, TimeUnit timeUnit) {
      scheduler.schedule(() -> {
         try {
            run.run();
         } catch (Exception e) {
            // silent
         }
      }, delay, timeUnit);
   }
}
