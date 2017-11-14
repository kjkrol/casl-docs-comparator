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
@Table(name = "person_context_cmrs_stat")
public class PersonContextCmrStat {
    @Id
    private String id;

    @Column(name = "count")
    private int count;

    public PersonContextCmrStat() {
    }

    public PersonContextCmrStat(String id, int count) {
        this.id = id;
        this.count = count;
    }
}
