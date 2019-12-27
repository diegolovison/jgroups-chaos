package com.github.diegolovison.infinispan.cache;

public abstract class ChaosCache {

   private String cacheName;

   public ChaosCache(String cacheName) {
      this.cacheName = cacheName;
   }

   public abstract String put(String key, String value);

   public abstract String get(String key);

   public abstract boolean isConflictResolutionInProgress();

   public abstract boolean isStateTransferInProgress();
}
