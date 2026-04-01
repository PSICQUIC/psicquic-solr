package org.hupo.psi.mi.psicquic.indexing.batch.tasklet;

import org.hupo.psi.mi.psicquic.indexing.batch.repository.InteractionRepository;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * clean solr
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/05/12</pre>
 */

public class SolrCleanerTasklet implements Tasklet {

    private InteractionRepository interactionRepository;

    public SolrCleanerTasklet() {
    }

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        // delete all previous records
        interactionRepository.deleteAll();
        contribution.getExitStatus().addExitDescription("Cleared.");
        return RepeatStatus.FINISHED;
    }

    public InteractionRepository getInteractionRepository() {
        return interactionRepository;
    }

    public void setInteractionRepository(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }
}
