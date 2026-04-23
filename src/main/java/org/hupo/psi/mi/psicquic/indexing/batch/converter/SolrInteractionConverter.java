package org.hupo.psi.mi.psicquic.indexing.batch.converter;

import org.apache.solr.client.solrj.SolrServerException;
import org.hupo.psi.calimocho.io.IllegalFieldException;
import org.hupo.psi.calimocho.model.Row;
import org.hupo.psi.mi.psicquic.indexing.batch.model.SolrInteraction;
import psidev.psi.mi.calimocho.solr.converter.Converter;

public class SolrInteractionConverter extends Converter implements SolrInputDocumentConverter<SolrInteraction>  {

    @Override
    public SolrInteraction toSolrDocument(Row row) throws SolrServerException, IllegalFieldException {
        return new SolrInteraction(super.toSolrDocument(row));
    }
}
