package websocket.commands;

public class LeaveCommand extends UserGameCommand {
    public LeaveCommand(String authToken, Integer gameID) {
        super(CommandType.LEAVE, authToken, gameID);
    }
}
