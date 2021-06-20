package tech.itparklessons.fileshares.archiver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.File;
import java.util.UUID;

@FeignClient(url = "http://localhost:8083/api/file-storage/internal", name = "file")
public interface FilesClient {
    @GetMapping("/getFile")
    File getFile(@RequestParam UUID fileUUID);
}