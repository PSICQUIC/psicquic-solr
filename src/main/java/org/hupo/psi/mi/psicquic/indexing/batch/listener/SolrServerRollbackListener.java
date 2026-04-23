package org.hupo.psi.mi.psicquic.indexing.batch.listener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hupo.psi.mi.psicquic.indexing.batch.model.SolrInteraction;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.retry.RetryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.RetryListener;

/**
 * This listener will rollback any added documents to the solr server that have not been commited by a SolrItemWriter so in the retry process
 * we don't add the same documents twice in the solr server when a SolrServerException occured
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/04/13</pre>
 */

public class SolrServerRollbackListener implements RetryListener {

    private static final Log log = LogFactory.getLog(SolrServerRollbackListener.class);

    private SolrOperations solrTemplate;
    private String solrCollection;

    int numberOfRetries = 5;

    public <T, E extends Throwable> boolean open(RetryContext context, RetryCallback<T, E> callback) {
        return solrTemplate != null &&solrCollection != null;
    }

    public <T, E extends Throwable> void close(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {
        // do noting
    }

    public <T, E extends Throwable> void onError(RetryContext context, RetryCallback<T, E> callback, Throwable throwable) {

        if (solrTemplate != null){
            try {
                solrTemplate.rollback(solrCollection);
            } catch (Exception e) {
                retryRollback(context, e);
            }
        }
    }

    private void retryRollback(RetryContext context, Exception e) {
        int number = 1;
        boolean didRollback = false;
        while (number < numberOfRetries && !didRollback) {
            try {
                solrTemplate.rollback(SolrInteraction.INTERACTIONS_CORE_NAME);
                didRollback = true;
            } catch (Exception e1) {
                log.error(e1);
                number++;
            }
            if (!didRollback) {
                // stop the job here
                context.setExhaustedOnly();
                log.error("Impossible to rollback added documents while retrying the indexing step.", e);
            }
        }
    }

    public SolrOperations getSolrTemplate() {
        return solrTemplate;
    }

    public void setSolrTemplate(SolrOperations solrTemplate) {
        this.solrTemplate = solrTemplate;
    }

    public String getSolrCollection() {
        return solrCollection;
    }

    public void setSolrCollection(String solrCollection) {
        this.solrCollection = solrCollection;
    }

    public int getNumberOfRetries() {
        return numberOfRetries;
    }

    public void setNumberOfRetries(int numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }
}
