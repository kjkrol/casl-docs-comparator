package kjkrol.casl.person.stats.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@EqualsAndHashCode(of = { "id" })
@ToString
@Getter
@Setter
@Entity
@Table(name = "person_ids")
public class PersonIds {

    @Id
    private String id;

    public PersonIds() {
    }

    public PersonIds(String id) {
        this.id = id;
    }

}
