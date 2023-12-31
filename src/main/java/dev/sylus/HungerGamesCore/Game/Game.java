package dev.sylus.HungerGamesCore.Game;

import dev.sylus.HungerGamesCore.Enums.GameState;
import dev.sylus.HungerGamesCore.Files.Databases;
import dev.sylus.HungerGamesCore.Files.Files;
import dev.sylus.HungerGamesCore.HungerGamesCore;
import dev.sylus.HungerGamesCore.Tasks.GameCountDownTask;
import dev.sylus.HungerGamesCore.Utils.ServerUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class Game {
    private List<Player> playersAlive;
    private List<Player> spectators;
    private GameState.gameState gameState = GameState.gameState.TESTING;
    private boolean movement = true; // This controls if people can move or not
    private boolean playerCount = true; // This controls if the player count should go up when someone joins or not
    boolean doDamage = true; // If this is false then players cannot take damage
    HungerGamesCore main;
    GameCountDownTask countDownTask;
    ChestManager chestManager;
    Files files;
    Border border;
    ServerUtil serverUtil;
    Databases databases;

    public Game(HungerGamesCore mainInstance, ChestManager chestManagerInstance, Files filesInstance, Border borderInstance, ServerUtil serverUtilInstance, Databases databasesInstance){ // Constructor
        main = mainInstance;
        countDownTask = main.getGameCountDownTask();
        playersAlive = new ArrayList<Player>();
        spectators = new ArrayList<Player>();
        chestManager = chestManagerInstance;
        files = filesInstance;
        border = borderInstance;
        serverUtil = serverUtilInstance;
        databases = databasesInstance;
    }

    public void startGame(){
        if (!(gameState == GameState.gameState.PREGAME || gameState == GameState.gameState.TESTING)){
            main.logger.log(Level.WARNING, "Game start called outside of pregame");
            main.logger.log(Level.INFO, "Game state is: " + gameState + " Game state from method is: " + this.getState());
            return;
        }
        new GameCountDownTask(this, main, chestManager, files, border, serverUtil, databases).runTaskTimer(main, 0, 20);

        this.gameState = GameState.gameState.ACTIVE;
        this.movement = true;
    }
    public void endGame(){ // This is no longer used
        if (this.gameState == GameState.gameState.PREGAME){
            main.logger.log(Level.WARNING, "Game end called during the pregame");
            return;
        }

        if (this.playersAlive.size() < 2){

        }

        for (Player players: playersAlive){
             players.sendTitle(ChatColor.RED + "Game over", "", 1, 5, 3);
        }
    }

    public List getPlayers(){
        return this.playersAlive;
    }
    public int getPlayerNumbers(){
        return this.playersAlive.size();
    }

    public List getSpectators(){
        return this.spectators;
    }

    public boolean getMovement(){
        return this.movement;
    }

    public void setMovement(boolean state){
        this.movement = state;
    }

    public boolean getCount(){
        return this.playerCount;
    }

    public void removePlayer(Player deadPlayer){
        this.playersAlive.remove(deadPlayer);
        this.spectators.add(deadPlayer);
    }

    public void setState(GameState.gameState state, String who){
        Bukkit.getLogger().log(Level.INFO, who + " Changed the game state to: " + state);
        this.gameState = state;
    }

   /* public void broadcastMessage(String message){
        Bukkit.broadcastMessage(message);
    }
    There is no use for this now */


    public GameState.gameState getState(){
        return gameState;
    }

    public Game gaetGame(){
        return this;
    }

    public void addPlayer(Player player){
        if (playerCount) {
            playersAlive.add(player);
        } else {
            Bukkit.getLogger().log(Level.WARNING, "Player tried to be added while player count is false");
        }
    }

    public void setCounting(boolean bool){
        playerCount = bool;
    }

    public boolean getCounting(){
        return playerCount;
    }

    public Player getLastPlayer(){
        return playersAlive.get(0);
    }

    public boolean getDamageState(){
        return doDamage;
    }

    public void setVunrability(Boolean vulnerability){
        this.doDamage = vulnerability;
    }

    public boolean isPlayerAlive(Player player){
        return playersAlive.contains(player);
    }


}
