package tech.itparklessons.fileshares.archiver.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tech.itparklessons.fileshares.archiver.model.User;
import tech.itparklessons.fileshares.archiver.service.ArchiverService;

import java.io.File;
import java.util.UUID;

@RequiredArgsConstructor
@RequestMapping("/api/archiver")
@RestController
public class ArchiverController {
    private final ArchiverService archiverService;

    @GetMapping("/getFile")
    public File getFile(@RequestParam UUID fileUUID,
                        @AuthenticationPrincipal User user) {
        return archiverService.getFile(fileUUID, user);
    }

    @GetMapping("/getFile-by-share-link")
    public File getFile(@RequestParam String shareLink,
                        @AuthenticationPrincipal User user) {
        return archiverService.getFile(shareLink, user);
    }
}
