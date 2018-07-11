package org.hupo.psi.mi.psicquic.model;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocumentList;
import org.hupo.psi.mi.psicquic.indexing.batch.AbstractSolrServerTest;
import org.hupo.psi.mi.psicquic.indexing.batch.SolrMitabIndexer;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import psidev.psi.mi.tab.PsimiTabException;
import psidev.psi.mi.tab.PsimiTabReader;
import psidev.psi.mi.tab.model.BinaryInteraction;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;

/**
 * Tester of MitabInputStream
 *
 * @author Marine Dumousseau (marine@ebi.ac.uk)
 * @version $Id$
 * @since <pre>30/07/12</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration( locations = {"classpath*:/META-INF/psicquic-spring.xml",
        "classpath*:/jobs/psicquic-indexing-spring-test.xml"})
public class MitabInputStreamTest  extends AbstractSolrServerTest {

    @Autowired
    private SolrMitabIndexer solrMitabIndexer;
    private PsimiTabReader mitabReader = new PsimiTabReader();

    @Test
    public void test_create_mitab25_results() throws JobInstanceAlreadyCompleteException, JobParametersInvalidException, JobRestartException, JobExecutionAlreadyRunningException, SolrServerException, PsicquicSolrException, PsimiTabException, IOException {

        solrMitabIndexer.startJob("mitabIndexNegativeJob");

        SolrServer server = solrJettyRunner.getSolrServer();

        SolrDocumentList solrResults = server.query(new SolrQuery("*:*")).getResults();
        MitabInputStream mitabInput = new MitabInputStream(solrResults, PsicquicSolrServer.DATA_FIELDS_25);

        Collection<BinaryInteraction> binaryInteractions = mitabReader.read(mitabInput);
        Assert.assertNotNull(binaryInteractions);
        Assert.assertEquals(4, binaryInteractions.size());

        BinaryInteraction<?> firstBinaryInteraction = binaryInteractions.iterator().next();

        // test idB
        Assert.assertEquals("uniprotkb", firstBinaryInteraction.getInteractorB().getIdentifiers().get(0).getDatabase());
        Assert.assertEquals("P07228", firstBinaryInteraction.getInteractorB().getIdentifiers().get(0).getIdentifier());

        // test AltIdA
        Assert.assertEquals("intact", firstBinaryInteraction.getInteractorA().getAlternativeIdentifiers().get(0).getDatabase());
        Assert.assertEquals("EBI-350432", firstBinaryInteraction.getInteractorA().getAlternativeIdentifiers().get(0).getIdentifier());

        // test pubauth
        Assert.assertEquals("Loo DT et al.(1998)", firstBinaryInteraction.getAuthors().get(0).getName());

        // test pubid
        Assert.assertEquals(2, firstBinaryInteraction.getPublications().size());
        Assert.assertEquals("pubmed", firstBinaryInteraction.getPublications().get(0).getDatabase());
        Assert.assertEquals("9722563", firstBinaryInteraction.getPublications().get(0).getIdentifier());
        Assert.assertEquals("imex", firstBinaryInteraction.getPublications().get(1).getDatabase());
        Assert.assertEquals("IM-17229", firstBinaryInteraction.getPublications().get(1).getIdentifier());

        // test interaction type
        Assert.assertEquals("psi-mi", firstBinaryInteraction.getInteractionTypes().get(0).getDatabase());
        Assert.assertEquals("MI:0915", firstBinaryInteraction.getInteractionTypes().get(0).getIdentifier());
        Assert.assertEquals("physical association", firstBinaryInteraction.getInteractionTypes().get(0).getText());

        // test confidence score
        Assert.assertTrue(firstBinaryInteraction.getConfidenceValues().isEmpty());
    }

    @Test
    public void test_create_mitab26_results() throws JobInstanceAlreadyCompleteException, JobParametersInvalidException, JobRestartException, JobExecutionAlreadyRunningException, SolrServerException, PsicquicSolrException, PsimiTabException, IOException {

        solrMitabIndexer.startJob("mitabIndexNegativeJob");

        SolrServer server = solrJettyRunner.getSolrServer();

        SolrDocumentList solrResults = server.query(new SolrQuery("*:*")).getResults();
        MitabInputStream mitabInput = new MitabInputStream(solrResults, PsicquicSolrServer.DATA_FIELDS_26);

        Collection<BinaryInteraction> binaryInteractions = mitabReader.read(mitabInput);
        Assert.assertNotNull(binaryInteractions);
        Assert.assertEquals(4, binaryInteractions.size());

        BinaryInteraction<?> firstBinaryInteraction = binaryInteractions.iterator().next();

        // test biological role A
        Assert.assertEquals("psi-mi", firstBinaryInteraction.getInteractorA().getBiologicalRoles().get(0).getDatabase());
        Assert.assertEquals("MI:0499", firstBinaryInteraction.getInteractorA().getBiologicalRoles().get(0).getIdentifier());
        Assert.assertEquals("unspecified role", firstBinaryInteraction.getInteractorA().getBiologicalRoles().get(0).getText());

        // test interactor type B
        Assert.assertEquals("psi-mi", firstBinaryInteraction.getInteractorB().getInteractorTypes().get(0).getDatabase());
        Assert.assertEquals("MI:0326", firstBinaryInteraction.getInteractorB().getInteractorTypes().get(0).getIdentifier());
        Assert.assertEquals("protein", firstBinaryInteraction.getInteractorB().getInteractorTypes().get(0).getText());

        // test xref for interaction
        Assert.assertEquals("imex", firstBinaryInteraction.getXrefs().get(0).getDatabase());
        Assert.assertEquals("IM-17229-1", firstBinaryInteraction.getXrefs().get(0).getIdentifier());
        Assert.assertEquals("imex-primary", firstBinaryInteraction.getXrefs().get(0).getText());

        // test parameters of the interaction
        Assert.assertTrue(firstBinaryInteraction.getParameters().isEmpty());

        // test update date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Assert.assertEquals("2012/03/01", simpleDateFormat.format(firstBinaryInteraction.getUpdateDate().get(0)));

        // test checksumB
        Assert.assertEquals("crc64", firstBinaryInteraction.getInteractorB().getChecksums().get(0).getMethodName());
        Assert.assertEquals("2F6FEFCDF2C80457", firstBinaryInteraction.getInteractorB().getChecksums().get(0).getChecksum());

        // test negative
        Assert.assertTrue(firstBinaryInteraction.isNegativeInteraction());
    }

    @Test
    public void test_create_mitab27_results() throws JobInstanceAlreadyCompleteException, JobParametersInvalidException, JobRestartException, JobExecutionAlreadyRunningException, SolrServerException, PsicquicSolrException, PsimiTabException, IOException {

        solrMitabIndexer.startJob("mitabIndexNegativeJob");

        SolrServer server = solrJettyRunner.getSolrServer();

        SolrDocumentList solrResults = server.query(new SolrQuery("*:*")).getResults();
        MitabInputStream mitabInput = new MitabInputStream(solrResults, PsicquicSolrServer.DATA_FIELDS_27);

        Collection<BinaryInteraction> binaryInteractions = mitabReader.read(mitabInput);
        Assert.assertNotNull(binaryInteractions);
        Assert.assertEquals(4, binaryInteractions.size());

        BinaryInteraction<?> firstBinaryInteraction = binaryInteractions.iterator().next();

        // test features
        Assert.assertEquals("necessary binding region", firstBinaryInteraction.getInteractorA().getFeatures().get(0).getFeatureType());
        Assert.assertEquals("2171-2647", firstBinaryInteraction.getInteractorA().getFeatures().get(0).getRanges().get(0));
        Assert.assertEquals("necessary binding region", firstBinaryInteraction.getInteractorB().getFeatures().get(0).getFeatureType());
        Assert.assertEquals("757-800", firstBinaryInteraction.getInteractorB().getFeatures().get(0).getRanges().get(0));
        Assert.assertEquals("mutation decreasing interaction", firstBinaryInteraction.getInteractorB().getFeatures().get(1).getFeatureType());
        Assert.assertEquals("764-764", firstBinaryInteraction.getInteractorB().getFeatures().get(1).getRanges().get(0));

        // test stoichiometries
        Assert.assertTrue(firstBinaryInteraction.getInteractorA().getStoichiometry().isEmpty());
        Assert.assertTrue(firstBinaryInteraction.getInteractorB().getStoichiometry().isEmpty());

        // test participant identification methods
        Assert.assertEquals("psi-mi", firstBinaryInteraction.getInteractorA().getParticipantIdentificationMethods().get(0).getDatabase());
        Assert.assertEquals("MI:0396", firstBinaryInteraction.getInteractorA().getParticipantIdentificationMethods().get(0).getIdentifier());
        Assert.assertEquals("predetermined participant", firstBinaryInteraction.getInteractorA().getParticipantIdentificationMethods().get(0).getText());
        Assert.assertEquals("psi-mi", firstBinaryInteraction.getInteractorB().getParticipantIdentificationMethods().get(0).getDatabase());
        Assert.assertEquals("MI:0396", firstBinaryInteraction.getInteractorB().getParticipantIdentificationMethods().get(0).getIdentifier());
        Assert.assertEquals("predetermined participant", firstBinaryInteraction.getInteractorB().getParticipantIdentificationMethods().get(0).getText());
    }

    @Test
    public void test_create_mitab28_results() throws JobInstanceAlreadyCompleteException, JobParametersInvalidException, JobRestartException, JobExecutionAlreadyRunningException, SolrServerException, PsicquicSolrException, PsimiTabException, IOException {

        solrMitabIndexer.startJob("mitabIndexMitab28Job");

        SolrServer server = solrJettyRunner.getSolrServer();

        SolrDocumentList solrResults = server.query(new SolrQuery("idA:P05556")).getResults();
        MitabInputStream mitabInput = new MitabInputStream(solrResults, PsicquicSolrServer.DATA_FIELDS_28);

        Collection<BinaryInteraction> binaryInteractions = mitabReader.read(mitabInput);
        Assert.assertNotNull(binaryInteractions);
        Assert.assertEquals(1, binaryInteractions.size());

        BinaryInteraction<?> binaryInteraction = binaryInteractions.iterator().next();

        // test idB
        Assert.assertEquals("uniprotkb", binaryInteraction.getInteractorB().getIdentifiers().get(0).getDatabase());
        Assert.assertEquals("P21333", binaryInteraction.getInteractorB().getIdentifiers().get(0).getIdentifier());

        // test pubauth
        Assert.assertTrue(binaryInteraction.getAuthors().isEmpty());

        // test interaction type
        Assert.assertEquals("psi-mi", binaryInteraction.getInteractionTypes().get(0).getDatabase());
        Assert.assertEquals("MI:0915", binaryInteraction.getInteractionTypes().get(0).getIdentifier());
        Assert.assertEquals("physical association", binaryInteraction.getInteractionTypes().get(0).getText());

        // test interactor type B
        Assert.assertEquals("psi-mi", binaryInteraction.getInteractorB().getInteractorTypes().get(0).getDatabase());
        Assert.assertEquals("MI:0326", binaryInteraction.getInteractorB().getInteractorTypes().get(0).getIdentifier());
        Assert.assertEquals("protein", binaryInteraction.getInteractorB().getInteractorTypes().get(0).getText());

        // test create date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Assert.assertEquals("2012/02/23", simpleDateFormat.format(binaryInteraction.getCreationDate().get(0)));

        // test negative
        Assert.assertTrue(binaryInteraction.isNegativeInteraction());

        // test features and stoichiometries
        Assert.assertTrue(binaryInteraction.getInteractorA().getFeatures().isEmpty());
        Assert.assertTrue(binaryInteraction.getInteractorB().getFeatures().isEmpty());
        Assert.assertTrue(binaryInteraction.getInteractorA().getStoichiometry().isEmpty());
        Assert.assertTrue(binaryInteraction.getInteractorB().getStoichiometry().isEmpty());

        // test participant identification methods
        Assert.assertEquals("psi-mi", binaryInteraction.getInteractorA().getParticipantIdentificationMethods().get(0).getDatabase());
        Assert.assertEquals("MI:0113", binaryInteraction.getInteractorA().getParticipantIdentificationMethods().get(0).getIdentifier());
        Assert.assertEquals("western blot", binaryInteraction.getInteractorA().getParticipantIdentificationMethods().get(0).getText());
        Assert.assertEquals("psi-mi", binaryInteraction.getInteractorB().getParticipantIdentificationMethods().get(0).getDatabase());
        Assert.assertEquals("MI:0113", binaryInteraction.getInteractorB().getParticipantIdentificationMethods().get(0).getIdentifier());
        Assert.assertEquals("western blot", binaryInteraction.getInteractorB().getParticipantIdentificationMethods().get(0).getText());

        // test biological effects
        Assert.assertEquals("go", binaryInteraction.getInteractorA().getBiologicalEffects().get(0).getDatabase());
        Assert.assertEquals("GO:0016301", binaryInteraction.getInteractorA().getBiologicalEffects().get(0).getIdentifier());
        Assert.assertEquals("kinase activity", binaryInteraction.getInteractorA().getBiologicalEffects().get(0).getText());
        Assert.assertEquals("go", binaryInteraction.getInteractorB().getBiologicalEffects().get(0).getDatabase());
        Assert.assertEquals("GO:0016301", binaryInteraction.getInteractorB().getBiologicalEffects().get(0).getIdentifier());
        Assert.assertEquals("kinase activity", binaryInteraction.getInteractorB().getBiologicalEffects().get(0).getText());

        // test causal regulatory mechanism
        Assert.assertEquals("psi-mi", binaryInteraction.getCausalRegulatoryMechanism().get(0).getDatabase());
        Assert.assertEquals("MI:2249", binaryInteraction.getCausalRegulatoryMechanism().get(0).getIdentifier());
        Assert.assertEquals("post transcriptional regulation", binaryInteraction.getCausalRegulatoryMechanism().get(0).getText());

        // test causal statement
        Assert.assertEquals("psi-mi", binaryInteraction.getCausalStatement().get(0).getDatabase());
        Assert.assertEquals("MI:2240", binaryInteraction.getCausalStatement().get(0).getIdentifier());
        Assert.assertEquals("down regulates", binaryInteraction.getCausalStatement().get(0).getText());
    }
}