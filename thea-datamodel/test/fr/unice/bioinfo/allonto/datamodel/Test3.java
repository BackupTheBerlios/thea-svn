package fr.unice.bioinfo.allonto.datamodel;

import junit.framework.*;
import fr.unice.bioinfo.allonto.datamodel.Property;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.datamodel.Context;
import fr.unice.bioinfo.allonto.datamodel.expression.Criterion;
import fr.unice.bioinfo.allonto.datamodel.expression.Expression;
import fr.unice.bioinfo.allonto.util.AllontoFactory;

import java.util.Set;

public class Test3 extends TestCase {
    private static ResourceFactory resourceFactory = (ResourceFactory)AllontoFactory.getResourceFactory();

    protected Property isa_rel, sub_rel, pSpecializes;
    protected Property pExpressedIn, pExpressedAt, pAnnotatedWith;

    protected Resource rGeneWG;
    protected Resource rStageEmbryo, rStageLarva;
    protected Resource rPositionAnalia, rPositionEpidermis, rPositionHead, rPositionDorsal, rPositionVentral;
    protected Resource pRef, pEvidence;
    protected Resource rFunctionA;
    protected Resource rEvidenceISS, rEvidenceBlast;

    public static void main(String args[]) {
	junit.textui.TestRunner.run(Test3.class);
    }

    protected void setUp() {
	// Retrieves is_a property
        isa_rel = resourceFactory.getProperty("SUBSUME");
	// Retrieves the inverse property of is-a
	sub_rel = isa_rel.getInverse();

        pSpecializes = resourceFactory.getProperty("SPECIALIZES");

        // Wingless is expressed in embryo (analia, epidermis, head), larva (dorsal mesothoracic disc, ventral thoracic disc).
        rGeneWG = new Resource();
        rStageEmbryo = new Resource();
        rStageLarva = new Resource();
        rPositionAnalia = new Resource();
        rPositionEpidermis = new Resource();
        rPositionHead = new Resource();
        rPositionDorsal = new Resource();
        rPositionVentral = new Resource();
        
        rFunctionA = new Resource();
        rEvidenceISS = new Resource();
        rEvidenceBlast = new Resource();
        
        // creates property pAnnotatedWith
	pExpressedIn = new Property("EXPRESSED_IN");
	pExpressedAt = new Property("EXPRESSED_AT");
	pAnnotatedWith = new Property("ANNOTATED_WITH");
        pEvidence = new Property("EVIDENCE");
        pRef = new Property("REF");
        
        Context c = new Context();
        c.addTarget(pExpressedIn, rPositionAnalia);
        rGeneWG.addTarget(pExpressedAt, rStageEmbryo, c);
        c = new Context();
        c.addTarget(pExpressedIn, rPositionEpidermis);
        rGeneWG.addTarget(pExpressedAt, rStageEmbryo, c);
        Context cHead = new Context();
        cHead.addTarget(pExpressedIn, rPositionHead);
        rGeneWG.addTarget(pExpressedAt, rStageEmbryo, cHead);
        c = new Context();
        c.addTarget(pExpressedIn, rPositionDorsal);
        rGeneWG.addTarget(pExpressedAt, rStageLarva, c);
        c = new Context();
        c.addTarget(pExpressedIn, rPositionVentral);
        rGeneWG.addTarget(pExpressedAt, rStageLarva, c);

        Context cEmbryo = new Context();
        cEmbryo.addTarget(pExpressedAt, rStageEmbryo);
        rGeneWG.addTarget(pExpressedIn, rPositionAnalia, cEmbryo);
        rGeneWG.addTarget(pExpressedIn, rPositionEpidermis, cEmbryo);
        rGeneWG.addTarget(pExpressedIn, rPositionHead, cEmbryo);
        c = new Context();
        c.addTarget(pExpressedAt, rStageLarva);
        rGeneWG.addTarget(pExpressedIn, rPositionDorsal, c);
        rGeneWG.addTarget(pExpressedIn, rPositionVentral, c);
        
        Context cISS = new Context();
        cISS.addTarget(pEvidence, rEvidenceISS);
        c = new Context(cISS);
        c.addTarget(pRef, new StringValue("PMID001"));
        c.addTarget(pSpecializes, cHead);
        c.addTarget(pSpecializes, cEmbryo);
        rGeneWG.addTarget(pAnnotatedWith, rFunctionA, c); // function=A when expressed in the head of embryo
    }

    // test ExpressedIn and ExpressedAt
    public void testExpression() {
        Set targets = rGeneWG.getTargets(pExpressedAt);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rStageEmbryo));
	assertTrue(targets.contains(rStageLarva));

        targets = rGeneWG.getTargets(pExpressedIn);
  	assertEquals(5, targets.size());
	assertTrue(targets.contains(rPositionAnalia));
	assertTrue(targets.contains(rPositionEpidermis));
	assertTrue(targets.contains(rPositionHead));
	assertTrue(targets.contains(rPositionDorsal));
	assertTrue(targets.contains(rPositionVentral));

        Criterion criterion = Expression.eq(pExpressedIn, rPositionHead);
        targets = rGeneWG.getTargets(pExpressedAt, criterion);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rStageEmbryo));
        
        criterion = Expression.eq(pExpressedAt, rStageEmbryo);
        targets = rGeneWG.getTargets(pExpressedIn, criterion);
  	assertEquals(3, targets.size());
	assertTrue(targets.contains(rPositionAnalia));
	assertTrue(targets.contains(rPositionEpidermis));
	assertTrue(targets.contains(rPositionHead));
    }

    // test AnnotatedWith
    public void testAnnotation() {
        Set targets = rGeneWG.getTargets(pAnnotatedWith);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rFunctionA));

        Criterion criterion = Expression.inContext(pExpressedIn, rPositionHead);
        targets = rGeneWG.getTargets(pAnnotatedWith, criterion);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rFunctionA));
        
        criterion = Expression.inContext(pExpressedIn, rPositionAnalia);
        targets = rGeneWG.getTargets(pAnnotatedWith, criterion);
  	assertNull(targets);

        criterion = Expression.inContext(pExpressedAt, rStageEmbryo);
        targets = rGeneWG.getTargets(pAnnotatedWith, criterion);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rFunctionA));
        
        criterion = Expression.inContext(pExpressedAt, rStageLarva);
        targets = rGeneWG.getTargets(pAnnotatedWith, criterion);
  	assertNull(targets);

        criterion = Expression.and(Expression.inContext(pExpressedIn, rPositionHead), Expression.inContext(pExpressedAt, rStageEmbryo));
        targets = rGeneWG.getTargets(pAnnotatedWith, criterion);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rFunctionA));

        criterion = Expression.eq(pEvidence, rEvidenceISS);
        targets = rGeneWG.getTargets(pAnnotatedWith, criterion);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rFunctionA));
    }

    // test of the retrieval of context
    public void testContextRetrieval() {
        Resource rFunctionB = new Resource();
        rGeneWG.addTarget(pAnnotatedWith, rFunctionB);

        Set contexts = rGeneWG.getContexts(pAnnotatedWith, rFunctionA, 0);
  	assertEquals(1, contexts.size());
        Set refs = ((Context)(contexts.iterator().next())).getTargets(pRef);
  	assertEquals(1, refs.size());
	assertTrue(refs.contains(new StringValue("PMID001")));
        
        contexts = rGeneWG.getContexts(pAnnotatedWith, rFunctionB, 0);
  	assertEquals(1, contexts.size());
        Resource cntx = (Resource)contexts.iterator().next();
	assertEquals(resourceFactory.getResource("TRUE"), cntx);
    }
}
