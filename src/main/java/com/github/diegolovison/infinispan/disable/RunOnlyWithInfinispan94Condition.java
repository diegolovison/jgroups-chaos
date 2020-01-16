package com.github.diegolovison.infinispan.disable;

import org.infinispan.Version;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;

public class RunOnlyWithInfinispan94Condition implements ExecutionCondition {
   @Override
   public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
      String infinispanVersion = Version.getMajorMinor();
      if(infinispanVersion.equalsIgnoreCase("9.4")) {
         return ConditionEvaluationResult.enabled("Test enabled");
      } else {
         return ConditionEvaluationResult.disabled("Test disabled because the Infinispan version is not 94");
      }
   }
}
