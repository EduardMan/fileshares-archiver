package tech.itparklessons.fileshares.social.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArchiveFileRabbitMessage implements Serializable {
    private UUID fileUUID;
    private int compressionLevel;
    private String originalFileName;
}
