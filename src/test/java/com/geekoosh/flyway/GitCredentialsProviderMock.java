package com.geekoosh.flyway;

import org.eclipse.jgit.errors.UnsupportedCredentialItem;
import org.eclipse.jgit.transport.CredentialItem;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.URIish;

public class GitCredentialsProviderMock extends CredentialsProvider {
    public GitCredentialsProviderMock() {
        super();
    }

    @Override
    public boolean isInteractive() {
        return false;
    }

    @Override
    public boolean supports(CredentialItem... items) {
        for (CredentialItem item : items) {
            if (item instanceof CredentialItem.InformationalMessage) {
                continue;
            }
            if (item instanceof CredentialItem.YesNoType) {
                continue;
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean get(URIish uri, CredentialItem... items) throws UnsupportedCredentialItem {
        for (CredentialItem item : items) {
            if (item instanceof CredentialItem.InformationalMessage) {
                continue;
            }
            if (item instanceof CredentialItem.YesNoType) {
                ((CredentialItem.YesNoType) item).setValue(true);
                continue;
            }
            return false;
        }
        return true;
    }
}
