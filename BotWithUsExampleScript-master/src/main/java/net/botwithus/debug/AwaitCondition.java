package net.botwithus.debug;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import net.botwithus.rs3.script.ScriptConsole;
public class AwaitCondition {

    public static void await(Supplier<Boolean> condition, long timeout, TimeUnit unit) throws InterruptedException {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        long startTime = System.currentTimeMillis();
        long delay = unit.toMillis(timeout);

        try {
            Future<?> future = executor.scheduleAtFixedRate(() -> {
                if (System.currentTimeMillis() - startTime < delay && condition.get()) {
                    throw new RuntimeException("Condition met");
                }
            }, 0, 590, TimeUnit.MILLISECONDS);

            executor.schedule(() -> {
                future.cancel(true);
            }, timeout, unit);

            while (!future.isDone()) {
                Thread.sleep(590);
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
