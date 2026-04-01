package org.hupo.psi.mi.psicquic;

import org.apache.solr.client.solrj.SolrClient;
import org.hupo.psi.mi.psicquic.indexing.batch.model.SolrInteraction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.repository.config.EnableSolrRepositories;
import org.springframework.data.solr.server.support.EmbeddedSolrServerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@Configuration
@EnableSolrRepositories(basePackages = "org.hupo.psi.mi.psicquic", schemaCreationSupport = true)
public class SolrTestConfig {

    @Bean
    public SolrClient solrClient() throws ParserConfigurationException, IOException, SAXException {
        EmbeddedSolrServerFactory factory = new EmbeddedSolrServerFactory("src/test/resources/solr-home");
        return factory.getSolrClient(SolrInteraction.INTERACTIONS_CORE_NAME);
    }

    @Bean
    public SolrOperations solrTemplate(SolrClient client) {
        return new SolrTemplate(client);
    }

    /*
     * Hack for https://issues.apache.org/jira/browse/SOLR-12858 for embedded POST request issue
     * */
    @Bean
    public boolean isEmbeddedSolr() {
        return true;
    }
}
