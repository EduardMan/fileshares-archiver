package tech.itparklessons.fileshares.archiver.listener;


import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import tech.itparklessons.fileshares.archiver.client.FilesClient;
import tech.itparklessons.fileshares.archiver.repository.FilesharesArchiverFileRepository;
import tech.itparklessons.fileshares.social.model.dto.ArchiveFileRabbitMessage;
import tech.itparklessons.fileshares.archiver.model.entity.FilesharesArchiverFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
@RequiredArgsConstructor
public class RabbitTaskListener {
    private final FilesClient filesClient;
    private final FilesharesArchiverFileRepository filesharesArchiverFileRepository;

    @Value("${application.storage-path}")
    private String storagePath;

    @RabbitListener(queues = "#{'${application.queues.files-for-archivation}'.split(',')}")
    public void archivateFile(ArchiveFileRabbitMessage message) throws IOException {
        File file = filesClient.getFile(message.getFileUUID());
        String fileName = RandomStringUtils.random(15, true, true);
        String fullFileName = getFullStoragePath() + fileName + ".zip";

        try (FileOutputStream fos = new FileOutputStream(fullFileName);
             ZipOutputStream zipOut = new ZipOutputStream(fos);
             FileInputStream fis = new FileInputStream(file);
        ) {
            zipOut.setLevel(message.getCompressionLevel());
            ZipEntry zipEntry = new ZipEntry(message.getOriginalFileName());
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }

        FilesharesArchiverFile filesharesArchiverFile = new FilesharesArchiverFile();
        filesharesArchiverFile.setFilesharesFilesFileUUID(message.getFileUUID());
        filesharesArchiverFile.setFileName(fileName);
        filesharesArchiverFile.setExtension("zip");
        filesharesArchiverFile.setPath(getFullStoragePath());
        filesharesArchiverFile.setOriginalName(message.getOriginalFileName());
        filesharesArchiverFile.setSize(new File(fullFileName).length());

        filesharesArchiverFileRepository.save(filesharesArchiverFile);
    }

    @RabbitListener(queues = "${application.queues.deleted-files}")
    public void deleteFile(List<UUID> fileUUIDs) {
        List<FilesharesArchiverFile> filesharesArchiverFiles = filesharesArchiverFileRepository.findAllByFilesharesFilesFileUUIDIn(fileUUIDs);

        for (FilesharesArchiverFile filesharesArchiverFile : filesharesArchiverFiles) {
            File file = new File(filesharesArchiverFile.getPath() + filesharesArchiverFile.getFileName() + "." + filesharesArchiverFile.getExtension());
            if (file.delete()) {
                filesharesArchiverFile.setDeleted(true);
            }
        }

        filesharesArchiverFileRepository.saveAll(filesharesArchiverFiles);
    }

    private String getFullStoragePath() {
        LocalDateTime ldt = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        int year = ldt.getYear();
        int month = ldt.getMonthValue();
        int dayOfMonth = ldt.getDayOfMonth();

        return storagePath + year + "/" + month + "/" + dayOfMonth + "/";
    }
}
