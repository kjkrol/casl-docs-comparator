package kjkrol.casl.person.stats.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@EqualsAndHashCode(of = { "id" })
@ToString
@Getter
@Setter
@Entity
@Table(name = "person_pc_stat")
public class PersonPcStat {

    @Id
    private String id;

    @Column(name = "count")
    private int count;

    public PersonPcStat() {
    }

    public PersonPcStat(String id, int count) {
        this.id = id;
        this.count = count;
    }

}
