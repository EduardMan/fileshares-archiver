package tech.itparklessons.fileshares.archiver.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tech.itparklessons.fileshares.archiver.model.dto.FilesharesSocialFile;

import java.util.UUID;

@FeignClient(url = "http://fileshares-social-service:8080/internal/social", name = "social")
public interface SocialClient {
    @GetMapping("/checkAccess")
    boolean checkAccess(@RequestParam UUID fileUUID);

    @GetMapping("/getByShareLink")
    FilesharesSocialFile getFilesharesSocialFile(@RequestParam String shareLink);
}
