package Salvo.salvo;


//Importing needed libraries

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

//Creating a repository to hold instances of Player in the database.
//Making it a rest resource allows me to view it in JSON form while developing via HTTP request.

@RepositoryRestResource
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Player findOneByUserName (@Param("username") String userName);
    List<Player> findByUserName (@Param("username") String username);
 }