package com.stockpilot.stock.entity;

import com.stockpilot.global.common.entity.Timestamp;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "sectors")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Sector extends Timestamp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "name_kr", length = 100)
    private String nameKr;

    @Builder
    public Sector(String name, String nameKr) {
        this.name = name;
        this.nameKr = nameKr;
    }
}
