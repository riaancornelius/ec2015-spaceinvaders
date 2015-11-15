package za.co.entelect.challenge.bot;

import za.co.entelect.challenge.dto.*;
import za.co.entelect.challenge.dto.enums.ShipCommand;
import za.co.entelect.challenge.dto.reader.BasicGameStateReader;
import za.co.entelect.challenge.dto.reader.GameStateReader;
import za.co.entelect.challenge.utils.BotHelper;
import za.co.entelect.challenge.utils.FileHelper;
import za.co.entelect.challenge.utils.LogHelper;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public class BasicBot {

    private static final int FIRING_POSITION = 4;
    private static final int SAFE_POSITION = 2;

    private Settings settings = null;

    public BasicBot(Settings settings) {
        this.settings = settings;
    }

    private GameState gameState;

    private Player myPlayer;

    public void execute() {
        //Choose how you want to access the JSON
        GameStateReader reader = 
                new BasicGameStateReader();
                //new JacksonGameStateReader();
                //new GsonGameStateReader();

        gameState = loadGameState(reader);

        myPlayer = findMyPlayer();

        logMatchState();

        StringBuilder map = loadMap();
        LogHelper.logPartsOnDifferentLines("Map", map);

        //String move = getRandomMove();
        String move = getNextMove();

        saveMove(move);
    }

    private GameState loadGameState(GameStateReader reader) {
        GameState gameState = null;

        File jsonFile = FileHelper.getFile(settings.getDefaultOutputFolder(), settings.getStateFile());

        try {
            gameState = reader.read(jsonFile);
        } catch (Exception ioe) {
            LogHelper.log("Error reading state file: " + settings.getStateFile());
            ioe.printStackTrace();
            return null;
        }

        return gameState;
    }

    private void logMatchState() {
        LogHelper.log(LogHelper.PREFIX + "Game state:");
        LogHelper.log("\tRound: " + gameState.getRoundNumber());

        for (Player player : getPlayers()) {
            logPlayerState(player);
        }
    }

    public GameState getGameState() {
        return gameState;
    }

    public List<Player> getPlayers() {
        return gameState.getPlayers();
    }

    private void logPlayerState(Player player) {
        LogHelper.log("\tPlayer " + player.getPlayerNumberReal() + " Kills: " + player.getKills());
        LogHelper.log("\tPlayer " + player.getPlayerNumberReal() + " Lives: " + player.getLives());
        LogHelper.log("\tPlayer " + player.getPlayerNumberReal() + " Missiles: " + player.getMissiles().size() + "/" + player.getMissileLimit());
    }

    private StringBuilder loadMap() {
        try {
            return FileHelper.readFile(settings.getDefaultOutputFolder(), settings.getMapFile());
        } catch(IOException ioe) {
            LogHelper.log("Unable to read map file: " + settings.getMapFile());
            ioe.printStackTrace();
            return new StringBuilder();
        }
    }

//    private String getRandomMove() {
//        long seed = System.currentTimeMillis();
//        Random random = new Random(seed);
//        int move = Math.abs(random.nextInt() % ShipCommand.values().length);
//        String translatedMove = BotHelper.translateMove(move);
//        return translatedMove;
//    }

    private ShipCommand getRandomMove() {
        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        return ShipCommand.values()[Math.abs(random.nextInt() % ShipCommand.values().length)];
    }

    private String getNextMove() {
        ShipCommand command;
        Ship ship = myPlayer.getShip();
        System.out.println("Ship position for: " + ship.getPlayerNumber() + " is " + ship.getX());
        switch (gameState.getRoundNumber()) {
            case 0:
                command = ShipCommand.MoveLeft;
                break;
            case 1:
                command = ShipCommand.MoveLeft;
                break;
            case 2:

                command = ShipCommand.BuildShield;
                break;
            case 3:
                command = ShipCommand.MoveLeft;
                break;
            case 4:
                command = ShipCommand.BuildAlienFactory;
                break;
            case 5:
                command = ShipCommand.MoveLeft;
                break;
            case 6:
                command = ShipCommand.Shoot;
                break;
            case 7:
                command = ShipCommand.MoveLeft;
                break;
            case 8:
                command = ShipCommand.MoveLeft;
                break;
            case 9:
                command = ShipCommand.BuildMissileController;
                break;
            case 10:
                command = ShipCommand.MoveRight;
                break;
            case 11:
                command = ShipCommand.MoveRight;
                break;
            case 12:
                command = ShipCommand.Shoot;
                break;
            case 13:
                command = ShipCommand.MoveRight;
                break;
            case 14:
                command = ShipCommand.MoveRight;
                break;
            default:
                command = getCommandAfterInitialSequence();
        }
        return BotHelper.translateMove(command.ordinal());
    }

    private ShipCommand getCommandAfterInitialSequence() {
        System.out.println("After initial sequence");
        int missiles = myPlayer.getMissiles().size();
        System.out.println("Missiles: " + missiles);
        if (missiles < 2) {
            System.out.println("Missiles available... Checking options");
            // We have missiles available
            if (isInFiringPosition()){
                return ShipCommand.Shoot;
            } else if (isLeftOfFiringPosition()) {
                return ShipCommand.MoveRight;
            } else if (isRightOfFiringPosition()) {
                return ShipCommand.MoveLeft;
            }
        } else {
            // No missiles
            System.out.println("No missiles available... Hiding");
            if (!isInSafePosition() && isLeftOfSafePosition()) {
                return ShipCommand.MoveRight;
            } else if (!isInSafePosition() && !isRightOfSafePosition()){
                return ShipCommand.MoveLeft;
            }
        }
        return ShipCommand.Nothing;
    }

    private boolean isInFiringPosition(){
        boolean ret = FIRING_POSITION == myPlayer.getShip().getX();
        System.out.println("Is in firing position: " + ret);
        return ret;
    }

    private boolean isLeftOfFiringPosition(){
        boolean ret = FIRING_POSITION > myPlayer.getShip().getX();
        System.out.println("Is left of firing position: " + ret);
        return ret;
    }

    private boolean isRightOfFiringPosition(){
        boolean ret = FIRING_POSITION < myPlayer.getShip().getX();
        System.out.println("Is right of firing position: " + ret);
        return ret;
    }

    private boolean isInSafePosition(){
        boolean ret = SAFE_POSITION == myPlayer.getShip().getX();
        System.out.println("Is in safe position: " + ret);
        return ret;
    }

    private boolean isLeftOfSafePosition(){
        boolean ret = SAFE_POSITION > myPlayer.getShip().getX();
        System.out.println("Is left of firing position: " + ret);
        return ret;
    }

    private boolean isRightOfSafePosition(){
        boolean ret = SAFE_POSITION < myPlayer.getShip().getX();
        System.out.println("Is right of firing position: " + ret);
        return ret;
    }

    private Player findMyPlayer() {
        List<Player> players = gameState.getPlayers();
        for (Player player : players) {
            if (player.getPlayerNumber() == 1) {
                return player;
            }
        }
        return null;
    }

    private void saveMove(String move) {
        try {
            FileHelper.writeFile(settings.getDefaultOutputFolder(), settings.getOutputFile(), move);
            LogHelper.log("Move", move);
        } catch (IOException ioe) {
            LogHelper.log("Unable to write move file: " + settings.getOutputFile());
            ioe.printStackTrace();
        }
    }
}
