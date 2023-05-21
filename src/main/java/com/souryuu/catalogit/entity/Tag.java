package com.souryuu.catalogit.entity;

import com.google.common.base.Objects;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TAGS")
@NoArgsConstructor @RequiredArgsConstructor @ToString
public class Tag {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    @Getter
    private long tagID;

    @Getter @Setter
    @ToString.Include
    @Column(name = "tag_name") @NonNull
    private String tagName;

    @Getter @Setter @ToString.Exclude
    @Column(name = "tag_description")
    private String tagDescription;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag tag = (Tag) o;
        return Objects.equal(getTagName(), tag.getTagName());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTagName());
    }

}
