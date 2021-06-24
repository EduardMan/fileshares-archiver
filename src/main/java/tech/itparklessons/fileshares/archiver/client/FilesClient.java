package tech.itparklessons.fileshares.archiver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(url = "http://fileshares-files-service:8080/internal/files", name = "file")
public interface FilesClient {
    @GetMapping("/getFile")
    ResponseEntity<Resource> getFile(@RequestParam UUID fileUUID);
}