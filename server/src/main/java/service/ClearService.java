package service;

import dataaccess.DataAccess;
import dataaccess.MemoryDataAccess;
import dataaccess.DataAccessException;

public class ClearService {
    private final DataAccess db = new MemoryDataAccess();

    public void clear() throws DataAccessException {
        db.clearUsers();
        db.clearGames();
        db.clearAuths();
    }
}