package org.hupo.psi.mi.psicquic.indexing.batch.writer;

import org.apache.solr.common.SolrInputDocument;
import org.hupo.psi.calimocho.model.Row;
import org.hupo.psi.mi.psicquic.indexing.batch.converter.SolrInputDocumentConverter;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.solr.core.SolrOperations;

import java.util.ArrayList;
import java.util.List;

/**
 * Solr item writer
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/05/12</pre>
 */

public class SolrItemWriter<T extends SolrInputDocument> implements ItemWriter<Row>, ItemStream {

    private SolrOperations solrTemplate;
    private String solrCollection;
    private SolrInputDocumentConverter<T> solrConverter;

    private boolean needToCommitOnClose;

    /**
     * Index a list of calimocho rows in SOLR
     * @param items: items to write
     * @throws Exception if anything
     */
    public void write(List<? extends Row> items) throws Exception {
        needToCommitOnClose = false;

        if (solrTemplate == null) {
            throw new IllegalStateException("No 'solrTemplate' configured for SolrItemWriter");
        }
        if (solrCollection == null) {
            throw new IllegalStateException("No 'solrCollection' configured for SolrItemWriter");
        }

        if (items.isEmpty()) {
            return;
        }

        List<SolrInputDocument> solrInputDocuments = new ArrayList<>();
        for (Row row : items) {
            solrInputDocuments.add(solrConverter.toSolrDocument(row));
        }
        solrTemplate.saveDocuments(solrCollection, solrInputDocuments);
    }

    public void open(ExecutionContext executionContext) throws ItemStreamException {
    }

    public void update(ExecutionContext executionContext) throws ItemStreamException {
        if (solrTemplate != null) {
            try {
                solrTemplate.commit(solrCollection);
                needToCommitOnClose = true;
            } catch (Exception e) {
                throw new ItemStreamException("Problem committing the results.", e);
            }
        }
    }

    public void close() throws ItemStreamException {
        if (solrTemplate != null) {
            try {
                if (needToCommitOnClose) {
                    solrTemplate.commit(solrCollection);
                }
            } catch (Exception e) {
                throw new ItemStreamException("Problem committing the results.", e);
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

    public SolrInputDocumentConverter<T> getSolrConverter() {
        return solrConverter;
    }

    public void setSolrConverter(SolrInputDocumentConverter<T> solrConverter) {
        this.solrConverter = solrConverter;
    }
}
