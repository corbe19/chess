package model;

import java.util.Collection;

public record ListGamesResult(
        Collection<GameData> games
) {
}
