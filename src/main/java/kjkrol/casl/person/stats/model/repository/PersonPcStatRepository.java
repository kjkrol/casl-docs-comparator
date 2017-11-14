package kjkrol.casl.person.stats.model.repository;

import kjkrol.casl.person.stats.model.PersonPcStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PersonPcStatRepository extends JpaRepository<PersonPcStat, String> {

    @Query(value = "SELECT person_pc_stat.id FROM person_pc_stat "
            + "LEFT JOIN person_ids ON (person_pc_stat.id = person_ids.id) "
            + "WHERE person_ids.id IS NULL AND length(person_pc_stat.id) = 36 "
            + "ORDER BY ?#{#pageable}",
            nativeQuery = true)
    Page<String> findMissingPersons(Pageable pageable);
}
