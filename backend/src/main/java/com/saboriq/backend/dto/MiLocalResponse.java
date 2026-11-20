package com.saboriq.backend.dto;

import java.util.List;

public record MiLocalResponse(
        LocalResponse local,
        List<MesaResponse> mesas,
        List<RepartidorResponse> repartidores) {
}