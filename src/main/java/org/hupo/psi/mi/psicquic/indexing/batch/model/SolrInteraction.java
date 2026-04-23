package org.hupo.psi.mi.psicquic.indexing.batch.model;

import org.apache.solr.client.solrj.beans.Field;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.SolrInputField;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.util.Map;

@SolrDocument(solrCoreName = SolrInteraction.INTERACTIONS_CORE_NAME)
public class SolrInteraction extends SolrInputDocument {

    public static final String INTERACTIONS_CORE_NAME = "interactions";
    public static final String UUID_FIELD = "uuId";

    public SolrInteraction() {
    }

    public SolrInteraction(Map<String, SolrInputField> fields) {
        super(fields);
        this.addField(UUID_FIELD, "NEW");
    }

    @Id
    @Field(UUID_FIELD)
    private String uuId;
}
