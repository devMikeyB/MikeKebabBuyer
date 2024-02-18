package net.botwithus.debug;

import net.botwithus.rs3.imgui.ImGui;
import net.botwithus.rs3.script.ScriptConsole;
import net.botwithus.rs3.script.ScriptGraphicsContext;

import java.time.Duration;
import java.time.Instant;


public class DebugGraphicsContext extends ScriptGraphicsContext {

    private final KebabScript script;
    private Instant startTime;

    public DebugGraphicsContext(ScriptConsole console, KebabScript script) {
        super(console);
        this.script = script;
        this.startTime = Instant.now();
    }

    public void drawSettings() {
        ImGui.SetWindowSize(200.f, 200.f);

        if (ImGui.Begin("Mike's Kebab purchaser!", 0)) {


            if (ImGui.BeginTabBar("Stats and Instructions", 0)) {


                if (ImGui.BeginTabItem("Statistics", 0)) {
                    drawStatisticsTab();
                    ImGui.EndTabItem();
                }


                if (ImGui.BeginTabItem("Instructions", 0)) {
                    drawInstructionsTab();
                    ImGui.EndTabItem();
                }

                if (ImGui.BeginTabItem("ConsoleLog", 0)) {
                    drawConsoleLogTab();
                    ImGui.EndTabItem();
                }

                ImGui.EndTabBar();
            }

            ImGui.End();
        }
    }

    public void drawStatisticsTab() {
        ImGui.Text("Current Activity: " + script.getCurrentActivity());

        ImGui.Separator();

        ImGui.Text("Loop Count: " + script.getLoopCounter());

        ImGui.Separator();

        Duration elapsed = Duration.between(startTime, Instant.now());
        long hours = elapsed.toHours();
        long minutes = elapsed.toMinutesPart();
        long seconds = elapsed.toSecondsPart();
        String elapsedTimeFormatted = String.format("Elapsed Time: %02d:%02d:%02d", hours, minutes, seconds);
        ImGui.Text(elapsedTimeFormatted);

        ImGui.Separator();

    }

    private void drawInstructionsTab() {
        ImGui.Text("Instructions:");
        ImGui.Text("1) Al-Kharid near Karim.");
        ImGui.Text("2) Have coin.");
        ImGui.Text("3) Profit!");
    }

    private void drawConsoleLogTab() {
        for (String message : script.consoleMessages) {
            if (message != null){
                ImGui.Text(message);
            }
        }
    }
}