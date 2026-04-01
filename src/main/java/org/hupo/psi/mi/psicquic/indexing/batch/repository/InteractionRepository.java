package org.hupo.psi.mi.psicquic.indexing.batch.repository;

import org.hupo.psi.mi.psicquic.indexing.batch.model.SolrInteraction;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InteractionRepository extends SolrCrudRepository<SolrInteraction, String> {
}
