package org.jenkinsci.plugins.gitparam;

import hudson.model.StringParameterValue;

public class GitParameterValue extends StringParameterValue {

	private static final long serialVersionUID = -6919100096182179407L;

	public GitParameterValue(String name, String value) {
		super(name, value);
	}

}
