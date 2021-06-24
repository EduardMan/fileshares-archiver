package tech.itparklessons.fileshares.archiver.listener;


import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tech.itparklessons.fileshares.archiver.client.FilesClient;
import tech.itparklessons.fileshares.archiver.model.entity.FilesharesArchiverFile;
import tech.itparklessons.fileshares.archiver.repository.FilesharesArchiverFileRepository;
import tech.itparklessons.fileshares.social.model.dto.ArchiveFileRabbitMessage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

    @RabbitListener(queues = "#{'${application.queues.files-for-archivation}'.split(',')}", ackMode = "MANUAL")
    public void archivateFile(ArchiveFileRabbitMessage message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        ResponseEntity<Resource> file = filesClient.getFile(message.getFileUUID());
        String fileName = RandomStringUtils.random(25, true, true);
        String fullFileName = getFullStoragePath() + fileName + ".zip";
        File fileArchive = new File(getFullStoragePath());
        fileArchive.mkdirs();

        try (FileOutputStream fos = new FileOutputStream(fullFileName);
             ZipOutputStream zipOut = new ZipOutputStream(fos);
             InputStream fis = file.getBody().getInputStream();
        ) {
            zipOut.setLevel(message.getCompressionLevel());
            ZipEntry zipEntry = new ZipEntry(file.getBody().getFilename());
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
        filesharesArchiverFile.setOriginalName(file.getBody().getFilename());
        filesharesArchiverFile.setSize(new File(fullFileName).length());

        filesharesArchiverFileRepository.save(filesharesArchiverFile);

        channel.basicAck(tag, false);
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
