package fr.unice.bioinfo.allonto.persistence;
import junit.framework.*;
import net.sf.hibernate.Session;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.HibernateException;
import fr.unice.bioinfo.allonto.datamodel.Property;
import fr.unice.bioinfo.allonto.datamodel.ResourceFactory;
import fr.unice.bioinfo.allonto.datamodel.Resource;
import fr.unice.bioinfo.allonto.datamodel.StringValue;
import fr.unice.bioinfo.allonto.datamodel.Context;
import fr.unice.bioinfo.allonto.util.AllontoFactory;
import fr.unice.bioinfo.allonto.persistence.HibernateUtil;

import java.util.Set;
import java.util.List;

public class TestHibernate extends TestCase {
    private static ResourceFactory resourceFactory = (ResourceFactory)AllontoFactory.getResourceFactory();

    private static String uniqueId = "-"+Long.toString(System.currentTimeMillis());
    protected Property nameProperty, evidenceProperty;
    protected Property isa_rel, sub_rel;

    public static void main(String args[]) throws HibernateException {
	junit.textui.TestRunner.run(TestHibernate.class);
    }

    protected void setUp() throws HibernateException {
	nameProperty = resourceFactory.getProperty("NAME");
	evidenceProperty = resourceFactory.getProperty("EVIDENCE");
	isa_rel = resourceFactory.getProperty("SUBSUME");
	sub_rel = resourceFactory.getProperty("SUBSUMED_BY");
    }

    public Integer saveObject(Object object) throws HibernateException {
	Session sess = HibernateUtil.currentSession();
	Transaction t = sess.beginTransaction();
	Integer generatedId = (Integer)sess.save(object);
	t.commit();
	HibernateUtil.closeSession();
	return generatedId;
    }

    public void testStringValuePersistence() throws HibernateException {
	StringValue nameValue = new StringValue("testStringValue"+uniqueId);
	assertEquals(nameValue.getValue(), "testStringValue"+uniqueId);
	Integer generatedId = saveObject(nameValue);

	Session sess = HibernateUtil.currentSession();
	Transaction t = sess.beginTransaction();
	StringValue nameValue_read = (StringValue)sess.get(fr.unice.bioinfo.allonto.datamodel.StringValue.class, generatedId);
	assertEquals(nameValue.getValue(), nameValue_read.getValue());
	assertEquals(nameValue.getId(), nameValue_read.getId());
	assertEquals(nameValue, nameValue_read);

	t.commit();
	HibernateUtil.closeSession();
    }


    public void testPropertyPersistence() throws HibernateException {
	Property property = new Property();
	property.setAcc("propAcc1"+uniqueId);
	assertEquals(property.getAcc(), "propAcc1"+uniqueId);

	Integer generatedId = saveObject(property);

	Session sess = HibernateUtil.currentSession();
	Transaction t = sess.beginTransaction();
	Property property_read = (Property)sess.get(fr.unice.bioinfo.allonto.datamodel.Property.class, generatedId);
	assertEquals(property.getAcc(), property_read.getAcc());
	assertEquals(property.getId(), property_read.getId());
	//	assertEquals(property, property_read);

	t.commit();
	HibernateUtil.closeSession();
    }

    public void testPropertyPersistence2() throws HibernateException {
	assertEquals(nameProperty.getAcc(), "NAME");
//		assertEquals(nameProperty.getTarget(nameProperty), new StringValue("name"));
//	assertEquals(nameProperty.getTarget(resourceFactory.getProperty("NAME")), new StringValue("name"));

	Session sess = HibernateUtil.currentSession();
	Transaction t = sess.beginTransaction();
	// the methods below (performed in different sessions) must not duplicate the property in the database
	//	sess.saveOrUpdate(resourceFactory.getProperty("NAME"));
	sess.saveOrUpdate(nameProperty);
	t.commit();
	HibernateUtil.closeSession();

	sess = HibernateUtil.currentSession();
	t = sess.beginTransaction();
	//	sess.saveOrUpdate(resourceFactory.getProperty("NAME"));
	sess.saveOrUpdate(nameProperty);
	t.commit();
	HibernateUtil.closeSession();

	// test 'session.find'
	sess = HibernateUtil.currentSession();
	t = sess.beginTransaction();
	List list = sess.find("from Property as prop where prop.acc = 'NAME'");
	assertEquals(1, list.size()); // only one property with acc == NAME
	Property property_read = (Property) list.iterator().next();
	assertEquals(property_read.getAcc(), nameProperty.getAcc());
	assertEquals(property_read.getId(), nameProperty.getId());
	assertEquals(property_read, nameProperty);
	assertEquals(property_read.getTarget(resourceFactory.getProperty("NAME")), new StringValue("name"));
	assertEquals(property_read.getTarget(nameProperty), new StringValue("name"));

	t.commit();
	HibernateUtil.closeSession();
    }

    public void testResourcePersistence() throws HibernateException {

	Resource resource = new Resource("resAcc1"+uniqueId);
	resource.setTarget(nameProperty, new StringValue("name of resource one"+uniqueId));

	Integer generatedId = saveObject(resource);

	Session sess = HibernateUtil.currentSession();
	Transaction t = sess.beginTransaction();
	List list = sess.find("from Resource as res where res.acc = 'resAcc1"+uniqueId+"'");
	assertEquals(1, list.size()); // only one resource with acc == resAcc1
	Resource resource_read = (Resource) list.iterator().next();
	assertEquals(resource_read.getAcc(), resource.getAcc());
	assertEquals(resource_read.getId(), resource.getId());
	assertEquals(resource_read, resource);
	assertEquals(resource_read.getTarget(resourceFactory.getProperty("NAME")), new StringValue("name of resource one"+uniqueId));
	assertEquals(resource_read.getTarget(nameProperty), new StringValue("name of resource one"+uniqueId));

	t.commit();
	HibernateUtil.closeSession();
    }

    public void testAssociationPersistence() throws HibernateException {
	assertEquals(nameProperty.getAcc(), "NAME");

	Session sess = HibernateUtil.currentSession();
	Transaction t = sess.beginTransaction();

	Resource resource1 = new Resource("resAccA1"+uniqueId);
	resource1.addTarget(nameProperty, new StringValue("name of source one"+uniqueId));

	Resource resource2 = new Resource("resAccA2"+uniqueId);
	resource2.setTarget(nameProperty, new StringValue("name of target one"+uniqueId));
	sess.saveOrUpdate(isa_rel);
	sess.saveOrUpdate(isa_rel.getInverse());
	sess.save(resource1);
	sess.save(resource2);

        resource1.addTarget(isa_rel, resource2);

	t.commit();
	HibernateUtil.closeSession();

	sess = HibernateUtil.currentSession();
	t = sess.beginTransaction();
	List list = sess.find("from Resource as res where res.acc = 'resAccA1"+uniqueId+"'");
	assertEquals(list.size(),1); // only one resource with acc == resAccA1
	Resource resource_read = (Resource) list.iterator().next();
	assertEquals(resource_read.getAcc(), resource1.getAcc());
	assertEquals(resource_read.getId(), resource1.getId());
	assertEquals(resource_read, resource1);
	assertEquals(resource_read.getTarget(resourceFactory.getProperty("NAME")), new StringValue("name of source one"+uniqueId));
	assertEquals(resource_read.getTarget(nameProperty), new StringValue("name of source one"+uniqueId));

	Set targets = resource_read.getTargets(isa_rel);
	assertEquals(targets.size(),1); // only one target
	Resource target = (Resource) targets.iterator().next();
	assertEquals(target.getAcc(), resource2.getAcc());
	assertEquals(target.getId(), resource2.getId());
	assertEquals(target, resource2);
	assertEquals(target.getTarget(resourceFactory.getProperty("NAME")), new StringValue("name of target one"+uniqueId));
	assertEquals(target.getTarget(nameProperty), new StringValue("name of target one"+uniqueId));

	t.commit();
	HibernateUtil.closeSession();
    }

    public void testGraphPersistence() throws HibernateException {
	// Creates a first property 'prop1'
        Property prop1 = new Property("prop1Acc"+uniqueId);
        prop1.setTarget(nameProperty, new StringValue("prop1"+uniqueId));

	// Creates a second property 'prop2'
        Property prop2 = new Property("prop2Acc"+uniqueId);
        prop2.setTarget(nameProperty, new StringValue("prop2"+uniqueId));

	// Creates an is_a relationship from prop2 to prop1
        prop2.addTarget(isa_rel, prop1);

	// Creates four resources
        Resource obj1 = new Resource("obj1Acc"+uniqueId);
        Resource obj2 = new Resource("obj2Acc"+uniqueId);
        Resource obj3 = new Resource("obj3Acc"+uniqueId);
        Resource obj4 = new Resource("obj4Acc"+uniqueId);

	// Creates a link from obj1 to obj2 with property prop1
        obj1.addTarget(prop1, obj2);
	// Creates a link from obj1 to obj3 with property prop2
	obj1.addTarget(prop2, obj3);
	// Creates a link from obj3 to obj4 with property prop2
	obj3.addTarget(prop2, obj4);
        
	// Saves the graph
	Session sess = HibernateUtil.currentSession();
	Transaction t = sess.beginTransaction();
	sess.saveOrUpdate(nameProperty);
	sess.saveOrUpdate(isa_rel);
	//sess.saveOrUpdate(sub_rel);
	sess.save(prop1);
	sess.save(prop2);
	sess.save(obj1);
	sess.save(obj2);
	sess.save(obj3);
	sess.save(obj4);
	t.commit();
	HibernateUtil.closeSession();

	// reload obj1
	sess = HibernateUtil.currentSession();
	t = sess.beginTransaction();
	List list = sess.find("from Resource as res where res.acc = 'obj1Acc"+uniqueId+"'");
	assertEquals(list.size(),1); // only one resource with acc == obj1Acc
	Resource obj1_read = (Resource) list.iterator().next();

	// Tests the graph
	Set targets = obj1_read.getTargets(prop1);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj2));

	targets = obj1_read.getTargets(prop2);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj3));

	targets = obj1_read.getImpliedTargets(prop1); // retrieve targets with prop1 and prop2
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));

	targets = obj1_read.getReachableTargets(prop1); // retrieve transitive targets with prop1
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj2));

	targets = obj1_read.getReachableTargets(prop2); // retrieve transitive targets with prop2
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj3));
        assertTrue(targets.contains(obj4));

	targets = obj1_read.getReachableTargets(prop1, true); // retrieve transitive targets
	                                                 // with prop1 and prop2
	assertEquals(3, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));
        assertTrue(targets.contains(obj4));
	t.commit();
	HibernateUtil.closeSession();
    }

    // test a graph usting contexts
    public void testGraphPersistence2() throws HibernateException {
	// Creates a first property 'propC1'
        Property prop1 = new Property("propC1Acc"+uniqueId);
        prop1.setTarget(nameProperty, new StringValue("propC1"+uniqueId));

	// Creates a second property 'propC2'
        Property prop2 = new Property("propC2Acc"+uniqueId);
        prop2.setTarget(nameProperty, new StringValue("propC2"+uniqueId));

	// Creates an is_a relationship from prop2 to prop1
        prop2.addTarget(isa_rel, prop1);

	// Creates four resources
        Resource obj1 = new Resource("objC1Acc"+uniqueId);
        Resource obj2 = new Resource("objC2Acc"+uniqueId);
        Resource obj3 = new Resource("objC3Acc"+uniqueId);
        Resource obj4 = new Resource("objC4Acc"+uniqueId);

	// Creates four contexts
        Context context1 = new Context("con1Acc"+uniqueId);
	context1.setTarget(evidenceProperty, new StringValue("TAS"));
	Context context2 = new Context("con2Acc"+uniqueId);
	context2.setTarget(evidenceProperty, new StringValue("NAS"));
	Context context3 = new Context("con3Acc"+uniqueId);
	context3.setTarget(evidenceProperty, new StringValue("TAS"));
	Context context4 = new Context("con4Acc"+uniqueId);
	context4.setTarget(evidenceProperty, new StringValue("NAS"));

	// Creates a link from obj1 to obj2 with property prop1 and context context1
        obj1.addTarget(prop1, obj2, context1);
	// Creates a link from obj1 to obj3 with property prop2 and context context2
	obj1.addTarget(prop2, obj3, context2);
	// Creates a link from obj3 to obj4 with property prop2
	obj3.addTarget(prop2, obj4);
        
	// Saves the graph
	Session sess = HibernateUtil.currentSession();
	Transaction t = sess.beginTransaction();
	sess.saveOrUpdate(nameProperty);
	sess.saveOrUpdate(evidenceProperty);
	sess.saveOrUpdate(isa_rel);
	//sess.saveOrUpdate(sub_rel);
	sess.save(prop1);
	sess.save(prop2);
	sess.save(obj1);
	sess.save(obj2);
	sess.save(obj3);
	sess.save(obj4);
	sess.save(context1);
	sess.save(context2);
	sess.save(context3);
	sess.save(context4);
	t.commit();
	HibernateUtil.closeSession();

	// reload obj1
	sess = HibernateUtil.currentSession();
	t = sess.beginTransaction();
	List list = sess.find("from Resource as res where res.acc = 'objC1Acc"+uniqueId+"'");
	assertEquals(list.size(),1); // only one resource with acc == obj1Acc
	Resource obj1_read = (Resource) list.iterator().next();

	// Tests the graph
	Set targets = obj1_read.getTargets(prop1);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj2));

	targets = obj1_read.getTargets(prop2);
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj3));

	targets = obj1_read.getImpliedTargets(prop1); // retrieve targets with prop1 and prop2
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));

	targets = obj1_read.getReachableTargets(prop1); // retrieve transitive targets with prop1
	assertEquals(1, targets.size());
        assertTrue(targets.contains(obj2));

	targets = obj1_read.getReachableTargets(prop2); // retrieve transitive targets with prop2
	assertEquals(2, targets.size());
        assertTrue(targets.contains(obj3));
        assertTrue(targets.contains(obj4));

	targets = obj1_read.getReachableTargets(prop1, true); // retrieve transitive targets
	// with prop1 and prop2
	assertEquals(3, targets.size());
        assertTrue(targets.contains(obj2));
        assertTrue(targets.contains(obj3));
        assertTrue(targets.contains(obj4));
	t.commit();
	HibernateUtil.closeSession();
    }

}
