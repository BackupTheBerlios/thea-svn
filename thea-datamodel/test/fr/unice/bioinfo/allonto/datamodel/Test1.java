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

public class Test1 extends TestCase {
    private static ResourceFactory resourceFactory = (ResourceFactory)AllontoFactory.getResourceFactory();

    protected Property nameProperty;
    protected Property isa_rel, sub_rel;
    protected Property prop1, prop2;
    protected Resource obj1, obj2, obj3, obj4;
    protected Context context1A, context2B, context3A, context4U1;
    protected Resource user1, user2, user3, user4;

    public static void main(String args[]) {
	junit.textui.TestRunner.run(Test1.class);
    }

    protected void setUp() {
	// Retrieves name property
	nameProperty = resourceFactory.getProperty("NAME");
	// Retrieves is_a property
        isa_rel = resourceFactory.getProperty("SUBSUME");
	// Retrieves the inverse property of is-a
	sub_rel = isa_rel.getInverse();

	// Creates a first property 'prop1'
	// with explicit setting of accessor key
        prop1 = new Property();
	prop1.setAcc("prop1Acc");
        prop1.setTarget(nameProperty, new StringValue("prop1"));
	// Creates a second property 'prop2'
	// with accessor key passed to the constructor
	prop2 = new Property("prop2Acc");
        prop2.setTarget(nameProperty, new StringValue("prop2"));

        // Creates four resources
        obj1 = new Resource();
        obj1.setAcc("obj1Acc");
        obj2 = new Resource("obj2Acc");
        obj3 = new Resource("obj3Acc");
        obj4 = new Resource("obj4Acc");

	// creates four users
	user1 = new Resource("lab");
	user2 = new Resource("chief");
	user3 = new Resource("trainee");
	user4 = new Resource("researcher");

        // Creates four contexts
        context1A = new Context();
	context1A.setAcc("con1Acc");
	context1A.setTarget(resourceFactory.getProperty("EVIDENCE"), new StringValue("TAS"));
	context2B = new Context("con2Acc");
	context2B.setTarget(resourceFactory.getProperty("EVIDENCE"), new StringValue("NAS"));
	context3A = new Context("con3Acc");
	context3A.setTarget(resourceFactory.getProperty("USER"), user3);
	context4U1 = new Context("con4AccU1");
	context4U1.setTarget(resourceFactory.getProperty("EVIDENCE"), new StringValue("TAS"));
	context4U1.setTarget(resourceFactory.getProperty("USER"), user2);
    }

    public void testObjectIntegrity() {
	// testing name property
	assertTrue(resourceFactory.getProperty("NAME").getName().equals("name"));
	assertEquals(nameProperty.getTarget(resourceFactory.getProperty("NAME")), new StringValue("name"));
	assertEquals(nameProperty.getTarget(nameProperty), new StringValue("name"));
	assertEquals(nameProperty.getAcc(), "NAME");
	assertEquals(((StringValue)nameProperty.getTarget(nameProperty)).getValue(), "name");

	// Testing prop1 & prop2
	assertEquals("prop1Acc", prop1.getAcc());
	assertEquals(new StringValue("prop1"), prop1.getTarget(nameProperty));
	assertEquals("prop1", ((StringValue)prop1.getTarget(nameProperty)).getValue());
	assertEquals("prop2Acc", prop2.getAcc());
	assertEquals(new StringValue("prop2"), prop2.getTarget(nameProperty));
	assertEquals("prop2", ((StringValue)prop2.getTarget(nameProperty)).getValue());

	// Testing is-a property
	assertEquals(isa_rel.getTarget(nameProperty), new StringValue("is a"));
	assertEquals(isa_rel.getAcc(), "SUBSUME");
	assertEquals(isa_rel.getClass(), Property.class);

	// Testing sub_rel
        assertEquals(sub_rel, resourceFactory.getProperty("SUBSUMED_BY"));

	// checks that there is no is-a relationship from prop1 to prop2
	assertNull(prop2.getTargets(isa_rel));
	assertNull(prop2.getReachableTargets(isa_rel));
	assertNull(prop2.getImpliedTargets(isa_rel));
	assertNull(prop2.getReachableTargets(isa_rel, true));
	assertNull(prop1.getTargets(isa_rel));
	assertNull(prop1.getReachableTargets(isa_rel));
	assertNull(prop1.getImpliedTargets(isa_rel));
	assertNull(prop1.getReachableTargets(isa_rel, true));
	assertNull(prop2.getTargets(sub_rel));
	assertNull(prop2.getReachableTargets(sub_rel));
	assertNull(prop2.getImpliedTargets(sub_rel));
	assertNull(prop2.getReachableTargets(sub_rel, true));
	assertNull(prop1.getTargets(sub_rel));
	assertNull(prop1.getReachableTargets(sub_rel));
	assertNull(prop1.getImpliedTargets(sub_rel));
	assertNull(prop1.getReachableTargets(sub_rel, true));

	// Testing the resources
	assertEquals(obj1.getAcc(), "obj1Acc");
	assertEquals(obj2.getAcc(), "obj2Acc");
	assertEquals(obj3.getAcc(), "obj3Acc");
	assertEquals(obj4.getAcc(), "obj4Acc");

	// Testing the contexts
	assertEquals(context1A.getAcc(), "con1Acc");
	assertEquals(context2B.getAcc(), "con2Acc");
	assertEquals(context3A.getAcc(), "con3Acc");
	assertEquals(context4U1.getAcc(), "con4AccU1");
	assertEquals(new StringValue("TAS"), context1A.getTarget(resourceFactory.getProperty("EVIDENCE")));
	assertEquals(new StringValue("NAS"), context2B.getTarget(resourceFactory.getProperty("EVIDENCE")));
	assertEquals(user3, context3A.getTarget(resourceFactory.getProperty("USER")));
	assertEquals(new StringValue("TAS"), context4U1.getTarget(resourceFactory.getProperty("EVIDENCE")));
    	assertEquals(user2, context4U1.getTarget(resourceFactory.getProperty("USER")));
}

    public void testGraph() {
	// Creates an is_a relationship from term2 to term1
        prop2.addTarget(isa_rel, prop1);
	Set targets = prop2.getTargets(isa_rel);
	assertTrue(targets.size() == 1);
        assertTrue(targets.contains(prop1));

	Set sources = prop1.getTargets(sub_rel);
	assertTrue(sources.size() == 1);
        assertTrue(sources.contains(prop2));

	// Creates a link from obj1 to obj2 with property prop1
        obj1.addTarget(prop1, obj2);
	// Creates a link from obj1 to obj3 with property prop2
	obj1.addTarget(prop2, obj3);
	// Creates a link from obj3 to obj4 with property prop2
	obj3.addTarget(prop2, obj4);
        
	// Tests the graph
	targets = obj1.getTargets(prop1);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj2));

	targets = obj1.getTargets(prop2);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj3));

	targets = obj1.getImpliedTargets(prop1); // retrieve targets with prop1 and prop2
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));

	targets = obj1.getReachableTargets(prop1); // retrieve transitive targets with prop1
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj2));

	targets = obj1.getReachableTargets(prop2); // retrieve transitive targets with prop2
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj3));
        assertTrue(targets.contains(obj4));

	targets = obj1.getReachableTargets(prop1, true); // retrieve transitive targets
	                                                 // with prop1 and prop2
	assertEquals(3, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));
        assertTrue(targets.contains(obj4));
    }

    // two contextuals targets associated with obj1
    public void testContext1() {
	// Creates a link from obj1 to obj2 with property prop1 and context context1A
        obj1.addTarget(prop1, obj2, context1A);
	// Creates a link from obj1 to obj3 with property prop1 and context context2B
	obj1.addTarget(prop1, obj3, context2B);

  	Set targets = obj1.getTargets(prop1);
  	assertEquals(2, targets.size());
	assertTrue(targets.contains(obj2));
	assertTrue(targets.contains(obj3));

	Criterion criterion = Expression.eq(resourceFactory.getProperty("EVIDENCE"), new StringValue("TAS"));
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj2));

        criterion = Expression.eq(resourceFactory.getProperty("EVIDENCE"), new StringValue("NAS"));
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj3));
    }

    // one direct target and one contextual target
    public void testContext3() {
	// Creates a link from obj1 to obj2 with property prop1 and context context1A
        obj1.addTarget(prop1, obj2, context1A);
	// Creates a link from obj1 to obj2 with property prop1 and no context
        obj1.addTarget(prop1, obj3);

	Set targets = obj1.getTargets(prop1);
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));

	Criterion criterion = Expression.eq(resourceFactory.getProperty("EVIDENCE"), new StringValue("TAS"));
        targets = obj1.getTargets(prop1, criterion);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj2));

	criterion = Expression.alwaysTrue();
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj3));

        criterion = Expression.inContext(resourceFactory.getProperty("EVIDENCE"), new StringValue("TAS"));
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));
    }

    // one target associated directly and with a context at the same time
    public void testContext4() {
	// Creates a link from obj1 to obj2 with and without context
        obj1.addTarget(prop1, obj2);
        obj1.addTarget(prop1, obj2, context1A);

        // Creates a link from obj1 to obj3 without context
        obj1.addTarget(prop1, obj3);

        // Creates a link from obj1 to obj4 with context 1A
        obj1.addTarget(prop1, obj4, context1A);

        Set targets = obj1.getTargets(prop1);
	assertEquals(3, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));
        assertTrue(targets.contains(obj4));

	Criterion criterion = Expression.eq(resourceFactory.getProperty("EVIDENCE"), new StringValue("TAS"));
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj4));

        criterion = Expression.alwaysTrue();
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));
    }

    // multiple targets with the same context
    public void testContext6() {
	// Creates a link from obj1 to obj2 with no context
        obj1.addTarget(prop1, obj2);
	// Creates a link from obj1 to obj2, obj3 and obj4 with property prop1 and context context1A
        obj1.addTarget(prop1, obj2, context1A);
        obj1.addTarget(prop1, obj3, context1A);
        obj1.addTarget(prop1, obj4, context1A);

	// Creates a link from obj1 to obj2, obj3 with property prop1 and context context2B
        obj1.addTarget(prop1, obj2, context2B);
        obj1.addTarget(prop1, obj3, context2B);
	// Creates a link from obj1 to obj3, obj4 with property prop1 and context context3A
        obj1.addTarget(prop1, obj3, context3A);
        obj1.addTarget(prop1, obj4, context3A);
	// Creates a link from obj1 to obj4 with property prop1 and context context4U1
        obj1.addTarget(prop1, obj3, context4U1);

	Set targets = obj1.getTargets(prop1);
	assertEquals(3, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));
        assertTrue(targets.contains(obj4));

	Criterion criterion = Expression.eq(resourceFactory.getProperty("EVIDENCE"), new StringValue("TAS"));
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(3, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));
        assertTrue(targets.contains(obj4));

	criterion = Expression.eq(resourceFactory.getProperty("EVIDENCE"), new StringValue("NAS"));
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));

        criterion = Expression.eq(resourceFactory.getProperty("USER"), user3);
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj3));
        assertTrue(targets.contains(obj4));

        criterion = Expression.eq(resourceFactory.getProperty("USER"), user2);
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj3));
        
        criterion = Expression.and(Expression.eq(resourceFactory.getProperty("EVIDENCE"), new StringValue("TAS")), Expression.eq(resourceFactory.getProperty("USER"), user2));
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj3));
    }

    // Retrieving targets with criteria
    public void testContext7() {
	// Creates a link from obj1 to obj2 with no context
        obj1.addTarget(prop1, obj2);
	// Creates a link from obj1 to obj2 and obj3 with property prop1 and context context1A
        obj1.addTarget(prop1, obj2, context1A);
        obj1.addTarget(prop1, obj3, context1A);

	// Creates a link from obj1 to obj2 with property prop1 and context context2B
        obj1.addTarget(prop1, obj2, context2B);
	// Creates a link from obj1 to obj3 with property prop1 and context context3A
        obj1.addTarget(prop1, obj3, context3A);

	// Creates a link from obj1 to obj4 with property prop1 and context4U1
        obj1.addTarget(prop1, obj4, context4U1);

	Set targets = obj1.getTargets(prop1);
	assertEquals(3, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));
        assertTrue(targets.contains(obj4));

	Criterion criterion = Expression.eq(resourceFactory.getProperty("EVIDENCE"), new StringValue("TAS"));
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(3, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));
        assertTrue(targets.contains(obj4));

	criterion = Expression.eq(resourceFactory.getProperty("EVIDENCE"), new StringValue("NAS"));
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj2));

	criterion = Expression.eq(resourceFactory.getProperty("USER"), user2);
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj4));

	criterion = Expression.eq(resourceFactory.getProperty("USER"), user1);
	targets = obj1.getTargets(prop1, criterion);
	assertNull(targets);

	criterion = Expression.eq(resourceFactory.getProperty("USER"), user3);
	targets = obj1.getTargets(prop1, criterion);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj3));
    }


    // Testing inverse relationships with criteria
    public void testContext8() {
	// Creates a link from obj1 to obj2 with property is_a and no context
        obj1.addTarget(isa_rel, obj2);
	// Creates a link from obj1 to obj3 with property is_a and context context1A
	obj1.addTarget(isa_rel, obj3, context1A);
	obj2.addTarget(isa_rel, obj3, context2B);
	obj4.addTarget(isa_rel, obj3);

	Set targets = obj1.getTargets(isa_rel);
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));
	
	targets = obj2.getTargets(sub_rel);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj1));
	
	targets = obj3.getTargets(sub_rel);
	assertEquals(3, targets.size());
        assertTrue(targets.contains(obj1));
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj4));

	Criterion criterion = Expression.eq(resourceFactory.getProperty("EVIDENCE"), new StringValue("TAS"));
	targets = obj3.getTargets(sub_rel, criterion);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj1));

	criterion = Expression.inContext(resourceFactory.getProperty("EVIDENCE"), new StringValue("TAS"));
	targets = obj3.getTargets(sub_rel, criterion);
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj1));
        assertTrue(targets.contains(obj4));

	criterion = Expression.eq(resourceFactory.getProperty("EVIDENCE"), new StringValue("NAS"));
	targets = obj3.getTargets(sub_rel, criterion);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj2));

	criterion = Expression.inContext(resourceFactory.getProperty("EVIDENCE"), new StringValue("NAS"));
	targets = obj3.getTargets(sub_rel, criterion);
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj4));
    }
}
