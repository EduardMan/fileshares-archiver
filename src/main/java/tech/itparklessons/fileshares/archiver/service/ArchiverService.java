package tech.itparklessons.fileshares.archiver.service;

import kotlin.Pair;
import tech.itparklessons.fileshares.archiver.model.User;

import java.io.File;
import java.util.UUID;

public interface ArchiverService {
    Pair<String, File>  getFile(UUID fileUUID, User user);

    Pair<String, File> getFile(String shareLink, User user);
}
