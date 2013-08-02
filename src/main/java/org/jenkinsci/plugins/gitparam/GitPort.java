package org.jenkinsci.plugins.gitparam;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LsRemoteCommand;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

public class GitPort {

	private static final String MASTER_NAME = "master";

	private URIish repositoryUrl;
	private CredentialsProvider credentialsProvider;

	public GitPort() {
	}

	public GitPort(URIish repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
	}

	public URIish getRepositoryUrl() {
		return repositoryUrl;
	}

	public void setRepositoryUrl(URIish repositoryUrl) {
		this.repositoryUrl = repositoryUrl;
		this.credentialsProvider = getCredentialsProvider();

	}

	public List<String> getTagList() throws Exception {
		LsRemoteCommand command = initLsRemoteCommand();
		Collection<Ref> refs = command.setTags(true).call();
		List<String> tagList = getItemNameList(refs, "refs/tags/", true);
		return tagList;
	}

	public List<String> getBranchList() throws Exception {
		return getBranchList(true);
	}

	public List<String> getBranchList(boolean omitMaster) throws Exception {
		LsRemoteCommand command = initLsRemoteCommand();
		Collection<Ref> refs = command.setHeads(true).call();
		List<String> branchList = getItemNameList(refs, "refs/heads/", true);
		if (omitMaster && branchList.contains(MASTER_NAME) && branchList.size() > 1) {
			branchList.remove(MASTER_NAME);
		}
		return branchList;
	}

	private List<String> getItemNameList(Collection<Ref> refList,
			String stringToOmit, boolean descending) {
		if (refList == null)
			return null;

		List<String> nameList = new LinkedList<String>();
		for (Ref ref : refList) {
			nameList.add(ref.getName().replace(stringToOmit, ""));
		}
		Collections.sort(nameList);
		if (descending) {
			Collections.reverse(nameList);
		}
		return nameList;
	}

	private LsRemoteCommand initLsRemoteCommand() throws IOException {
		Git git = new Git(new NoLocalRepository());
		return git.lsRemote().setCredentialsProvider(getCredentialsProvider())
				.setRemote(this.repositoryUrl.toString());
	}

	private CredentialsProvider getCredentialsProvider() {
		if (credentialsProvider == null) {
			URIish url = getRepositoryUrl();
			if (url.getUser() != null && url.getPass() != null) {
				credentialsProvider = new UsernamePasswordCredentialsProvider(
						url.getUser(), url.getPass());
			} else {
				credentialsProvider = CredentialsProvider.getDefault();
			}
		}
		return credentialsProvider;
	}
}
