package tech.itparklessons.fileshares.archiver.service;

import tech.itparklessons.fileshares.archiver.model.User;

import java.io.File;
import java.util.UUID;

public interface ArchiverService {
    File getFile(UUID fileUUID, User user);

    File getFile(String shareLink, User user);
}
