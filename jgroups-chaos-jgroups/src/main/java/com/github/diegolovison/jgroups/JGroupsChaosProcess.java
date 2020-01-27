package com.github.diegolovison.jgroups;

import com.github.diegolovison.os.ChaosProcess;

public abstract class JGroupsChaosProcess extends ChaosProcess<JGroupsChaosConfig> {

   public abstract void waitForClusterToForm(int numberOfNodes);
}
