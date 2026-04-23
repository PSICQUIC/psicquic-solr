package org.hupo.psi.mi.psicquic.indexing.batch.writer;

import org.apache.solr.client.solrj.SolrQuery;
import org.hupo.psi.calimocho.model.Row;
import org.hupo.psi.mi.psicquic.indexing.batch.converter.SolrInteractionConverter;
import org.hupo.psi.mi.psicquic.indexing.batch.model.SolrInteraction;
import org.hupo.psi.mi.psicquic.indexing.batch.reader.MitabCalimochoLineMapper;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * Unit tester for SolrItemWriter
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>16/07/12</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = {
        "classpath*:/META-INF/psicquic-spring.xml",
        "classpath*:/jobs/psicquic-indexing-spring-test.xml"
})
public class SolrItemWriterUnitTest {

    @Autowired
    private SolrOperations solrTemplate;
    @Autowired
    private SolrInteractionConverter solrInteractionConverter;

    @Before
    public void clearRepo() {
        solrTemplate.delete(SolrInteraction.INTERACTIONS_CORE_NAME, new SimpleQuery("*:*"));
    }

    @Test
    public void test_write_mitab27_row() throws Exception {
        SolrItemWriter<SolrInteraction> writer = new SolrItemWriter<>();
        writer.setSolrTemplate(solrTemplate);
        writer.setSolrCollection(SolrInteraction.INTERACTIONS_CORE_NAME);
        writer.setSolrConverter(solrInteractionConverter);

        // add some data to the solrServer using writer
        MitabCalimochoLineMapper mitabLineMapper = new MitabCalimochoLineMapper();

        String mitab27Line = "uniprotkb:P73045\tintact:EBI-1579103\tintact:EBI-1607518\tintact:EBI-1607516\tuniprotkb:slr1767(locus name)\tuniprotkb:alias2(gene name)\tpsi-mi:\"MI:0018\"(two hybrid)\tauthor et al.(2007)\tpubmed:18000013\ttaxid:4932(yeasx)|taxid:4932(\"Saccharomyces cerevisiae (Baker's yeast)\")\ttaxid:1142(9sync)|taxid:1142(Synechocystis)\tpsi-mi:\"MI:0915\"(physical association)\tpsi-mi:\"MI:0469\"(IntAct)\tintact:EBI-1607514\tauthor-score:C\t-\tpsi-mi:\"MI:0499\"(unspecified role)\tpsi-mi:\"MI:0499\"(unspecified role)\tpsi-mi:\"MI:0496\"(bait)\tpsi-mi:\"MI:0498\"(prey)\tpsi-mi:\"MI:0326\"(protein)\tpsi-mi:\"MI:0326\"(protein)\trefseq:NP_440386.1\t-\t-\t-\t-\t-\t-\t-\t2008/01/14\t2008/09/22\tcrc64:9E0E98F314F90177\t-\tintact-crc:3E26AC3853066993\t-\t-\t-\t-\t-\tpsi-mi:\"MI:0078\"(nucleotide sequence identification)\tpsi-mi:\"MI:0078\"(nucleotide sequence identification)\n";
        Row row = mitabLineMapper.mapLine(mitab27Line, 0);

        // index data to be hosted by PSICQUIC : we should have one result
        ExecutionContext context = new ExecutionContext();
        writer.open(context);
        writer.write(List.of(row));
        writer.update(context);
        writer.close();

        Assert.assertEquals(1L, solrTemplate.getSolrClient().query(SolrInteraction.INTERACTIONS_CORE_NAME, new SolrQuery("*:*")).getResults().getNumFound());
    }

    @Test
    public void test_write_diff_mitab_version_rows() throws Exception {
        SolrItemWriter<SolrInteraction> writer = new SolrItemWriter<>();
        writer.setSolrTemplate(solrTemplate);
        writer.setSolrCollection(SolrInteraction.INTERACTIONS_CORE_NAME);
        writer.setSolrConverter(solrInteractionConverter);

        // add some data to the solrServer using writer
        MitabCalimochoLineMapper mitabLineMapper = new MitabCalimochoLineMapper();

        String mitab27Line = "uniprotkb:P73045\tintact:EBI-1579103\tintact:EBI-1607518\tintact:EBI-1607516\tuniprotkb:slr1767(locus name)\tuniprotkb:alias2(gene name)\tpsi-mi:\"MI:0018\"(two hybrid)\tauthor et al.(2007)\tpubmed:18000013\ttaxid:4932(yeasx)|taxid:4932(\"Saccharomyces cerevisiae (Baker's yeast)\")\ttaxid:1142(9sync)|taxid:1142(Synechocystis)\tpsi-mi:\"MI:0915\"(physical association)\tpsi-mi:\"MI:0469\"(IntAct)\tintact:EBI-1607514\tauthor-score:C\t-\tpsi-mi:\"MI:0499\"(unspecified role)\tpsi-mi:\"MI:0499\"(unspecified role)\tpsi-mi:\"MI:0496\"(bait)\tpsi-mi:\"MI:0498\"(prey)\tpsi-mi:\"MI:0326\"(protein)\tpsi-mi:\"MI:0326\"(protein)\trefseq:NP_440386.1\t-\t-\t-\t-\t-\t-\t-\t2008/01/14\t2008/09/22\tcrc64:9E0E98F314F90177\t-\tintact-crc:3E26AC3853066993\t-\t-\t-\t-\t-\tpsi-mi:\"MI:0078\"(nucleotide sequence identification)\tpsi-mi:\"MI:0078\"(nucleotide sequence identification)\n";
        String mitab28Line = mitab27Line + "\t-\t-\tpsi-mi:\"MI:2247\"(transcriptional regulation)\tpsi-mi:\"MI:2236\"(up-regulates activity)";
        Row firstRow = mitabLineMapper.mapLine(mitab27Line, 0);
        Row secondRow = mitabLineMapper.mapLine(mitab28Line, 1);

        // index data to be hosted by PSICQUIC : we should have 2 results
        ExecutionContext context = new ExecutionContext();
        writer.open(context);
        writer.write(List.of(firstRow));
        writer.write(List.of(secondRow));
        writer.update(context);
        writer.close();

        Assert.assertEquals(2L, solrTemplate.getSolrClient().query(SolrInteraction.INTERACTIONS_CORE_NAME, new SolrQuery("*:*")).getResults().getNumFound());
    }
}
