package tech.itparklessons.fileshares.archiver.model.dto;


import lombok.Data;

import java.util.UUID;

@Data
public class FilesharesSocialFile {
    private Long ownerId;
    private UUID filesServiceFileUUID;
}
