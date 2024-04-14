package element.binder.plugin.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.Instant;

@Data
@Entity
@Table(name = "elements")
public class Element {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "element_seq")
    @SequenceGenerator(name = "element_seq", sequenceName = "element_sequence", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "article")
    private String article;

    @Column(name = "size")
    private String size;

    @Column(name = "material_name")
    private String materialName;

    @Column(name = "price")
    private Double price;

    @Column(name = "create_date")
    private Instant createDate;

    @PrePersist
    private void onCreate() {
        createDate = Instant.now();
    }
}
