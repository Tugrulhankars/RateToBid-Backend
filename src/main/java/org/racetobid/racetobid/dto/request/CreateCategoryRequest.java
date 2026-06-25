package org.racetobid.racetobid.dto.request;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CreateCategoryRequest {

    private String name;
    private String description;
}
