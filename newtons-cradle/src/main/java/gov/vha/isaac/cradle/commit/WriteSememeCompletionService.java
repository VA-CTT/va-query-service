/*
 * Copyright 2015 kec.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gov.vha.isaac.cradle.commit;

import gov.vha.isaac.ochre.api.commit.Alert;
import gov.vha.isaac.ochre.api.commit.ChangeChecker;
import gov.vha.isaac.ochre.api.sememe.SememeChronicle;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javafx.concurrent.Task;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author kec
 */
public class WriteSememeCompletionService implements Runnable {
    private static final Logger log = LogManager.getLogger();
    private final ExecutorService writeSememePool = Executors.newFixedThreadPool(2);
    private boolean run = true;

    ExecutorCompletionService<Void> conversionService = new ExecutorCompletionService(writeSememePool);
    
    public Task<Void> checkAndWrite(SememeChronicle sc, 
            ConcurrentSkipListSet<ChangeChecker> checkers,
            ConcurrentSkipListSet<Alert> alertCollection, Semaphore writeSemaphore) {
        writeSemaphore.acquireUninterruptibly();
        WriteAndCheckSememeChronicle task = 
                new WriteAndCheckSememeChronicle(sc, checkers, alertCollection,
                writeSemaphore);
        conversionService.submit(task);
        return task;
    }

    public Task<Void> write(SememeChronicle sc, Semaphore writeSemaphore) {
        writeSemaphore.acquireUninterruptibly();
        WriteSememeChronicle task = new WriteSememeChronicle(sc, writeSemaphore);
        conversionService.submit(task);
        return task;
    }

    @Override
    public void run() {
        while (run) {
            try {
                Future<Void> task = conversionService.poll(500, TimeUnit.MILLISECONDS);
                if (task != null) {
                    task.get();
                }
                if (writeSememePool.isTerminated()) {
                    run = false;
                }
            } catch (InterruptedException ex) {
                log.warn(ex.getLocalizedMessage(), ex);
            } catch (ExecutionException ex) {
                log.error(ex.getLocalizedMessage(), ex);
            }
        }
        System.out.println("WriteConceptCompletionService closed");
    }
    
    public void cancel() {
        try {
            writeSememePool.shutdown();
            writeSememePool.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
