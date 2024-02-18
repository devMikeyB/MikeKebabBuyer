package net.botwithus.debug;

import net.botwithus.api.game.hud.Dialog;
import net.botwithus.api.game.hud.inventories.Backpack;
import net.botwithus.api.game.hud.inventories.Bank;
import net.botwithus.internal.scripts.ScriptDefinition;
import net.botwithus.rs3.events.impl.ServerTickedEvent;
import net.botwithus.rs3.game.queries.builders.characters.NpcQuery;
import net.botwithus.rs3.game.scene.entities.characters.npc.Npc;
import net.botwithus.rs3.script.LoopingScript;
import net.botwithus.rs3.script.config.ScriptConfig;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

public class KebabScript extends LoopingScript {

    private int loopCounter=0;
    private String currentActivity;
    public KebabScript(String name, ScriptConfig scriptConfig, ScriptDefinition scriptDefinition) {
        super(name, scriptConfig, scriptDefinition);
    }

    @Override
    public boolean initialize() {
        this.sgc = new DebugGraphicsContext(getConsole(), this);
        this.loopDelay = 590;
        subscribe(ServerTickedEvent.class, serverTickedEvent -> {
            theBrain();


        });
        return super.initialize();
    }

    @Override
    public void onLoop(){

    }

    public void theBrain() {

        loopCounter++;
        String stateString = getState();
        currentActivity = stateString;
        try{
            switch(stateString){
                case "Nice Kebab?":
                    niceKebab();
                    break;
                case "Say Yes":
                    sayYesToTheMan();
                    break;
                case "Take Kebab.":
                    takeKebab();
                    break;
                case "Talking to Kebab Man":
                    talkToKebabMan();
                    break;
                case "Depositing":
                    bankItems();
                    break;
            }
        } catch(Exception e){
            println(e.getMessage());
        }
    }

    public String[] consoleMessages = new String[15];
    private int consoleIndex = 0;
    public void printlnn(String message){
        println(message);
        consoleMessages[consoleIndex] = Instant.now().toString() + ": " + message;
        consoleIndex = (consoleIndex + 1) % consoleMessages.length;

    }

    private void talkToKebabMan(){
        printlnn("Talking to Karim.");
        Npc kebabMan = NpcQuery.newQuery().name("Karim").results().first();
        if (kebabMan != null){
            kebabMan.interact("Talk-to");
            try{
                AwaitCondition.await(() -> Dialog.isOpen(), 10, TimeUnit.SECONDS);
            } catch(InterruptedException e) {
                printlnn("Talk-to operation interrupted, trying again.");
            } catch (RuntimeException e) {
                printlnn("10 seconds without reaching Karim. Trying again.....");
            }
        }
    }
    private void niceKebab(){
            if (Dialog.isOpen() && Dialog.getText() != null && Dialog.getText().toString().contains("nice kebab?")){
                printlnn("Continue to options.");
                if (Dialog.select()){
                    printlnn("Continued.");
                    try{
                        printlnn("Waiting.....");
                        AwaitCondition.await(() -> Dialog.getText() == null, 4, TimeUnit.SECONDS);
                        printlnn("Waited.");
                        printlnn("State: "+ getState() + "\nText: "+ Dialog.getText());
                    } catch (Exception e){
                        printlnn(e.toString());
                    }
                }
            }
        }

        private void sayYesToTheMan(){
        if (Dialog.interact("Yes please.")){
            printlnn("Accepted offer.");
            try{
                printlnn("Waiting for 'Yes Please.'");
                AwaitCondition.await(() -> Dialog.select(), 4, TimeUnit.SECONDS);
                printlnn("Waited successfully.");
                printlnn("State: "+ getState() + "\nText: "+ Dialog.getText());
            } catch (Exception e){
                printlnn(e.toString());
            }
        }
        }
        private void takeKebab(){
        if (Dialog.getText().toString().contains("Yes")){
            if(Dialog.select()){
                try{
                    AwaitCondition.await(() -> !Dialog.isOpen(), 3, TimeUnit.SECONDS);
                } catch (Exception e){
                    printlnn(e.toString());
                }
            }
        }
        }

    private void bankItems(){
        Bank.open();
        try{
            AwaitCondition.await(() -> Bank.isOpen(), 12, TimeUnit.SECONDS);
        } catch(Exception e){
            printlnn("Something happened: " + e.getMessage());
        }
        if (Bank.isOpen()){
            Bank.depositAll();
        }

    }

    private String getState(){
        if (Dialog.isOpen() && Dialog.getText() != null && Dialog.getText().toString().contains("please")) {
            return "Take Kebab.";
        }
        if (Dialog.isOpen() && Dialog.getText() != null && Dialog.getText().toString().contains("nice kebab?")){
            return "Nice Kebab?";
        }
        if (Dialog.isOpen() && Dialog.getOptions().toArray().length > 0){//[1].toString().contains("Yes")){
            return "Say Yes";
        }
        if(Backpack.isFull()){
            return "Depositing";
        }
        if(!Backpack.isFull()){
            return "Talking to Kebab Man";
        }
        return "Stateless. Are you next to the Kebab Man in Al-Kharid?";
    }


    public int getLoopCounter() {
        return loopCounter;
    }

    public String getCurrentActivity() {
        return currentActivity;
    }

}
