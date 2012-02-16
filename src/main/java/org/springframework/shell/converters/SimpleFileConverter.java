package org.springframework.shell.converters;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.shell.Shell;
import org.springframework.roo.shell.converters.FileConverter;
import org.springframework.stereotype.Component;

//@Component
public class SimpleFileConverter extends FileConverter {
	@Autowired private Shell shell;

	@Override
	protected File getWorkingDirectory() {
		return shell.getHome();
	}
}
