/*
	Copyright (C) 2007 Joern Dinkla, www.dinkla.net, joern@dinkla.net

	This program is free software; you can redistribute it and/or
	modify it under the terms of the GNU General Public License
	as published by the Free Software Foundation; either version 2
	of the License, or (at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program; if not, write to the Free Software
	Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
*/

package net.dinkla.emf

import org.eclipse.example.library.BookCategory
import org.eclipse.example.library.Library
import org.eclipse.example.library.LibraryFactory
import org.eclipse.example.library.LibraryPackage

/**
	Tests for the EMFBuilder.
*/
class EMFBuilderTest extends GroovyTestCase {

	final static Map testData = [ 
 		    'Dashiell Hammet': 
 		    	[ [ title: 'The Maltese Falcon', pages: 224 ],
 		    	  [ title: 'Red Harvest', pages: 224 ] ], 
 			'Raymond Chandler': 
 				[ [ title: 'The Big Sleep', pages: 234 ],
 	              [ title: 'The High Window', pages: 272 ] ],
 			'Ross Macdonald': 
 				[ [ title: 'Meet Me at the Morgue', pages: 241 ],
 				  [ title: 'The Far Side of the Dollar', pages: 245] ]
 		  ]

	void testBuilder1() {
		def builder = new EMFBuilder(LibraryFactory.eINSTANCE)
		builder.debug = false
		Map myWriters = [:]		// a map to store the authors/writers
		def library = builder.Library( name: 'Hardboiled Library' ) {
			writers {			// create and memoize the writers
				for ( writer in testData ) {
					myWriters[writer.key] = Writer( name: writer.key )
				}
			}
			books {				// create the books, use the writers in myWriters
				for (writer in testData) {
					for (book in writer.value) {
						Book( title: book.title, 
						 	  pages: book.pages, 
							  category: BookCategory.MYSTERY_LITERAL, 
							  author: myWriters[writer.key] )
					}
				}
			}
		}
		// save it to a file
		EMFUtilities.save('data/generated_hardboiled_library.xml', library)
	}

	void testBuilder2() {
		def builder = new EMFBuilder(LibraryFactory)
		shouldFail(ScriptException) {
			builder.Library() {
				noreference {
					Book()
				}
			}
		}
		shouldFail(ScriptException) {
			builder.Library() {
				NoClass( 'noAttribute' ) 
			}
		}
		shouldFail(ScriptException) {
			builder.Library {
				noclass( a: '1', b: '2', c: '3')
			}
		}	
		shouldFail(ScriptException) {
			builder.Library {
				noclass( a: '1', b: '2', c: '3', 'default')
			}
		}		
		shouldFail(ScriptException) {
			builder.Library( dubdi: 'xx')
		}
		shouldFail(ScriptException) {
			builder.VOID('value')
		}		
		shouldFail(ScriptException) {
			builder.VOID(name: 'x')
		}
		shouldFail(ScriptException) {
			builder.VOID(name: 'x', 'default')
		}
		shouldFail(ScriptException) {
			builder.defaultFeature = 'xxx'
			builder.Library('value')
		}		
	}

	void testBuilder3() {
		def builder = new EMFBuilder(LibraryFactory)
		builder.debug = false
		String name = 'name of library'
		def library2 = builder.Library( name )
		assertEquals(name, library2.name )
		shouldFail(ScriptException) {
			builder.Library {
				Writer()
			}
		}
	}

	void testBuilder4() {
		// tests a 0..1 reference Book.author
		def builder = new EMFBuilder(LibraryFactory)
		builder.debug = false
		def lib = builder.Library('name') {
			books {
				Book() {
					author {
						Writer( 'A writer')
					}
				}
			}
		}
		assertTrue(lib.books.author.every { it.name == 'A writer' })
	}
	
	void testModel() {
		// get the package (this is needed, so that the library model is known to the loader)
		LibraryPackage.eINSTANCE
		// load the file
		def resource = EMFUtilities.load('data/hardboiled_library.xml')
		// get the library element
		Library library = (Library) resource.contents[0]
		// assertions
		assertEquals(6, library.books.size())		
		assertEquals(3, library.writers.size())		
		assertEquals(3, library.books.grep { it.pages < 240 }.size())
		assertEquals(["Dashiell Hammet", "Raymond Chandler", "Ross Macdonald"], library.writers.name.sort())
		assertEquals('The High Window', library.books.grep { it.pages == 272 }[0].title)
		assertEquals(2, library.books.grep { it.author.name == 'Raymond Chandler' }.size())
		assertEquals(2, library.writers.find { it.name == 'Raymond Chandler' }.books.size())
	}

	void testCreate1() {
		// create a model
		def factory = LibraryFactory.eINSTANCE
		def library = factory.createLibrary()
		library.name = 'Hardboiled Library'
		def writer = factory.createWriter()
		writer.name = 'Raymond Chandler'
		library.writers << writer		// add him/her to the library
		def book = factory.createBook()
		book.title = 'The Big Sleep'
		book.pages = 234
		book.category = BookCategory.MYSTERY_LITERAL
		book.author = writer
		library.books << book			// add the book to the library
		
		assertEquals(1, library.books.size())
		assertEquals(1, library.writers.size())
		assertEquals(1, library.writers[0].books.size())
		assertTrue(library.books[0].author == writer)
	}
	
	void testCreate2() {
		// use the builder
		def builder = new EMFBuilder(LibraryFactory)
		builder.debug = false
		def writer
		def library = builder.Library( name : 'Hardboiled Library') {
			writers {
				writer = Writer( name : 'Raymond Chandler')
			}
			books {
				Book ( title: 'The Big Sleep', pages: 234, category: BookCategory.MYSTERY_LITERAL, author: writer)
			}
		}
		assertEquals(1, library.writers.size())
		assertEquals(1, library.books.size())
		assertEquals(1, library.writers[0].books.size())
		assertTrue(library.books[0].author == writer)
	}
	
	void testSaveLoad() {
		LibraryPackage.eINSTANCE
		def resource = EMFUtilities.load('data/hardboiled_library.xml')
		Library library = (Library) resource.contents[0]
		String xmlFile = 'data/tmp_hardboiled_library.xml'
		EMFUtilities.saveXML(xmlFile, library)
		def resource2 = EMFUtilities.loadXML(xmlFile)
		Library library2 = (Library) resource2.contents[0]
		assertEquals(library.writers.size(), library2.writers.size())
		assertEquals(library.books.size(), library2.books.size())
		new File(xmlFile).delete()
	}
	
}
