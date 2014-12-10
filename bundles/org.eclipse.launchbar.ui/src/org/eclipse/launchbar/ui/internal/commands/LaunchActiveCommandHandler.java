/*******************************************************************************
 * Copyright (c) 2014 QNX Software Systems and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Doug Schaefer
 *******************************************************************************/
package org.eclipse.launchbar.ui.internal.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchMode;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.launchbar.core.ILaunchDescriptor;
import org.eclipse.launchbar.core.ILaunchTarget;
import org.eclipse.launchbar.core.internal.LaunchBarManager;
import org.eclipse.launchbar.ui.internal.Activator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.UIJob;

public class LaunchActiveCommandHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		new UIJob(Display.getDefault(), "Launching Active Configuration") {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				try {
					LaunchBarManager launchBarManager = Activator.getDefault().getLaunchBarUIManager().getManager();
					ILaunchDescriptor desc = launchBarManager.getActiveLaunchDescriptor();
					ILaunchTarget target = launchBarManager.getActiveLaunchTarget();
					ILaunchConfiguration config = launchBarManager.getLaunchConfiguration(desc, target);
					if (config == null)
						return Status.OK_STATUS;
					ILaunchMode launchMode = launchBarManager.getActiveLaunchMode();
					DebugUITools.launch(config, launchMode.getIdentifier());
				} catch (CoreException e) {
					return e.getStatus();
				}
				return Status.OK_STATUS;
			};
		}.schedule();

		return Status.OK_STATUS;
	}

	protected String getMode(ILaunchMode launchMode) {
		return launchMode.getIdentifier(); //$NON-NLS-1$
	}

}
