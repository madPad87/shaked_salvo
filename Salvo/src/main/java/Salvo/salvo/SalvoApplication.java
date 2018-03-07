package Salvo.salvo;

import com.fasterxml.jackson.databind.ObjectMapper;

//Import needed spring libraries

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootApplication
public class SalvoApplication {

	//Public - can be used by all code. void - returns no value. main - functions which is the "EXE" of the project.

	public static void main(String[] args) {
		SpringApplication.run(SalvoApplication.class);
	}

	@Bean
	public CommandLineRunner initData(PlayerRepository playerRepository, GameRepository gameRepository, GamePlayerRepository gamePlayerRepository,
									  ShipRepository shipRepository, SalvoRepository salvoRepository, ScoreRepository scoreRepository) {
		return (args) -> {

			// Instantiate all classes

//			Player bean = new Player("bean@gmail.com", "bean123");
//			Player achilles = new Player("achilles@gmail.com", "achilles123");
//			Player ender = new Player("ender@gmail.com", "ender123");
//			Player ansett = new Player("ansett@gmail.com", "ansett123");
//
//            Game one = new Game(Date.from(new Date().toInstant().plusSeconds(3000)));
//            Game two = new Game(Date.from(new Date().toInstant().plusSeconds(5000)));
//            Game three = new Game(Date.from(new Date().toInstant().plusSeconds(7000)));
//            Game four = new Game(Date.from(new Date().toInstant().plusSeconds(9000)));
//            Game five = new Game(Date.from(new Date().toInstant().plusSeconds(11000)));
//            Game six = new Game(Date.from(new Date().toInstant().plusSeconds(13000)));
//            Game seven = new Game(Date.from(new Date().toInstant().plusSeconds(15000)));
//            Game eight = new Game(Date.from(new Date().toInstant().plusSeconds(17000)));
//
//			Ship shipOne = new Ship(Ship.Type.Carrier, Arrays.asList("A3", "B3", "C3"));
//			Ship shipTwo = new Ship(Ship.Type.Battleship, Arrays.asList("C7", "C6"));
//			Ship shipThree = new Ship(Ship.Type.PatrolBoat, Arrays.asList("G4", "G3", "G2", "G1"));
//			Ship shipFour = new Ship(Ship.Type.Destroyer, Arrays.asList("I2", "I3", "I4", "I5"));
//			Ship shipFive = new Ship(Ship.Type.Submarine, Arrays.asList("F4", "F5"));
//			Ship shipSix = new Ship(Ship.Type.Carrier, Arrays.asList("G6", "G7", "G8", "G9", "G10"));
//			Ship shipSeven = new Ship(Ship.Type.Battleship, Arrays.asList("A9", "B9", "C9"));
//			Ship shipEight = new Ship(Ship.Type.PatrolBoat, Arrays.asList("H3", "G3", "F3", "E3"));
//			Ship shipNine = new Ship(Ship.Type.Destroyer, Arrays.asList("J1", "J2", "J3"));
//			Ship shipTen = new Ship(Ship.Type.Submarine, Arrays.asList("A5", "B5", "C5"));
//			Ship shipEleven = new Ship(Ship.Type.Carrier, Arrays.asList("H2", "I2", "J2"));
//			Ship shipTwelve = new Ship(Ship.Type.Battleship, Arrays.asList("C7", "C6", "C5"));
//			Ship shipThirteen = new Ship(Ship.Type.PatrolBoat, Arrays.asList("A4", "B4", "C4"));
//			Ship shipFourteen = new Ship(Ship.Type.Destroyer, Arrays.asList("H3", "G2", "F1"));
//			Ship shipFifteen = new Ship(Ship.Type.Submarine, Arrays.asList("F5", "G6", "H7"));
////			Ship shipSixteen = new Ship(Ship.Type.Cruiser, Arrays.asList("C1", "D2", "E3"));
//
//            GamePlayer first = new GamePlayer(bean, one, new Date());
//            GamePlayer second = new GamePlayer(achilles, one, new Date());
//			GamePlayer third = new GamePlayer(ender, two, new Date());
//			GamePlayer fourth = new GamePlayer(ansett, two, new Date());
//			GamePlayer fifth = new GamePlayer(bean, three, new Date());
//			GamePlayer sixth = new GamePlayer(achilles, three, new Date());
//			GamePlayer seventh = new GamePlayer(ender, four, new Date());
//			GamePlayer eighth = new GamePlayer(ansett, four, new Date());
//			GamePlayer ninth = new GamePlayer(bean, five, new Date());
//			GamePlayer tenth = new GamePlayer(achilles, five, new Date());
//			GamePlayer eleventh = new GamePlayer(ender, six, new Date());
//			GamePlayer twelfth = new GamePlayer(ansett, six, new Date());
//			GamePlayer thirteenth = new GamePlayer(bean, seven, new Date());
//			GamePlayer fourteenth = new GamePlayer(ender, seven, new Date());
//			GamePlayer fifteenth = new GamePlayer(ansett, eight, new Date());
//			GamePlayer sixteenth = new GamePlayer(achilles, eight, new Date());
//
//			first.addShip(shipOne);
//			first.addShip(shipTwo);
//			first.addShip(shipThree);
//			first.addShip(shipFour);
//			first.addShip(shipFive);
//			second.addShip(shipSix);
//			second.addShip(shipSeven);
//			second.addShip(shipEight);
//			second.addShip(shipNine);
//			second.addShip(shipTen);
//			fourth.addShip(shipEleven);
//			fourth.addShip(shipTwelve);
//			fifth.addShip(shipThirteen);
//			fifth.addShip(shipFourteen);
//			fifth.addShip(shipFifteen);
//
//			Salvo a = new Salvo(first, 1, Arrays.asList("H1", "H3"));
//			Salvo b = new Salvo(second, 1,Arrays.asList("A3", "J6"));
//			Salvo c = new Salvo(first, 2,Arrays.asList("B6", "I8"));
//			Salvo d = new Salvo(second, 2,Arrays.asList("C1", "E9"));
//			Salvo e = new Salvo(first, 3,Arrays.asList("D6", "B9"));
//			Salvo f = new Salvo(second, 3,Arrays.asList("E2", "A1"));
//			Salvo g = new Salvo(third, 1,Arrays.asList("F2", "E6"));
//			Salvo h = new Salvo(fourth, 1,Arrays.asList("G4", "B3"));
//			Salvo i = new Salvo(third, 2,Arrays.asList("H6", "J7"));
//			Salvo j = new Salvo(fourth, 2,Arrays.asList("I4", "E1"));
//			Salvo k = new Salvo(third, 3,Arrays.asList("J7", "A7"));
//			Salvo l = new Salvo(fourth, 3,Arrays.asList("B7", "H3"));
//
//			Score scoreOne = new Score(1, one, bean);
//			Score scoreTwo = new Score(0, one, achilles);
//			Score scoreThree = new Score(1, two, ender);
//			Score scoreFour = new Score(0, two, ansett);
//			Score scoreFive = new Score(1, three, achilles);
//			Score scoreSix = new Score(0, three, bean);
//			Score scoreSeven = new Score(1, four, ansett);
//			Score scoreEight = new Score(0, four, ender);
//			Score scoreNine = new Score(0.5, five, bean);
//			Score scoreTen = new Score(0.5, five, achilles);
//			Score scoreEleven = new Score(0.5, six, ender);
//			Score scoreTwelve = new Score(0.5, six, ansett);
//			Score scoreThirteen = new Score(1, seven, ender);
//			Score scoreFourteen = new Score(0, seven, bean);
//			Score scoreFifteen = new Score(0, eight, ansett);
//			Score scoreSixteen = new Score(1, eight, achilles);
//			// Save to repositories
//            playerRepository.save(bean);
//			playerRepository.save(ender);
//			playerRepository.save(achilles);
//			playerRepository.save(ansett);
//            gameRepository.save(one);
//			gameRepository.save(two);
//			gameRepository.save(three);
//			gameRepository.save(four);
//			gameRepository.save(five);
//			gameRepository.save(six);
//			gameRepository.save(seven);
//			gameRepository.save(eight);
//			gamePlayerRepository.save(first);
//			gamePlayerRepository.save(second);
//			gamePlayerRepository.save(third);
//			gamePlayerRepository.save(fourth);
//			gamePlayerRepository.save(fifth);
//			gamePlayerRepository.save(sixth);
//			gamePlayerRepository.save(seventh);
//			gamePlayerRepository.save(eighth);
//			gamePlayerRepository.save(ninth);
//			gamePlayerRepository.save(tenth);
//			gamePlayerRepository.save(eleventh);
//			gamePlayerRepository.save(twelfth);
//			gamePlayerRepository.save(thirteenth);
//			gamePlayerRepository.save(fourteenth);
//			gamePlayerRepository.save(fifteenth);
//			gamePlayerRepository.save(sixteenth);
//			shipRepository.save(shipOne);
//			shipRepository.save(shipTwo);
//			shipRepository.save(shipThree);
//			shipRepository.save(shipFour);
//			shipRepository.save(shipFive);
//			shipRepository.save(shipSix);
//			shipRepository.save(shipSeven);
//			shipRepository.save(shipEight);
//			shipRepository.save(shipNine);
//			shipRepository.save(shipTen);
//			shipRepository.save(shipEleven);
//			shipRepository.save(shipTwelve);
//			shipRepository.save(shipThirteen);
//			shipRepository.save(shipFourteen);
//			shipRepository.save(shipFifteen);
////			shipRepository.save(shipSixteen);
//			salvoRepository.save(a);
//			salvoRepository.save(b);
//			salvoRepository.save(c);
//			salvoRepository.save(d);
//			salvoRepository.save(e);
//			salvoRepository.save(f);
//			salvoRepository.save(g);
//			salvoRepository.save(h);
//			salvoRepository.save(i);
//			salvoRepository.save(j);
//			salvoRepository.save(k);
//			salvoRepository.save(l);
//			scoreRepository.save(scoreOne);
//			scoreRepository.save(scoreTwo);
//			scoreRepository.save(scoreThree);
//			scoreRepository.save(scoreFour);
//			scoreRepository.save(scoreFive);
//			scoreRepository.save(scoreSix);
//			scoreRepository.save(scoreSeven);
//			scoreRepository.save(scoreEight);
//			scoreRepository.save(scoreNine);
//			scoreRepository.save(scoreTen);
//			scoreRepository.save(scoreEleven);
//			scoreRepository.save(scoreTwelve);
//			scoreRepository.save(scoreThirteen);
//			scoreRepository.save(scoreFourteen);
//			scoreRepository.save(scoreFifteen);
//			scoreRepository.save(scoreSixteen);
		};
	}
}


@Configuration
class WebSecurityConfiguration extends GlobalAuthenticationConfigurerAdapter {

	@Autowired
	PlayerRepository playerRepository;

	@Override
	public void init(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService());
	}

	@Bean
	UserDetailsService userDetailsService() {
		return new UserDetailsService() {

			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				List<Player> players = playerRepository.findByUserName(username);
				if (!players.isEmpty()) {
					Player player = players.get(0);
					return new User(player.getUserName(), player.getPassword(),
							AuthorityUtils.createAuthorityList("USER"));
				} else {
					throw new UsernameNotFoundException("Unknown user: " + username);
				}
			}
		};
	}
}

@EnableWebSecurity
@Configuration
class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/web/public/**").permitAll()
				.antMatchers("/web/images/**").permitAll()
				.antMatchers("/api/players/**").permitAll()
				.antMatchers("/**").hasAuthority("USER")
				.and()
				.formLogin();

		http.formLogin()
				.usernameParameter("username")
				.passwordParameter("password")
				.loginPage("/api/login");

		http.logout().logoutUrl("/api/logout");

		// turn off checking for CSRF tokens
		http.csrf().disable();

		// if user is not authenticated, just send an authentication failure response
		http.exceptionHandling().authenticationEntryPoint((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if login is successful, just clear the flags asking for authentication
		http.formLogin().successHandler((req, res, auth) -> clearAuthenticationAttributes(req));

		// if login fails, just send an authentication failure response
		http.formLogin().failureHandler((req, res, exc) -> res.sendError(HttpServletResponse.SC_UNAUTHORIZED));

		// if logout is successful, just send a success response
		http.logout().logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
	}

	private void clearAuthenticationAttributes(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
	}
}