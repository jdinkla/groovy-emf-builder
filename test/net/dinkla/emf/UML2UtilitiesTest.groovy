package net.dinkla.emf

import org.eclipse.emf.common.util.URI
import org.eclipse.uml2.uml.Class
import org.eclipse.uml2.uml.PrimitiveType
import org.eclipse.uml2.uml.UMLPackage

/**
*/

class UML2UtilitiesTest extends GroovyTestCase {

	void testLoad1() {
		def epo2 = UML2Utilities.load('data/epo2.uml')
		checkModel1(epo2)
	}
	
	void testLoad2() {
		def epo2 = UML2Utilities.load(URI.createURI('data/epo2.uml'))
		checkModel1(epo2)
	}

	private void checkModel1(def model) {
		assertEquals(22, model.packagedElements.size())
		assertEquals(4, model.packagedElements.grep { it instanceof PrimitiveType }.size())
		assertEquals(8, model.packagedElements.grep { it instanceof Class }.size())
		assertEquals(1, model.packagedElements.grep { it instanceof Class && it.isAbstract()}.size())
	}

	void testLoad3() {
		def sm = UML2Utilities.load('data/state_machine.uml')
		assertNull(sm)
		sm = UML2Utilities.load('data/state_machine.uml', UMLPackage.Literals.STATE_MACHINE)
		assertEquals(1, sm.regions.size())
		assertEquals(2, sm.regions.subvertices.flatten().size())
	}

}