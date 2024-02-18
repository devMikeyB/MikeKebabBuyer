package net.botwithus.debug;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.util.RandomGenerator;
import net.botwithus.rs3.script.Script;
//////Is there a better way to await a condition being met? Please help me Cipher.
public class AwaitCondition {

 public static void await(Supplier<Boolean> condition, long timeout, TimeUnit unit) throws InterruptedException {
     ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
     try {
         Future<?> future = executor.scheduleAtFixedRate(() -> {
             if (condition.get()) {
                 throw new RuntimeException("Condition met");
             }
         }, 0, 1100, TimeUnit.MILLISECONDS);

         executor.schedule(() -> {
             future.cancel(true);
         }, timeout, unit);

         while (!future.isDone()) {
             Thread.sleep(1100);
         }
     } catch (RuntimeException e) {
         if (!e.getMessage().equals("Condition met")) {
             throw e;
         }
     } catch(Exception e){
         ScriptConsole.println(e.getMessage());
     } finally {
         executor.shutdownNow();
     }
 }
}