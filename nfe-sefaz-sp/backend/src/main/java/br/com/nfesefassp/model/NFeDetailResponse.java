package br.com.nfesefassp.model;

import java.util.List;

public record NFeDetailResponse(
        NFe nfe,
        List<NFeItem> items
) {}
