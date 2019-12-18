package com.github.diegolovison.jgroups;

import com.github.diegolovison.os.ChaosProcess;

public abstract class JGroupsChaosProcess<T> extends ChaosProcess<T> {

   public abstract void waitForClusterToForm(int numberOfNodes);
}
