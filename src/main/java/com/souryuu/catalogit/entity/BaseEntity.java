package com.souryuu.catalogit.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@EqualsAndHashCode(of = {"uuid"})
public abstract class BaseEntity {
    @Getter @Setter
    private UUID uuid = UUID.randomUUID();
}
