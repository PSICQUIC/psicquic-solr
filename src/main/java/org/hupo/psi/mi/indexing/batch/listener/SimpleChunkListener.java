package org.hupo.psi.mi.indexing.batch.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

/**
 * TODO comment this
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/05/12</pre>
 */

public class SimpleChunkListener implements StepExecutionListener {

    private static final Log log = LogFactory.getLog(SimpleChunkListener.class);

    private StepExecution stepExecution;
    private long startTime;

    public ExitStatus afterStep(StepExecution stepExecution) {
        return null;
    }

    public void beforeStep(StepExecution stepExecution) {
        this.stepExecution = stepExecution;

        this.startTime = System.currentTimeMillis();
    }

    public void beforeChunk() {
    }

    public void afterChunk() {
        if (!log.isInfoEnabled()) {
            return;
        }

        final int readCount = stepExecution.getReadCount();

        DateTime now = new DateTime();

        log.info("Number of lines read : " + readCount);
    }
}
