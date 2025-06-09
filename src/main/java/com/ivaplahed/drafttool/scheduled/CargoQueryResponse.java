package com.ivaplahed.drafttool.scheduled;

import java.util.List;

public record CargoQueryResponse(List<CargoEntry> cargoquery) {

    public record CargoEntry(Title title) {
        public record Title(
                String Name,
                String ID,
                String Role,
                String Country,
                String FileName,
                String Champion,
                String Title,
                String Attributes
        ) {}
    }
}
