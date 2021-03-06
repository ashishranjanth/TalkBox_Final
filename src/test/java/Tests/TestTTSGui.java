package Tests;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.FlowPane;
import org.junit.After;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.testfx.api.FxRobot;
import org.testfx.api.FxToolkit;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.control.LabeledMatchers;
import org.testfx.matcher.control.ListViewMatchers;
import talkboxnew.Main;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

/*
    The first test class to test the functionality of the TTS class
    All the Tests here are ordered and performed in ascending order in lexicographic order
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class TestTTSGui extends ApplicationTest {

    // maybe change the structuring of the nodes to be a new class?
    // CreateConfigWizardTestingNodes.NamePane().Node; This should return a string of the node
    // CreateConfigWizardTestingNodes.FilePane().Node;
    // SplashStageTestingNodes.Node

    private <T extends Node> T lookfor(final String node) {
        return (T) lookup("#" + node).queryAll().iterator().next();
    }

    private void writeText(String text){
        new FxRobot().write(text);
    }

    @BeforeAll
    public static void launch() throws Exception {
        ApplicationTest.launch(Main.class);
    }

    @After
    public void afterEachTest() throws TimeoutException {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    // testAinitialAppearance test1
    public void testA_IAT1() {
        Button openButton = lookfor(SplashStageTestingNodes.OPEN_BUTTON);
        Assertions.assertThat(openButton).hasText("Open Existing Directory...");
    }

    @Test
    // initialAppearance2 test2
    public void testA_IAT2() {
        Button newButton = lookfor(SplashStageTestingNodes.NEW_BUTTON);
        Assertions.assertThat(newButton).hasText(" New TalkBox Directory...");
    }

    @Test
    // initialAppearance3 test3
    public void testA_IAT3() {
        ListView newView = lookfor(SplashStageTestingNodes.LIST);
        if (newView.getItems().isEmpty()){
            assertThat(newView, ListViewMatchers.isEmpty());
        }
    }

    @Test
    // Set Name Foobar 1
    public void testB_SNF1() {
        Button newButton = lookfor(SplashStageTestingNodes.NEW_BUTTON);
        clickOn(newButton);
        TextField newTextField = lookfor(CreateConfigWizardTestingNodes.NamePane.getIdTextfield());
        clickOn(newTextField);
        writeText("foobarTTS");
        Assertions.assertThat(newTextField).hasText("foobarTTS");
    }

    @Test
    // set "Enablers" On (previous button)
    public void testB_SON1() {
        try {
            clickOn(LabeledMatchers.hasText("Next"));
            CheckBox newCB1 = lookfor(CreateConfigWizardTestingNodes.FeaturesPane.getIdBackCheckBox());
            clickOn(newCB1);
            clickOn(newCB1);
            boolean result = newCB1.isSelected();
            Assert.assertTrue("Expected True", result);
        }
        catch(Exception e) {
            System.exit(1);
        }
    }

    @Test
    // set "Enablers" On (custom button)
    public void testB_SON2() {
        CheckBox newCB2 = lookfor(CreateConfigWizardTestingNodes.FeaturesPane.getIdCustomCheckBox());
        clickOn(newCB2);
        clickOn(newCB2);
        boolean result = newCB2.isSelected();
        Assert.assertTrue("Expected True",result);
    }

    // successfully moved onto the next page
    // move im on the numbers pane
    // click("") brings it back to the middle of the window to be clicked
    // clickOn() if I just wanna click where the cursor is currently on
    // moveBy(x,y) to move x pixels right(+) or left(-) and y pixels up(-) or down(+)

    @Test
    // increment buttons
    public void testC_IDB1() {
        clickOn(LabeledMatchers.hasText("Next"));
        Spinner <Integer> newButtonSpinner = lookfor(CreateConfigWizardTestingNodes.NumbersPane.getIdNumButtonsSpinner());
        moveTo(newButtonSpinner);
        moveBy(70,-5);
        for (int i=0; i<7; i++) {
            clickOn();
        }
        assertThat(newButtonSpinner.getValue(), is(equalTo(8)));
    }

    @Test
    // decrement buttons (IDB = increment/decrement buttons test)
    public void testC_IDB2() {
        Spinner <Integer> newButtonSpinner = lookfor(CreateConfigWizardTestingNodes.NumbersPane.getIdNumButtonsSpinner());
        moveBy(0, 10);
        for (int i=0; i<2; i++) {
            clickOn();
        }
        assertThat(newButtonSpinner.getValue(), is(equalTo(6)));
    }
    // The next tests aren't working...  ---> not in lexicographic order

    @Test
    // increment number of sets (increment/decrement sets)
    public void testD_IDS1() {
        Spinner <Integer> newSetSpinner = lookfor(CreateConfigWizardTestingNodes.NumbersPane.getIdNumSetsSpinner());
        moveBy(0,23);
        for (int i=0; i<6; i++) {
            clickOn();
        }
        assertThat(newSetSpinner.getValue(), is(equalTo(7)));
    }
    //
    @Test
    // decrement number of sets
    public void testD_IDS2() {
        Spinner <Integer> newSetSpinner = lookfor(CreateConfigWizardTestingNodes.NumbersPane.getIdNumSetsSpinner());
        moveBy(0,10);
        for (int i=0; i<3; i++) {
            clickOn();
        }
        assertThat(newSetSpinner.getValue(), is(equalTo(4)));
    }

    @Test
    // Go Back Forward test1
    public void testE_GBF1() {
        clickOn(LabeledMatchers.hasText("Previous"));
        clickOn(LabeledMatchers.hasText("Next"));
        clickOn(LabeledMatchers.hasText("Next"));
    }

    @Test
    // Checking Storing Directory
    public void testF_CSD1() {
        Button newButton = lookfor(CreateConfigWizardTestingNodes.FilePane.getIdFileTextfield());
        clickOn(newButton);
        push(KeyCode.ENTER);
        Assertions.assertThat(newButton).hasText("Choose");
    }

    @Test
    // Check File Exists (and confirms the overwriting of a file)
    public void testG_CFE1() {
        moveBy(-30,45);
        clickOn();
        // no overwriting file here...
        try {
            FlowPane newFlowPane = lookfor(ConfigStageTestingNodes.BUTTONS_FLOWPANE);
        }
        // confirms the overwriting of file
        catch (Exception e){
            clickOn();
        }
    }


    @Test
    // Set Text-To-Speech test1
    public void testH_TTS0() {
        // check if the buttons exists
        FlowPane newFlowPane = lookfor(ConfigStageTestingNodes.BUTTONS_FLOWPANE);
        //goes to the first button and clicks it
        clickOn(LabeledMatchers.hasText("Empty"));
    }

    @Test
    public void testH_TTS1() {
        // click on the record radio button and then the tts radio button
        RadioButton newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.RECORD_BUTTON);
        clickOn(newRadioButton);
        newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.WAV_BUTTON);
        clickOn(newRadioButton);
        newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.TTS_BUTTON);
        clickOn(newRadioButton);
    }

    @Test
    // Set Text-To-Speech test2
    public void testH_TTS2() {
        clickOn(LabeledMatchers.hasText("Next"));
        TextField newTextField = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSPHRASE_TEXTFIELD);
        Button newButton = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSPLAY_BUTTON);
        moveTo(newTextField);
        clickOn();
        writeText("Hello World");
        clickOn(newButton);
        // wait 5 seconds
        sleep(5,TimeUnit.SECONDS);
    }

    @Test
    // Set Text-To-Speech test3 and play it in the configurator
    public void testH_TTS3() {
        clickOn(LabeledMatchers.hasText("Next"));
        TextField newTextField = lookfor(AddWizardTestingNodes.NamePaneNodes.AUDIONAME_TEXTFIELD);
        clickOn(newTextField);
        writeText("Hello World");
        clickOn(LabeledMatchers.hasText("Previous"));
        clickOn(LabeledMatchers.hasText("Next"));
        clickOn(LabeledMatchers.hasText("Next"));
        moveBy(0, 20);
        clickOn();
    }

    @Test
    // Play Audio Button 1
    public void testI_PAB1() {
        clickOn(LabeledMatchers.hasText("Hello World"));
        sleep(3, TimeUnit.SECONDS);
    }

    @Test
    // Save TTS
    public void testJ_STTS() {
        clickOn(LabeledMatchers.hasText("File"));
        clickOn(LabeledMatchers.hasText("Save"));
    }
    //Start Testing male voice 2
    // Set Text-To-Speech test1
    @Test
    public void testK_TTS0() {
        FlowPane newFlowPane = lookfor(ConfigStageTestingNodes.BUTTONS_FLOWPANE);
        //goes to the first button and clicks it
        moveTo(LabeledMatchers.hasText("Empty"));
        moveBy(115,0);
        clickOn();
        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){
        }
    }
    // click on the record radio button and then the tts radio button
    @Test//REPEAT
    public void testK_TTS1() {
        RadioButton newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.RECORD_BUTTON);
        clickOn(newRadioButton);
        newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.WAV_BUTTON);
        clickOn(newRadioButton);
        newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.TTS_BUTTON);
        clickOn(newRadioButton);

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){
        }
    }
    @Test
    // Set Text-To-Speech test2
    public void testK_TTS2() {
        clickOn(LabeledMatchers.hasText("Next"));
        TextField newTextField = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSPHRASE_TEXTFIELD);
        Button newButton = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSPLAY_BUTTON);
        ComboBox newComboBox = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSVOICE_COMBOBOX);
        clickOn(newComboBox);
        clickOn(LabeledMatchers.hasText("Male 2"));
        moveTo(newTextField);
        clickOn();
        writeText("Hello World 2");
        clickOn(newButton);

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }

    @Test
    // Set Text-To-Speech test3 and play it in the configurator
    public void testK_TTS3() {
        clickOn(LabeledMatchers.hasText("Next"));
        TextField newTextField = lookfor(AddWizardTestingNodes.NamePaneNodes.AUDIONAME_TEXTFIELD);
        clickOn(newTextField);
        writeText("Hello World 2");
        clickOn(LabeledMatchers.hasText("Previous"));
        clickOn(LabeledMatchers.hasText("Next"));
        clickOn(LabeledMatchers.hasText("Next"));
        moveBy(0, 15);
        clickOn();

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }

    @Test
    // Play Audio Button 2
    public void testL_PAB1() {
        try {
            clickOn(LabeledMatchers.hasText("Hello World 2"));
            sleep(5, TimeUnit.SECONDS);
        } catch (Exception e) {

        }

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }
    //End voice testing male 2
    //*************************************
    //Start voice testing male 3
    // Set Text-To-Speech test1
    @Test
    public void testN_TTS0() {
        FlowPane newFlowPane = lookfor(ConfigStageTestingNodes.BUTTONS_FLOWPANE);
        //goes to the first button and clicks it
        moveTo(LabeledMatchers.hasText("Empty"));
        moveBy(230,0);
        clickOn();
        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){
        }
    }
    // click on the record radio button and then the tts radio button
    @Test//REPEAT
    public void testN_TTS1() {
        RadioButton newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.RECORD_BUTTON);
        clickOn(newRadioButton);
        newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.WAV_BUTTON);
        clickOn(newRadioButton);
        newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.TTS_BUTTON);
        clickOn(newRadioButton);

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){
        }
    }

    @Test
    // Set Text-To-Speech test2
    public void testN_TTS2() {
        clickOn(LabeledMatchers.hasText("Next"));
        TextField newTextField = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSPHRASE_TEXTFIELD);
        Button newButton = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSPLAY_BUTTON);
        ComboBox newComboBox = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSVOICE_COMBOBOX);
        clickOn(newComboBox);
        clickOn(LabeledMatchers.hasText("Male 3"));
        moveTo(newTextField);
        clickOn();
        writeText("Hello World 3");
        clickOn(newButton);

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }

    @Test
    // Set Text-To-Speech test3 and play it in the configurator
    public void testN_TTS3() {
        clickOn(LabeledMatchers.hasText("Next"));
        TextField newTextField = lookfor(AddWizardTestingNodes.NamePaneNodes.AUDIONAME_TEXTFIELD);
        clickOn(newTextField);
        writeText("Hello World 3");
        clickOn(LabeledMatchers.hasText("Previous"));
        clickOn(LabeledMatchers.hasText("Next"));
        clickOn(LabeledMatchers.hasText("Next"));
        moveBy(0, 15);
        clickOn();

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }
    // Play Audio Button 3
    @Test
    public void testO_PAB1() {
        try {
            clickOn(LabeledMatchers.hasText("Hello World 3"));
            sleep(5, TimeUnit.SECONDS);
        } catch (Exception e) {

        }

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }
    //End Voice testing male 3
    //***************************************
    //Start voice testing female 1
    // Set Text-To-Speech test1
    @Test
    public void testP_TTS0() {
        FlowPane newFlowPane = lookfor(ConfigStageTestingNodes.BUTTONS_FLOWPANE);
        //goes to the first button and clicks it
        moveTo(LabeledMatchers.hasText("Empty"));
        moveBy(345,0);
        clickOn();
        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){
        }
    }
    // click on the record radio button and then the tts radio button
    @Test
    public void testP_TTS1() {
        RadioButton newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.RECORD_BUTTON);
        clickOn(newRadioButton);
        newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.WAV_BUTTON);
        clickOn(newRadioButton);
        newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.TTS_BUTTON);
        clickOn(newRadioButton);

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){
        }
    }

    @Test
    // Set Text-To-Speech test2
    public void testP_TTS2() {
        clickOn(LabeledMatchers.hasText("Next"));
        TextField newTextField = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSPHRASE_TEXTFIELD);
        Button newButton = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSPLAY_BUTTON);
        ComboBox newComboBox = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSVOICE_COMBOBOX);
        clickOn(newComboBox);
        clickOn(LabeledMatchers.hasText("Female 1"));
        moveTo(newTextField);
        clickOn();
        writeText("Hello World 4");
        clickOn(newButton);

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }

    @Test
    // Set Text-To-Speech test3 and play it in the configurator
    public void testP_TTS3() {
        clickOn(LabeledMatchers.hasText("Next"));
        TextField newTextField = lookfor(AddWizardTestingNodes.NamePaneNodes.AUDIONAME_TEXTFIELD);
        clickOn(newTextField);
        writeText("Hello World 4");
        clickOn(LabeledMatchers.hasText("Previous"));
        clickOn(LabeledMatchers.hasText("Next"));
        clickOn(LabeledMatchers.hasText("Next"));
        moveBy(0, 15);
        clickOn();

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }
    // Play Audio Button 4
    @Test
    public void testQ_PAB1() {
        try {
            clickOn(LabeledMatchers.hasText("Hello World 4"));
            sleep(5, TimeUnit.SECONDS);
        } catch (Exception e) {

        }

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }
    //End voice  testing female 1
    //***************************************
    //Start voice testing female 2
    // Set Text-To-Speech test1
    @Test
    public void testR_TTS0() {
        FlowPane newFlowPane = lookfor(ConfigStageTestingNodes.BUTTONS_FLOWPANE);
        //goes to the first button and clicks it
        moveTo(LabeledMatchers.hasText("Empty"));
        moveBy(460,0);
        clickOn();
        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){
        }
    }
    // click on the record radio button and then the tts radio button
    @Test
    public void testR_TTS1() {
        RadioButton newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.RECORD_BUTTON);
        clickOn(newRadioButton);
        newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.WAV_BUTTON);
        clickOn(newRadioButton);
        newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.TTS_BUTTON);
        clickOn(newRadioButton);

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){
        }
    }

    @Test
    // Set Text-To-Speech test2
    public void testR_TTS2() {
        clickOn(LabeledMatchers.hasText("Next"));
        TextField newTextField = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSPHRASE_TEXTFIELD);
        Button newButton = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSPLAY_BUTTON);
        ComboBox newComboBox = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSVOICE_COMBOBOX);
        clickOn(newComboBox);
        clickOn(LabeledMatchers.hasText("Female 2"));
        moveTo(newTextField);
        clickOn();
        writeText("Hello World 5");
        clickOn(newButton);

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }

    @Test
    // Set Text-To-Speech test3 and play it in the configurator
    public void testR_TTS3() {
        clickOn(LabeledMatchers.hasText("Next"));
        TextField newTextField = lookfor(AddWizardTestingNodes.NamePaneNodes.AUDIONAME_TEXTFIELD);
        clickOn(newTextField);
        writeText("Hello World 5");
        clickOn(LabeledMatchers.hasText("Previous"));
        clickOn(LabeledMatchers.hasText("Next"));
        clickOn(LabeledMatchers.hasText("Next"));
        moveBy(0, 15);
        clickOn();

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }
    // Play Audio Button 5
    @Test
    public void testS_PAB1() {
        try {
            clickOn(LabeledMatchers.hasText("Hello World 5"));
            sleep(5, TimeUnit.SECONDS);
        } catch (Exception e) {

        }

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }
    //End voice testing female 2
    //********************************************
    //Start voice testing female 3
    // Set Text-To-Speech test1
    @Test
    public void testT_TTS0() {
        FlowPane newFlowPane = lookfor(ConfigStageTestingNodes.BUTTONS_FLOWPANE);
        //goes to the first button and clicks it
        moveTo(LabeledMatchers.hasText("Empty"));
        moveBy(575,0);
        clickOn();
        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){
        }
    }
    // click on the record radio button and then the tts radio button
    @Test
    public void testT_TTS1() {
        RadioButton newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.RECORD_BUTTON);
        clickOn(newRadioButton);
        newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.WAV_BUTTON);
        clickOn(newRadioButton);
        newRadioButton = lookfor(AddWizardTestingNodes.IntroPaneNodes.TTS_BUTTON);
        clickOn(newRadioButton);

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){
        }
    }

    @Test
    // Set Text-To-Speech test2
    public void testT_TTS2() {
        clickOn(LabeledMatchers.hasText("Next"));
        TextField newTextField = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSPHRASE_TEXTFIELD);
        Button newButton = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSPLAY_BUTTON);
        ComboBox newComboBox = lookfor(AddWizardTestingNodes.TTSPaneNodes.TTSVOICE_COMBOBOX);
        clickOn(newComboBox);
        clickOn(LabeledMatchers.hasText("Female 3"));
        moveTo(newTextField);
        clickOn();
        writeText("Hello World 6");
        clickOn(newButton);

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }

    @Test
    // Set Text-To-Speech test3 and play it in the configurator
    public void testT_TTS3() {
        clickOn(LabeledMatchers.hasText("Next"));
        TextField newTextField = lookfor(AddWizardTestingNodes.NamePaneNodes.AUDIONAME_TEXTFIELD);
        clickOn(newTextField);
        writeText("Hello World 6");
        clickOn(LabeledMatchers.hasText("Previous"));
        clickOn(LabeledMatchers.hasText("Next"));
        clickOn(LabeledMatchers.hasText("Next"));
        moveBy(0, 15);
        clickOn();

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }
    // Play Audio Button 6
    @Test
    public void testU_PAB1() {
        try {
            clickOn(LabeledMatchers.hasText("Hello World 6"));
            sleep(5, TimeUnit.SECONDS);
        } catch (Exception e) {

        }

        try {
            TimeUnit.SECONDS.sleep(1);
        }
        catch(Exception e){

        }
    }

    @Test
    // Save TTS
    public void testV_SSTS() {
        clickOn(LabeledMatchers.hasText("File"));
        clickOn(LabeledMatchers.hasText("Save"));
    }

}