/**********************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved.   This program and the accompanying materials
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.cdt.core.dom.ast;

/**
 * @author Doug Schaefer
 */
public interface IVariable extends IBinding {

	/**
	 * @return the type of the variable
	 */
	public IType getType() throws DOMException;
	
	
	/**
	 * whether or not this is a static variable
	 * @return
	 * @throws DOMException
	 */
	public boolean isStatic() throws DOMException;
}
