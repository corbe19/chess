package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameDAOTests {
    private GameDAO gameDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        gameDAO = new GameDAO();
        gameDAO.clear();

        userDAO = new UserDAO();
        userDAO.clear();

        userDAO.insertUser(new UserData("whiteUser", "pass", "white@example.com"));
        userDAO.insertUser(new UserData("blackUser", "pass", "black@example.com"));
    }

    @Test
    public void insertGamePositive() throws DataAccessException {
        int id = gameDAO.insertGame(new GameData(0, "whiteUser", "blackUser", "Test Game", new ChessGame()));
        assertNotNull(gameDAO.getGame(id));
    }

    @Test
    public void insertGameNegative() {
        GameData invalidGame = new GameData(0, "ghost", "ghost", "Invalid", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.insertGame(invalidGame));
    }


    @Test
    public void getGamePositive() throws DataAccessException {
        int id = gameDAO.insertGame(new GameData(0, "whiteUser", "blackUser", "Get Test", new ChessGame()));
        assertNotNull(gameDAO.getGame(id));
    }

    @Test
    public void getGameNegative() throws DataAccessException {
        assertNull(gameDAO.getGame(9999));
    }

    @Test
    public void listAllGamesPositive() throws DataAccessException {
        gameDAO.insertGame(new GameData(0, "whiteUser", "blackUser", "Game A", new ChessGame()));
        gameDAO.insertGame(new GameData(0, "whiteUser", "blackUser", "Game B", new ChessGame()));

        var allGames = gameDAO.listAllGames();
        assertEquals(2, allGames.size());
    }

    @Test
    public void listAllGamesNegative() throws DataAccessException {
        var allGames = gameDAO.listAllGames();
        assertTrue(allGames.isEmpty());
    }

    @Test
    public void updateGamePositive() throws DataAccessException {
        int id = gameDAO.insertGame(new GameData(0, "whiteUser", "blackUser", "Update Test", new ChessGame()));
        ChessGame updated = new ChessGame();
        updated.setTeamTurn(ChessGame.TeamColor.BLACK);
        gameDAO.updateGame(id, updated);
        assertEquals(ChessGame.TeamColor.BLACK, gameDAO.getGame(id).game().getTeamTurn());
    }

    @Test
    public void updateGameNegative() {
        ChessGame game = new ChessGame();
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(9999, game));
    }

    @Test
    public void deleteGamePositive() throws DataAccessException {
        int id = gameDAO.insertGame(new GameData(0, "whiteUser", "blackUser", "Delete Test", new ChessGame()));
        gameDAO.deleteGame(id);
        assertNull(gameDAO.getGame(id));
    }

    @Test
    public void deleteGameNegative() {
        assertThrows(DataAccessException.class, () -> gameDAO.deleteGame(9999));
    }

    @Test
    public void clearPositive() throws DataAccessException {
        //use id to avoid auto increment quirks maybe?
        int id = gameDAO.insertGame(new GameData(0, "whiteUser", "blackUser", "Clear Test", new ChessGame()));
        assertNotNull(gameDAO.getGame(id));
        gameDAO.clear();
        assertNull(gameDAO.getGame(id));
    }

    @Test
    public void persistenceCheck() throws DataAccessException {
        // Reset tables explicitly
        userDAO.clear();
        gameDAO.clear();

        // Insert required users
        userDAO.insertUser(new UserData("whiteUser", "pass", "white@example.com"));
        userDAO.insertUser(new UserData("blackUser", "pass", "black@example.com"));

        // Insert a game
        int id = gameDAO.insertGame(new GameData(0, "whiteUser", "blackUser", "Test Game", new ChessGame()));

        // Retrieve all games
        List<GameData> allGames = gameDAO.listAllGames();

        // Assert the inserted game exists
        assertTrue(allGames.stream().anyMatch(g -> g.gameID() == id));
    }
}