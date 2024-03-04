package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.TaintLabel;

import java.util.Objects;

public class PreprocessingLeakageInstance implements LeakageInstance {
   private final String train;
   private final int lineNumber;
   private final LeakageType type;
   private final Invocation invocation;
   private final LeakageSource leakageSource;

   public PreprocessingLeakageInstance(int lineNumber, Invocation invocation) {
      this.lineNumber = lineNumber;
      this.type = LeakageType.PreprocessingLeakage;
      this.invocation = invocation;
      this.train = Utils.getTrainFromPreprocessingLeakTelemetryFile();
      this.leakageSource = new LeakageSource(this.type);
   }


   @Override
   public int lineNumber() {
      return lineNumber;
   }

   @Override
   public LeakageType type() {
      return type;
   }

   @Override
   public Invocation invocation() {
      return invocation;
   }

   @Override
   public String variableName() {
      return train;
   }

   public LeakageSource getLeakageSource() {
      return leakageSource;
   }

   public Taint findTaintThatMatchesText(String text) {
      return this.getLeakageSource().getTaints().stream().filter(
            taint -> taint.getPyCallExpression().equalsIgnoreCase(text) //equalsIgnoreCase MUST be used here
      ).findFirst().orElse(new Taint("", TaintLabel.rowset));//TODO: better error handling
   }

   //    @Override
//    public boolean equals(Object obj){
//        return this.lineNumber() == ((PreprocessingLeakageInstance)obj).lineNumber()
//                &&this.invocation.getNumber() == ((PreprocessingLeakageInstance)obj).invocation().getNumber();
//    }
   @Override
   public boolean equals(Object obj) {
      if (obj == null || getClass() != obj.getClass()) return false;
      PreprocessingLeakageInstance otherInstance = (PreprocessingLeakageInstance) obj;
      return this.lineNumber() == otherInstance.lineNumber()
            && this.invocation().getNumber() == otherInstance.invocation().getNumber();
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.lineNumber(), this.invocation());
   }
}
