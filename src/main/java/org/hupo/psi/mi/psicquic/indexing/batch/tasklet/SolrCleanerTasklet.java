package org.hupo.psi.mi.psicquic.indexing.batch.tasklet;

import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 * clean solr
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/05/12</pre>
 */

public class SolrCleanerTasklet<T> implements Tasklet {

    private SolrCrudRepository<T, String> solrCrudRepository;

    public SolrCleanerTasklet() {
    }

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        // delete all previous records
        solrCrudRepository.deleteAll();
        contribution.getExitStatus().addExitDescription("Cleared.");
        return RepeatStatus.FINISHED;
    }

    public SolrCrudRepository<T, String> getSolrCrudRepository() {
        return solrCrudRepository;
    }

    public void setSolrCrudRepository(SolrCrudRepository<T, String> solrCrudRepository) {
        this.solrCrudRepository = solrCrudRepository;
    }
}
