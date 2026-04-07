package org.hupo.psi.mi.psicquic.indexing.batch.writer;

import org.apache.solr.common.SolrInputDocument;
import org.hupo.psi.calimocho.model.Row;
import org.hupo.psi.mi.psicquic.indexing.batch.converter.SolrInputDocumentConverter;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.solr.repository.SolrCrudRepository;

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

    private SolrCrudRepository<T, String> solrCrudRepository;
    protected SolrInputDocumentConverter<T> solrConverter;

    /**
     * Index a list of calimocho rows in SOLR
     * @param items: items to write
     * @throws Exception if anything
     */
    public void write(List<? extends Row> items) throws Exception {
        if (items.isEmpty()) {
            return;
        }

        List<T> solrInputDocuments = new ArrayList<>();
        for (Row row : items) {
            solrInputDocuments.add(solrConverter.toSolrDocument(row));
        }
        solrCrudRepository.save(solrInputDocuments);
    }

    public void open(ExecutionContext executionContext) throws ItemStreamException {
    }

    public void update(ExecutionContext executionContext) throws ItemStreamException {
    }

    public void close() throws ItemStreamException {
    }

    public SolrCrudRepository<T, String> getSolrCrudRepository() {
        return solrCrudRepository;
    }

    public void setSolrCrudRepository(SolrCrudRepository<T, String> solrCrudRepository) {
        this.solrCrudRepository = solrCrudRepository;
    }

    public SolrInputDocumentConverter<T> getSolrConverter() {
        return solrConverter;
    }

    public void setSolrConverter(SolrInputDocumentConverter<T> solrConverter) {
        this.solrConverter = solrConverter;
    }
}
