package org.hupo.psi.mi.psicquic.indexing.batch.converter;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrInputDocument;
import org.hupo.psi.calimocho.io.IllegalFieldException;
import org.hupo.psi.calimocho.model.Row;

public interface SolrInputDocumentConverter<T extends SolrInputDocument> {

    T toSolrDocument(Row row) throws SolrServerException, IllegalFieldException;
}
