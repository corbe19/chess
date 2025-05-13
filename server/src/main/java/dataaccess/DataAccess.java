package dataaccess;

public interface DataAccess {
    void clearUsers() throws DataAccessException;
    void clearGames() throws DataAccessException;
    void clearAuths() throws DataAccessException;
}