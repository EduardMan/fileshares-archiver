package tech.itparklessons.fileshares.archiver.controller;

import kotlin.Pair;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.itparklessons.fileshares.archiver.model.User;
import tech.itparklessons.fileshares.archiver.service.ArchiverService;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/archiver")
@RestController
public class ArchiverController {
    private final ArchiverService archiverService;

    @GetMapping("/getFile")
    public ResponseEntity<Resource> getFile(@RequestParam UUID fileUUID,
                                            @AuthenticationPrincipal User user) throws FileNotFoundException {

        Pair<String, File> archiverServiceFile = archiverService.getFile(fileUUID, user);
        InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(archiverServiceFile.component2()));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archiverServiceFile.component1() + "\"")
                .body(inputStreamResource);
    }

    @GetMapping("/getFileByShareLink")
    public ResponseEntity<Resource> getFile(@RequestParam String shareLink,
                                            @AuthenticationPrincipal User user) throws FileNotFoundException {

        Pair<String, File> archiverServiceFile = archiverService.getFile(shareLink, user);

        InputStreamResource inputStreamResource = new InputStreamResource(new FileInputStream(archiverServiceFile.component2()));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + archiverServiceFile.component1() + "\"")
                .body(inputStreamResource);
    }
}