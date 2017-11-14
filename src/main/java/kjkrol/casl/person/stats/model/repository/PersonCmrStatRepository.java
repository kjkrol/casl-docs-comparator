package kjkrol.casl.person.stats.model.repository;

import kjkrol.casl.person.stats.model.PersonCmrStat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonCmrStatRepository extends JpaRepository<PersonCmrStat, String> {
}
