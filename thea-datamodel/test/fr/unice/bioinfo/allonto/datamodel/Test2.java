package fr.unice.bioinfo.allonto.datamodel;

import junit.framework.*;
import fr.unice.bioinfo.allonto.datamodel.Property;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.Context;
import fr.unice.bioinfo.allonto.datamodel.expression.Criterion;
import fr.unice.bioinfo.allonto.datamodel.expression.Expression;
import fr.unice.bioinfo.allonto.util.AllontoFactory;

import java.util.Set;

public class Test2 extends TestCase {
    private static ResourceFactory resourceFactory = (ResourceFactory)AllontoFactory.getResourceFactory();

    protected Property isa_rel, sub_rel, pSpecializes;
    protected Property pAnnotatedWith, pGoAnnotatedWith;
    protected Property pInteractsWith, pActivates, pInhibits;
    protected Property pEvidence, pUser;
    protected Resource rGeneA, rTermA, rTermAA;
    protected Resource rGeneB, rTermB, rTermBB;
    protected Resource rGeneC, rTermC, rTermCC;
    protected Resource rGeneD, rTermD, rTermDD;
    protected Resource rUserLab, rUserChief, rUserTrainee, rUserFreelance;
    protected Resource rEvidenceTAS, rEvidenceISS, rEvidenceBLAST;
    protected Context  cEvidenceTAS, cEvidenceISS, cEvidenceBLAST, cUserLab, cUserChief, cUserTrainee, cUserFreelance;

    public static void main(String args[]) {
	junit.textui.TestRunner.run(Test2.class);
    }

    protected void setUp() {
	// Retrieves is_a property
        isa_rel = resourceFactory.getProperty("SUBSUME");
	// Retrieves the inverse property of is-a
	sub_rel = isa_rel.getInverse();

        pSpecializes = resourceFactory.getProperty("SPECIALIZES");

        // retrieves property pGoAnnotatedWith
        pGoAnnotatedWith = resourceFactory.getProperty("GOANNOTATION");
        // creates property pAnnotatedWith
	pAnnotatedWith = new Property("ANNOTATION");
        // pGoAnnotatedWith subsume pAnnotatedWith
        pGoAnnotatedWith.addTarget(isa_rel, pAnnotatedWith);
        
        pEvidence = resourceFactory.getProperty("EVIDENCE");
        pUser = resourceFactory.getProperty("USER");
        pInteractsWith = resourceFactory.getProperty("INTERACTS_WITH");
        pActivates = resourceFactory.getProperty("ACTIVATES");
        pInhibits = resourceFactory.getProperty("INHIBITS");

        // Creates resources
        rGeneA = new Resource();
        rTermA = new Resource();
        rTermAA = new Resource();
        rTermAA.addTarget(isa_rel, rTermA);

        rGeneB = new Resource();
        rTermB = new Resource();
        rTermBB = new Resource();
        rTermBB.addTarget(isa_rel, rTermB);
        
        rGeneC = new Resource();
        rTermC = new Resource();
        rTermCC = new Resource();
        rTermCC.addTarget(isa_rel, rTermC);
        
        rGeneD = new Resource();
        rTermD = new Resource();
        rTermDD = new Resource();
        rTermDD.addTarget(isa_rel, rTermD);

        rUserLab = new Resource();
        rUserChief = new Resource();
        rUserTrainee = new Resource();
        rUserFreelance = new Resource();
        
        rEvidenceTAS = new Resource();
        rEvidenceISS = new Resource();
        rEvidenceBLAST = new Resource();
        rEvidenceBLAST.addTarget(isa_rel, rEvidenceISS);

        cEvidenceTAS = 	new Context();
        cEvidenceTAS.setTarget(pEvidence, rEvidenceTAS);
        cEvidenceISS = 	new Context();
        cEvidenceISS.setTarget(pEvidence, rEvidenceISS);
        cEvidenceBLAST = new Context();
        cEvidenceBLAST.setTarget(pEvidence, rEvidenceBLAST);
        cUserLab  = new Context();
        cUserLab.setTarget(pUser, rUserLab);
        cUserChief  = new Context();
        cUserChief.setTarget(pUser, rUserChief);
        cUserTrainee  = new Context();
        cUserTrainee.setTarget(pUser, rUserTrainee);
        cUserFreelance  = new Context();
        cUserFreelance.setTarget(pUser, rUserFreelance);

        cUserLab.setTarget(pSpecializes, cUserFreelance);
        cUserLab.setTarget(pSpecializes, cUserChief);
        cUserChief.setTarget(pSpecializes, cUserTrainee);
        //resourceFactory.getResource("TRUE").addTarget(pSpecializes, cUserLab);
        //resourceFactory.getResource("TRUE").addTarget(pSpecializes, cUserFreelance);
    }

    // Inheritance of property
    public void testPropertyInheritance() {
        // goAnnotated(geneA, termAA)
        rGeneA.addTarget(pGoAnnotatedWith, rTermAA);

        Set targets = rGeneA.getTargets(pGoAnnotatedWith);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rTermAA));

  	targets = rGeneA.getTargets(pAnnotatedWith);
  	assertNull(targets);
        
  	targets = rGeneA.getTargets(pAnnotatedWith, Resource.USE_SUBPROPERTIES);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rTermAA));
    }

    // Transitive search
    public void testPropertyTransitivity() {
        rGeneA.addTarget(pActivates, rGeneB); // activates(geneA, geneB)
        rGeneB.addTarget(pInhibits, rGeneC); // inhibits(geneB, geneC)
        rGeneB.addTarget(pActivates, rGeneD); // activates(geneB, geneD)

        Set targets = rGeneA.getTargets(pActivates);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rGeneB));

        targets = rGeneA.getTargets(pActivates, Resource.TRANSITIVE_SEARCH);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rGeneB));
	assertTrue(targets.contains(rGeneD));
    }

    // test inheritance and transitivity of property
    public void testPropertyInheritanceAndTransitivity() {
        rGeneA.addTarget(pActivates, rGeneB); // activates(geneA, geneB)
        rGeneB.addTarget(pInhibits, rGeneC); // inhibits(geneB, geneC)
        rGeneB.addTarget(pActivates, rGeneD); // activates(geneB, geneD)

        Set targets = rGeneA.getTargets(pActivates);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rGeneB));

        targets = rGeneA.getTargets(pInteractsWith);
  	assertNull(targets);

        targets = rGeneA.getTargets(pInteractsWith, Resource.USE_SUBPROPERTIES);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rGeneB));

        targets = rGeneA.getTargets(pActivates, Resource.TRANSITIVE_SEARCH);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rGeneB));
	assertTrue(targets.contains(rGeneD));

        targets = rGeneA.getTargets(pInhibits, Resource.TRANSITIVE_SEARCH);
  	assertNull(targets);

        targets = rGeneA.getTargets(pInteractsWith, Resource.TRANSITIVE_SEARCH);
  	assertNull(targets);
        
        targets = rGeneA.getTargets(pInteractsWith, Resource.USE_SUBPROPERTIES | Resource.TRANSITIVE_SEARCH);
  	assertEquals(3, targets.size());
	assertTrue(targets.contains(rGeneB));
	assertTrue(targets.contains(rGeneC));
	assertTrue(targets.contains(rGeneD));

        targets = rGeneA.getTargets(pActivates, Resource.USE_SUBPROPERTIES | Resource.TRANSITIVE_SEARCH);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rGeneB));
	assertTrue(targets.contains(rGeneD));

        targets = rGeneA.getTargets(pInhibits, Resource.USE_SUBPROPERTIES | Resource.TRANSITIVE_SEARCH);
  	assertNull(targets);
    }

    // covered targets
    public void testCoveredTargets() {
        // goAnnotated(geneA, termAA)
        rGeneA.addTarget(pGoAnnotatedWith, rTermAA);

        Set targets = rGeneA.getCoveredTargets(pGoAnnotatedWith, 0);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rTermAA));
	assertTrue(targets.contains(rTermA));

  	targets = rGeneA.getCoveredTargets(pAnnotatedWith, 0);
  	assertNull(targets);
        
  	targets = rGeneA.getCoveredTargets(pAnnotatedWith, Resource.USE_SUBPROPERTIES);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rTermAA));
	assertTrue(targets.contains(rTermA));
    }

/*
 * Context
 */
 
 // Inheritance of property
    public void testPropertyInheritanceWithContext() {
        // goAnnotated(geneA, termAA)
        rGeneA.addTarget(pGoAnnotatedWith, rTermAA, cEvidenceBLAST);

        Set targets = rGeneA.getTargets(pGoAnnotatedWith);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rTermAA));

	Criterion criterion = Expression.eq(pEvidence, rEvidenceBLAST);
	targets = rGeneA.getTargets(pGoAnnotatedWith, criterion);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rTermAA));

	criterion = Expression.eq(pEvidence, rEvidenceISS);
	targets = rGeneA.getTargets(pGoAnnotatedWith, criterion);
	if (rEvidenceISS.equals(rEvidenceBLAST)) System.out.println("===========equals");
	System.out.println(rEvidenceISS.getId()+"###########"+rEvidenceBLAST.getId());
  	assertNull(targets);

        criterion = Expression.eq(pEvidence, rEvidenceTAS);
	targets = rGeneA.getTargets(pGoAnnotatedWith, criterion);
  	assertNull(targets);

        criterion = Expression.alwaysTrue();
	targets = rGeneA.getTargets(pGoAnnotatedWith, criterion);
  	assertNull(targets);

        targets = rGeneA.getTargets(pAnnotatedWith);
  	assertNull(targets);

	criterion = Expression.eq(pEvidence, rEvidenceBLAST);
	targets = rGeneA.getTargets(pAnnotatedWith, criterion);
  	assertNull(targets);

	criterion = Expression.eq(pEvidence, rEvidenceISS);
	targets = rGeneA.getTargets(pAnnotatedWith, criterion);
  	assertNull(targets);

        criterion = Expression.eq(pEvidence, rEvidenceTAS);
	targets = rGeneA.getTargets(pAnnotatedWith, criterion);
  	assertNull(targets);

        criterion = Expression.alwaysTrue();
	targets = rGeneA.getTargets(pAnnotatedWith, criterion);
  	assertNull(targets);
        
  	targets = rGeneA.getTargets(pAnnotatedWith, Resource.USE_SUBPROPERTIES);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rTermAA));

	criterion = Expression.eq(pEvidence, rEvidenceBLAST);
	targets = rGeneA.getTargets(pAnnotatedWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rTermAA));

	criterion = Expression.eq(pEvidence, rEvidenceISS);
	targets = rGeneA.getTargets(pAnnotatedWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertNull(targets);

        criterion = Expression.eq(pEvidence, rEvidenceTAS);
	targets = rGeneA.getTargets(pAnnotatedWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertNull(targets);

        criterion = Expression.alwaysTrue();
	targets = rGeneA.getTargets(pAnnotatedWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertNull(targets);

	criterion = Expression.inContext(pEvidence, rEvidenceBLAST);
	targets = rGeneA.getTargets(pAnnotatedWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rTermAA));

	criterion = Expression.inContext(pEvidence, rEvidenceISS);
	targets = rGeneA.getTargets(pAnnotatedWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rTermAA));

        criterion = Expression.eq(pEvidence, rEvidenceTAS);
	targets = rGeneA.getTargets(pAnnotatedWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertNull(targets);

        criterion = Expression.alwaysTrue();
	targets = rGeneA.getTargets(pAnnotatedWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertNull(targets);
    }

    // Context specialization
    public void testContextSpecialization() {
        rGeneA.addTarget(pActivates, rGeneB, cUserChief); // activates(geneA, geneB)
        rGeneA.addTarget(pActivates, rGeneC, cUserTrainee); // activates(geneA, geneC)
        rGeneA.addTarget(pActivates, rGeneD); // activates(geneA, geneD)

        Set targets = rGeneA.getTargets(pActivates);
  	assertEquals(3, targets.size());
	assertTrue(targets.contains(rGeneB));
	assertTrue(targets.contains(rGeneC));
	assertTrue(targets.contains(rGeneD));

        Criterion criterion = Expression.eq(pUser, rUserChief);
        targets = rGeneA.getTargets(pActivates, criterion);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rGeneB));

        criterion = Expression.eq(pUser, rUserTrainee);
        targets = rGeneA.getTargets(pActivates, criterion);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rGeneC));

        criterion = Expression.eq(pUser, rUserFreelance);
        targets = rGeneA.getTargets(pActivates, criterion);
        assertNull(targets);

        criterion = Expression.eq(pUser, rUserLab);
        targets = rGeneA.getTargets(pActivates, criterion);
        assertNull(targets);

        criterion = Expression.alwaysTrue();
	targets = rGeneA.getTargets(pActivates, criterion);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rGeneD));

        criterion = Expression.inContext(pUser, rUserChief);
        targets = rGeneA.getTargets(pActivates, criterion);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rGeneB));
	assertTrue(targets.contains(rGeneD));

        criterion = Expression.inContext(pUser, rUserTrainee);
        targets = rGeneA.getTargets(pActivates, criterion);
  	assertEquals(3, targets.size());
	assertTrue(targets.contains(rGeneB));
	assertTrue(targets.contains(rGeneC));
	assertTrue(targets.contains(rGeneD));

        criterion = Expression.inContext(pUser, rUserFreelance);
        targets = rGeneA.getTargets(pActivates, criterion);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rGeneD));

        criterion = Expression.inContext(pUser, rUserLab);
        targets = rGeneA.getTargets(pActivates, criterion);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rGeneD));
    }

    // test inheritance and transitivity of property
    public void testPropertyInheritanceAndTransitivityWithContext() {
        rGeneA.addTarget(pActivates, rGeneB, cUserChief); // activates(geneA, geneB)
        rGeneB.addTarget(pInhibits, rGeneC, cUserTrainee); // inhibits(geneB, geneC)
        rGeneB.addTarget(pActivates, rGeneD); // activates(geneB, geneD)
        rGeneA.addTarget(pInhibits, rGeneD, cUserTrainee); // activates(geneA, geneD)

        Set targets = rGeneA.getTargets(pActivates);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rGeneB));

        Criterion criterion = Expression.inContext(pUser, rUserChief);
        targets = rGeneA.getTargets(pInteractsWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rGeneB));

        criterion = Expression.inContext(pUser, rUserTrainee);
        targets = rGeneA.getTargets(pInteractsWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rGeneB));
	assertTrue(targets.contains(rGeneD));

        criterion = Expression.inContext(pUser, rUserChief);
        targets = rGeneA.getTargets(pActivates, criterion, Resource.TRANSITIVE_SEARCH);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rGeneB));
	assertTrue(targets.contains(rGeneD));

        criterion = Expression.inContext(pUser, rUserTrainee);
        targets = rGeneA.getTargets(pInhibits, criterion, Resource.TRANSITIVE_SEARCH);
  	assertEquals(1, targets.size());
	assertTrue(targets.contains(rGeneD));

        criterion = Expression.inContext(pUser, rUserChief);
        targets = rGeneA.getTargets(pInteractsWith, criterion, Resource.USE_SUBPROPERTIES | Resource.TRANSITIVE_SEARCH);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rGeneB));
	assertTrue(targets.contains(rGeneD));

        criterion = Expression.inContext(pUser, rUserTrainee);
        targets = rGeneA.getTargets(pInteractsWith, criterion, Resource.USE_SUBPROPERTIES | Resource.TRANSITIVE_SEARCH);
  	assertEquals(3, targets.size());
	assertTrue(targets.contains(rGeneB));
	assertTrue(targets.contains(rGeneC));
	assertTrue(targets.contains(rGeneD));
    }

    // covered targets
    public void testCoveredTargetsWithContext() {
        // goAnnotated(geneA, termAA)
        rGeneA.addTarget(pGoAnnotatedWith, rTermAA, cUserChief);

        Criterion criterion = Expression.inContext(pUser, rUserTrainee);
        Set targets = rGeneA.getCoveredTargets(pGoAnnotatedWith,criterion, 0);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rTermAA));
	assertTrue(targets.contains(rTermA));

        criterion = Expression.inContext(pUser, rUserChief);
        targets = rGeneA.getCoveredTargets(pGoAnnotatedWith,criterion, 0);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rTermAA));
	assertTrue(targets.contains(rTermA));

        criterion = Expression.inContext(pUser, rUserLab);
        targets = rGeneA.getCoveredTargets(pGoAnnotatedWith,criterion, 0);
  	assertNull(targets);

        criterion = Expression.inContext(pUser, rUserChief);
        targets = rGeneA.getCoveredTargets(pAnnotatedWith,criterion, 0);
  	assertNull(targets);
        
        criterion = Expression.inContext(pUser, rUserTrainee);
  	targets = rGeneA.getCoveredTargets(pAnnotatedWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rTermAA));
	assertTrue(targets.contains(rTermA));

        criterion = Expression.inContext(pUser, rUserChief);
  	targets = rGeneA.getCoveredTargets(pAnnotatedWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(rTermAA));
	assertTrue(targets.contains(rTermA));

        criterion = Expression.inContext(pUser, rUserLab);
  	targets = rGeneA.getCoveredTargets(pAnnotatedWith, criterion, Resource.USE_SUBPROPERTIES);
  	assertNull(targets);
    }
    
}
