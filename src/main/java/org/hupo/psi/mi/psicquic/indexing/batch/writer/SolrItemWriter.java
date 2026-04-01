package org.hupo.psi.mi.psicquic.indexing.batch.writer;

import org.apache.solr.common.SolrInputDocument;
import org.hupo.psi.calimocho.model.Row;
import org.hupo.psi.mi.psicquic.indexing.batch.model.SolrInteraction;
import org.hupo.psi.mi.psicquic.indexing.batch.repository.InteractionRepository;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemWriter;
import psidev.psi.mi.calimocho.solr.converter.Converter;

import java.util.ArrayList;
import java.util.List;

/**
 * Solr item writer
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>29/05/12</pre>
 */

public class SolrItemWriter implements ItemWriter<Row>, ItemStream {

    private InteractionRepository interactionRepository;
    protected Converter solrConverter;

    public SolrItemWriter(){
        solrConverter = new Converter();
    }

    /**
     * Index a list of calimocho rows in SOLR
     * @param items: items to write
     * @throws Exception if anything
     */
    public void write(List<? extends Row> items) throws Exception {
        if (items.isEmpty()) {
            return;
        }

        List<SolrInteraction> solrInputDocuments = new ArrayList<>();
        for (Row row : items) {
            solrInputDocuments.add(new SolrInteraction(solrConverter.toSolrDocument(row)));
        }
        interactionRepository.save(solrInputDocuments);
    }

    public void open(ExecutionContext executionContext) throws ItemStreamException {
    }

    public void update(ExecutionContext executionContext) throws ItemStreamException {
    }

    public void close() throws ItemStreamException {
    }

    public InteractionRepository getInteractionRepository() {
        return interactionRepository;
    }

    public void setInteractionRepository(InteractionRepository interactionRepository) {
        this.interactionRepository = interactionRepository;
    }
}
