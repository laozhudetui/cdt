/*******************************************************************************
 * Copyright (c) 2006 Symbian Software and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Andrew Ferguson (Symbian) - Initial API and implementation
 *******************************************************************************/
package org.eclipse.cdt.internal.pdom.tests;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.eclipse.cdt.core.CCorePlugin;
import org.eclipse.cdt.core.dom.ast.IBasicType;
import org.eclipse.cdt.core.dom.ast.IBinding;
import org.eclipse.cdt.core.dom.ast.cpp.ICPPClassType;
import org.eclipse.cdt.core.model.ICProject;
import org.eclipse.cdt.internal.core.pdom.PDOM;
import org.eclipse.cdt.internal.core.pdom.dom.PDOMBinding;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
 
/**
 * Test overloaded symbols are correctly resolved when within a single translation
 * unit. This covers the case of adapting non-PDOM bindings to PDOM bindings by
 * searching for the equivalent binding within the PDOM.
 */
public class OverloadsWithinSingleTUTests extends PDOMTestBase {
	protected PDOM pdom;

	protected void setUp() throws Exception {
		if (pdom == null) {
			ICProject project = createProject("overloadsWithinSingleTU");
			pdom = (PDOM)CCorePlugin.getPDOMManager().getPDOM(project);
		}
		pdom.acquireReadLock();
	}

	protected void tearDown() throws Exception {
		pdom.releaseReadLock();
	}
	
	public void testDistinctBindingsPresent() throws Exception {
		IBinding[] fooBs = pdom.findBindings(Pattern.compile("foo"), new NullProgressMonitor());
		assertEquals(3, fooBs.length);

		IBinding[] barBs = pdom.findBindings(Pattern.compile("bar"), new NullProgressMonitor());
		assertEquals(8, barBs.length);

		IBinding[] FooBs = pdom.findBindings(Pattern.compile("Foo"), new NullProgressMonitor());
		assertEquals(4, FooBs.length);

		Pattern[] XBarAbsPath = makePatternArray(new String[] {"X","bar"});
		IBinding[] XBarBs = pdom.findBindings(XBarAbsPath, new NullProgressMonitor());
		assertEquals(4, XBarBs.length);

		Pattern[] XFooPath = makePatternArray(new String[] {"X","Foo"});
		IBinding[] XFooPathBs = pdom.findBindings(XFooPath, new NullProgressMonitor());
		assertEquals(1, XFooPathBs.length);
	}

	public void testReferencesToGlobalBindings() throws Exception {
		IBinding[] BarBs = pdom.findBindings(Pattern.compile("bar"), new NullProgressMonitor());
		IBinding[] globalBs = getGlobalBindings(BarBs);
		assertEquals(4, globalBs.length);

		// bar()
		assertFunctionRefCount(new Class[] {}, globalBs, 4);
		
		// bar(int)
		assertFunctionRefCount(new Class[] {IBasicType.class}, globalBs, 3);

		// bar(int,int)
		assertFunctionRefCount(new Class[] {IBasicType.class, IBasicType.class}, globalBs, 2);

		// bar(Foo,int)
		assertFunctionRefCount(new Class[] {ICPPClassType.class, IBasicType.class}, globalBs, 1);
	} 

	// aftodo - this is probably not the best way to determine this
	private static IBinding[] getGlobalBindings(IBinding[] bindings) throws CoreException {
		List preresult = new ArrayList();
		for(int i=0; i<bindings.length; i++) {
			if(((PDOMBinding)bindings[i]).getParentNode()==null) {
				preresult.add(bindings[i]);
			}
		}
		return (IBinding[]) preresult.toArray(new IBinding[preresult.size()]);
	}

	public void testReferencesToNamespacedBindings() throws Exception {
		Pattern[] XBarAbsPath = makePatternArray(new String[] {"X","bar"});
		IBinding[] XBarBs = pdom.findBindings(XBarAbsPath, new NullProgressMonitor());

		// X::bar()
		assertFunctionRefCount(new Class[] {}, XBarBs, 2);

		// X::bar(int)
		assertFunctionRefCount(new Class[] {IBasicType.class}, XBarBs, 3);

		// X::bar(int,int)
		assertFunctionRefCount(new Class[] {IBasicType.class,IBasicType.class}, XBarBs, 4);

		// X::bar(X::Foo,int)
		assertFunctionRefCount(new Class[] {ICPPClassType.class,IBasicType.class}, XBarBs, 5);
	}
	
	public void assertFunctionRefCount(Class[] args, IBinding[] bindingPool, int refCount) throws CoreException {
		assertFunctionRefCount(pdom, args, bindingPool, refCount);
	}
}
