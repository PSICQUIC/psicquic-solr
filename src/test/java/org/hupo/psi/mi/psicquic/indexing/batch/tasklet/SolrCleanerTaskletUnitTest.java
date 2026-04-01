package org.hupo.psi.mi.psicquic.indexing.batch.tasklet;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.common.SolrInputDocument;
import org.hupo.psi.calimocho.model.Row;
import org.hupo.psi.mi.psicquic.indexing.batch.model.SolrInteraction;
import org.hupo.psi.mi.psicquic.indexing.batch.reader.MitabCalimochoLineMapper;
import org.hupo.psi.mi.psicquic.indexing.batch.repository.InteractionRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import psidev.psi.mi.calimocho.solr.converter.Converter;

/**
 * Unit tester of SolrCleaner tasklet
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>12/07/12</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = {
        "classpath*:/META-INF/psicquic-spring.xml",
        "classpath*:/jobs/psicquic-indexing-spring-test.xml"
})
public class SolrCleanerTaskletUnitTest {

    @Autowired
    private InteractionRepository interactionRepository;
    @Autowired
    private SolrClient solrClient;

    @Before
    public void clearRepo() {
        interactionRepository.deleteAll();
    }

    @Test
    public void test_delete_all() throws Exception {

        SolrCleanerTasklet tasklet = new SolrCleanerTasklet();
        tasklet.setInteractionRepository(interactionRepository);

        // add some data to the solrServer
        Converter solrConverter = new Converter();
        MitabCalimochoLineMapper mitabLineMapper = new MitabCalimochoLineMapper();

        String mitab27 = "uniprotkb:P73045\tintact:EBI-1579103\tintact:EBI-1607518\tintact:EBI-1607516\tuniprotkb:slr1767(locus name)\tuniprotkb:alias2(gene name)\tpsi-mi:\"MI:0018\"(two hybrid)\tauthor et al.(2007)\tpubmed:18000013\ttaxid:4932(yeasx)|taxid:4932(\"Saccharomyces cerevisiae (Baker's yeast)\")\ttaxid:1142(9sync)|taxid:1142(Synechocystis)\tpsi-mi:\"MI:0915\"(physical association)\tpsi-mi:\"MI:0469\"(IntAct)\tintact:EBI-1607514\tauthor-score:C\t-\tpsi-mi:\"MI:0499\"(unspecified role)\tpsi-mi:\"MI:0499\"(unspecified role)\tpsi-mi:\"MI:0496\"(bait)\tpsi-mi:\"MI:0498\"(prey)\tpsi-mi:\"MI:0326\"(protein)\tpsi-mi:\"MI:0326\"(protein)\trefseq:NP_440386.1\t-\t-\t-\t-\t-\t-\t-\t2008/01/14\t2008/09/22\tcrc64:9E0E98F314F90177\t-\tintact-crc:3E26AC3853066993\t-\t-\t-\t-\t-\tpsi-mi:\"MI:0078\"(nucleotide sequence identification)\tpsi-mi:\"MI:0078\"(nucleotide sequence identification)\n";
        Row row = mitabLineMapper.mapLine(mitab27, 0);

        // index data to be hosted by PSICQUIC : we should have one result
        SolrInteraction solrInputDoc = new SolrInteraction(solrConverter.toSolrDocument(row));
        interactionRepository.save(solrInputDoc);

        Assert.assertEquals(1L, solrClient.query(new SolrQuery("*:*")).getResults().getNumFound());

        // run the tasklet
        StepExecution stepExecution = new StepExecution("stepTest", new JobExecution(1L));
        StepContribution stepContribution = new StepContribution(stepExecution);
        RepeatStatus status = tasklet.execute(stepContribution, new ChunkContext(new StepContext(stepExecution)));

        // the index should be empty
        Assert.assertEquals(RepeatStatus.FINISHED, status);
        Assert.assertEquals(0L, solrClient.query(new SolrQuery("*:*")).getResults().getNumFound());
    }
}
