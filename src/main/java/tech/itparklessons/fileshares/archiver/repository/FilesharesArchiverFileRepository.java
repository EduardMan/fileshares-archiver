package tech.itparklessons.fileshares.archiver.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tech.itparklessons.fileshares.archiver.model.entity.FilesharesArchiverFile;

import java.util.List;
import java.util.UUID;

public interface FilesharesArchiverFileRepository extends JpaRepository<FilesharesArchiverFile, UUID> {
    List<FilesharesArchiverFile> findAllByFilesharesFilesFileUUIDIn(List<UUID> filesharesFilesFileUUIDs);

    FilesharesArchiverFile findByFilesharesFilesFileUUID(UUID filesharesFilesFileUUID);
}
