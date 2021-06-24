package tech.itparklessons.fileshares.archiver.service.impl;

import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tech.itparklessons.fileshares.archiver.client.SocialClient;
import tech.itparklessons.fileshares.archiver.model.User;
import tech.itparklessons.fileshares.archiver.model.dto.FilesharesSocialFile;
import tech.itparklessons.fileshares.archiver.model.entity.FilesharesArchiverFile;
import tech.itparklessons.fileshares.archiver.repository.FilesharesArchiverFileRepository;
import tech.itparklessons.fileshares.archiver.service.ArchiverService;

import java.io.File;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArchiverServiceImpl implements ArchiverService {
    private final SocialClient socialClient;
    private final FilesharesArchiverFileRepository filesharesArchiverFileRepository;

    @Override
    public Pair<String, File> getFile(UUID fileUUID, User user) {
        boolean isAccessible = socialClient.checkAccess(fileUUID);
        if (isAccessible) {
            FilesharesArchiverFile filesharesArchiveFile = filesharesArchiverFileRepository.findByFilesharesFilesFileUUID(fileUUID);
            String fullFileName = filesharesArchiveFile.getPath() + filesharesArchiveFile.getFileName() + filesharesArchiveFile.getExtension();
            return new Pair<>(filesharesArchiveFile.getOriginalName(), new File(fullFileName));
        }

        throw new RuntimeException("");
    }

    @Override
    public Pair<String, File> getFile(String shareLink, User user) {
        FilesharesSocialFile filesharesSocialFile = socialClient.getFilesharesSocialFile(shareLink);

        if (filesharesSocialFile != null) {
            FilesharesArchiverFile filesharesArchiveFile = filesharesArchiverFileRepository.findByFilesharesFilesFileUUID(filesharesSocialFile.getFilesServiceFileUUID());
            String fullFileName = filesharesArchiveFile.getPath() + filesharesArchiveFile.getFileName() + filesharesArchiveFile.getExtension();
            return new Pair<>(filesharesArchiveFile.getOriginalName(), new File(fullFileName));
        }

        return null;
    }
}
