package net.dinkla.emf

import org.eclipse.emf.common.util.EList
import org.eclipse.emf.common.util.URI
import org.eclipse.emf.ecore.EObject
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl
import org.eclipse.emf.ecore.resource.Resource
import org.eclipse.emf.ecore.resource.ResourceSet
import org.eclipse.uml2.uml.AggregationKind
import org.eclipse.uml2.uml.Class
import org.eclipse.uml2.uml.Element
import org.eclipse.uml2.uml.LiteralUnlimitedNatural
import org.eclipse.uml2.uml.PrimitiveType
import org.eclipse.uml2.uml.Stereotype
import org.eclipse.uml2.uml.UMLFactory
import org.eclipse.uml2.uml.UMLPackage
import org.eclipse.uml2.uml.resource.UMLResource
import org.eclipse.uml2.uml.util.UMLUtil

import org.eclipse.uml2.uml.Profile

/**
*/

class UML2Test extends GroovyTestCase {
	
	void testStateMachine() {
		def builder = new EMFBuilder(UMLFactory)
//		builder.debug = true
		// references to the model classes
		def sm = builder.StateMachine (name:"SM") {
			region {
			    Region {
			    	def a, b
			        subvertex {
			        	a = State(name:"alpha")
			        	b = State(name:"beta")
			        }
			        transition {
			        	Transition(source: a, target: b)
			        }
			    }
			}
	    }
		assertEquals('SM', sm.name)
		assertEquals(1, sm.regions.size())
		assertEquals(2, sm.regions.subvertices.flatten().size())	
		// save it to a file as ecore
		EMFUtilities.save('data/state_machine.xml', sm)
		// save it to a file as uml
		UML2Utilities.save('data/state_machine.uml', sm)
	}

	/**
		The following method is equivalent to the code that is described in
		http://www.eclipse.org/modeling/mdt/uml2/docs/articles/Getting_Started_with_UML2/article.html
	*/
	void testGettingStartedWithUML2() {
		def builder = new EMFBuilder(UMLFactory)
//		builder.debug = true
		def epo2Model = builder.Model(name: 'epo2') {
			packagedElement {
				// primitives types and enumerations
				def intPrimitiveType = PrimitiveType(name: 'int')
				def stringPrimitiveType = PrimitiveType(name: 'String')
				def datePrimitiveType = PrimitiveType(name: 'Date')
				def skuPrimitiveType = PrimitiveType(name: 'SKU')
				def orderStatusEnumeration = Enumeration(name: 'OrderStatus') {
					ownedLiteral {
						EnumerationLiteral(name: 'Pending')
						EnumerationLiteral(name: 'Back Order')
						EnumerationLiteral(name: 'Complete')
					}
				}
				// classes incl. inheritance
				def supplierClass = Class(name: 'Supplier') {
					ownedAttribute {
						Property(name: 'name', type: stringPrimitiveType, lower: 0, upper: 1)
					}
				}
				def customerClass = Class(name: 'Customer') {
					ownedAttribute {
						Property(name: 'customerID', type: intPrimitiveType, lower: 0, upper: 1)
					}
				}
				def purchaseOrderClass = Class(name: 'PurchaseOrder') {
					ownedAttribute {
						Property(name: 'comment', type: stringPrimitiveType, lower: 0, upper: 1)
						Property(name: 'orderDate', type: datePrimitiveType, lower: 0, upper: 1)
						Property(name: 'status', type: orderStatusEnumeration, lower: 0, upper: 1)
						Property(name: 'totalAmount', type: intPrimitiveType, lower: 0, upper: 1)
					}
				}
				def itemClass = Class(name: 'Item') {
					ownedAttribute {
						Property(name: 'productName', type: stringPrimitiveType, lower: 0, upper: 1)
						Property(name: 'quantity', type: intPrimitiveType, lower: 0, upper: 1)
						Property(name: 'USPrice', type: intPrimitiveType, lower: 0, upper: 1)
						Property(name: 'comment', type: stringPrimitiveType, lower: 0, upper: 1)
						Property(name: 'shipDate', type: datePrimitiveType, lower: 0, upper: 1)
						Property(name: 'partNum', type: skuPrimitiveType, lower: 0, upper: 1)
					}					
				}
				def addressClass = Class(name: 'Address' ,isAbstract: true) {
					ownedAttribute {
						Property(name: 'name', type: stringPrimitiveType, lower: 0, upper: 1)
						Property(name: 'country', type: stringPrimitiveType, lower: 0, upper: 1)
					}					
				}
				def usAddressClass = Class(name: 'USAddress') {
					generalization {
						Generalization(general: addressClass)
					}
					ownedAttribute {
						Property(name: 'street', type: stringPrimitiveType, lower: 0, upper: 1)
						Property(name: 'city', type: stringPrimitiveType, lower: 0, upper: 1)
						Property(name: 'state', type: stringPrimitiveType, lower: 0, upper: 1)
						Property(name: 'zip', type: intPrimitiveType, lower: 0, upper: 1)
					}					
				}
				def globalLocationClass = Class(name: 'GlobalLocation') {
					ownedAttribute {
						Property(name: 'countryCode', type: intPrimitiveType, lower: 0, upper: 1)
					}					
					
				}
				def globalAddressClass = Class(name: 'GlobalAddress') {
					generalization {
						Generalization(general: addressClass)
						Generalization(general: globalLocationClass)
					}
					ownedAttribute {
						Property(name: 'location', type: stringPrimitiveType, 
								lower: 0, upper: LiteralUnlimitedNatural.UNLIMITED)
					}					
				}
				// associations
				supplierClass.createAssociation(true,
					AggregationKind.COMPOSITE_LITERAL, "orders", 0,
					LiteralUnlimitedNatural.UNLIMITED, purchaseOrderClass, false,
					AggregationKind.NONE_LITERAL, "", 1, 1)
				supplierClass.createAssociation(true, AggregationKind.NONE_LITERAL,
					"pendingOrders", 0, LiteralUnlimitedNatural.UNLIMITED,
					purchaseOrderClass, false, AggregationKind.NONE_LITERAL, "", 0, 1)
				supplierClass.createAssociation(true, AggregationKind.NONE_LITERAL,
					"shippedOrders", 0, LiteralUnlimitedNatural.UNLIMITED,
					purchaseOrderClass, false, AggregationKind.NONE_LITERAL, "", 0, 1)
				supplierClass.createAssociation(true, AggregationKind.COMPOSITE_LITERAL, 
					"customers", 0, LiteralUnlimitedNatural.UNLIMITED, 
					customerClass, false, AggregationKind.NONE_LITERAL, "", 1, 1)
				customerClass.createAssociation(true, AggregationKind.NONE_LITERAL,
					"orders", 0, LiteralUnlimitedNatural.UNLIMITED, purchaseOrderClass,
					true, AggregationKind.NONE_LITERAL, "customer", 1, 1)
				purchaseOrderClass.createAssociation(true, AggregationKind.NONE_LITERAL, 
					"previousOrder", 0, 1, purchaseOrderClass, 
					false, AggregationKind.NONE_LITERAL, "", 0, 1)
				purchaseOrderClass.createAssociation(true,
					AggregationKind.COMPOSITE_LITERAL, "items", 0,
					LiteralUnlimitedNatural.UNLIMITED, itemClass, true,
					AggregationKind.NONE_LITERAL, "order", 1, 1)
				purchaseOrderClass.createAssociation(true,
					AggregationKind.COMPOSITE_LITERAL, "billTo", 1, 1, addressClass,
					false, AggregationKind.NONE_LITERAL, "", 1, 1)
				purchaseOrderClass.createAssociation(true,
					AggregationKind.COMPOSITE_LITERAL, "shipTo", 0, 1, addressClass,
					false, AggregationKind.NONE_LITERAL, "", 1, 1)
			}
		}
		UML2Utilities.save('data/epo2.uml', epo2Model)
		// checking the model created
		assertEquals(22, epo2Model.packagedElements.size())
		assertEquals(4, epo2Model.packagedElements.grep { it instanceof PrimitiveType }.size())
		assertEquals(8, epo2Model.packagedElements.grep { it instanceof Class }.size())
		assertEquals(1, epo2Model.packagedElements.grep { it instanceof Class && it.isAbstract()}.size())
	}

	/**
		The following method is equivalent to the code that is described in
		http://www.eclipse.org/modeling/mdt/uml2/docs/articles/Introduction_to_UML2_Profiles/article.html
	*/
	void testIntroductionToUML2Profiles() {
		def builder = new EMFBuilder(UMLFactory)
		UML2Utilities.registerPathmaps(URI.createURI('jar:file:./lib/org.eclipse.uml2.uml.resources_2.1.0.v200706251652.jar!/'))
//		builder.debug = true
		Profile profile = builder.Profile(name: 'ecore') {
			def profile = builder.currentNode
			def booleanPrimitiveType = UML2Utilities.importPrimitiveType(profile, 'Boolean')
			def stringPrimitiveType = UML2Utilities.importPrimitiveType(profile, 'String')
			packagedElement {
				def visibilityKindEnumeration = Enumeration(name: 'VisibilityKind') {
					ownedLiteral {
						["Unspecified", "None", "ReadOnly", "ReadWrite", "ReadOnlyUnsettable", "ReadWriteUnsettable"].each {
							EnumerationLiteral(name: it)
						}
					}
				}
				def featureKindEnumeration = Enumeration(name: 'FeatureKind') {
					ownedLiteral {
						["Unspecified", "Simple", "Attribute", "Element", "AttributeWildcard", "ElementWildcard", "Group"].each {
							EnumerationLiteral(name: it)
						}
					}
				}
				def eStructuralFeatureStereotype = Stereotype(name: 'EStructuralFeature', isAbstract: true) {
					ownedAttribute {
						def isTransientProperty = Property(name: 'isTransient', type: booleanPrimitiveType, lower: 0, upper: 1)
						Property(name: 'isUnsettable', type: booleanPrimitiveType, lower: 0, upper: 1)
						Property(name: 'isVolatile', type: booleanPrimitiveType, lower: 0, upper: 1)
						Property(name: 'visibility', type: visibilityKindEnumeration, lower: 0, upper: 1)
						Property(name: 'xmlName', type: stringPrimitiveType, lower: 0, upper: 1)
						Property(name: 'xmlNamespace', type: stringPrimitiveType, lower: 0, upper: 1)
						Property(name: 'xmlFeatureKind', type: featureKindEnumeration, lower: 0, upper: 1)
					}					
				}
				def eAttributeStereotype = Stereotype(name: 'EAttribute', isAbstract: false) {
					ownedAttribute {
						Property(name: 'attributeName', type: stringPrimitiveType, lower: 0, upper: 1)
						Property(name: 'isID', type: booleanPrimitiveType, lower: 0, upper: 1)
					}
				}
				def eReferenceStereotype = Stereotype(name: 'EReference', isAbstract: false) {
					ownedAttribute {
						Property(name: 'referenceName', type: stringPrimitiveType, lower: 0, upper: 1)
						def isResolveProxiesProperty = Property(name: 'isResolveProxies', type: booleanPrimitiveType, lower: 0, upper: 1)
						isResolveProxiesProperty.setBooleanDefaultValue(true);
					}
				}
				eAttributeStereotype.createGeneralization(eStructuralFeatureStereotype)
				eReferenceStereotype.createGeneralization(eStructuralFeatureStereotype)
				
				// Referencing metaclasses
				def propertyMetaclass = UML2Utilities.referenceMetaclass(profile, UMLPackage.Literals.PROPERTY.getName())
				// Creating extensions
				eAttributeStereotype.createExtension(propertyMetaclass, false)
				eReferenceStereotype.createExtension(propertyMetaclass, false)
			}
		}
		// Defining profile
		profile.define()
		// Saving profile
		String profileFileName = 'ecore.' + UMLResource.PROFILE_FILE_EXTENSION
		// save the profile for debugging
		UML2Utilities.save('data/' + profileFileName, profile)
		// save the profile that will be applied. We need it without path information (TODO find a better way)
		UML2Utilities.save(profileFileName, profile)

		// Checking the profile
		assertEquals(7, profile.packagedElements.size())
		assertEquals(0, profile.packagedElements.grep { it instanceof PrimitiveType }.size())
		assertEquals(3, profile.packagedElements.grep { it instanceof Stereotype }.size())
		assertEquals(1, profile.packagedElements.grep { it instanceof Stereotype && it.isAbstract()}.size())

		// Load the epo2 model
		def epo2 = UML2Utilities.load('data/epo2.uml')
		// Apply the profile to the epo2 model
		// TODO this does not work, maybe the two have to use a single resource set/resource ?
		//epo2.applyProfile(profile)
		
		// TODO work in progress
		UML2Utilities.save('data/epo2_b.uml', epo2)
		
		// delete the profile
		new File(profileFileName).delete()
	}
}