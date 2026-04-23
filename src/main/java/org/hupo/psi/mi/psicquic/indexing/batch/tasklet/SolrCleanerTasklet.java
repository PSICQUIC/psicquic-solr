package org.hupo.psi.mi.psicquic.indexing.batch.tasklet;

import org.apache.solr.common.SolrInputDocument;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.query.SimpleQuery;

/**
 * clean solr
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/05/12</pre>
 */

public class SolrCleanerTasklet<T extends SolrInputDocument> implements Tasklet {

    private SolrOperations solrTemplate;
    private String solrCollection;

    public SolrCleanerTasklet() {
    }

    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        if (solrTemplate != null){

            // delete all previous records
            solrTemplate.delete(solrCollection, new SimpleQuery("*:*"));

            // optimize here
            solrTemplate.commit(solrCollection);

            contribution.getExitStatus().addExitDescription("Cleared: " + solrCollection);
        }
        else {
            throw new IllegalStateException("no SOLR server url found.");
        }

        return RepeatStatus.FINISHED;
    }

    public void setSolrTemplate(SolrOperations solrTemplate) {
        this.solrTemplate = solrTemplate;
    }

    public void setSolrCollection(String solrCollection) {
        this.solrCollection = solrCollection;
    }
}
