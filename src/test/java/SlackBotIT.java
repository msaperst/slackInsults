import com.coveros.selenified.Locator;
import com.coveros.selenified.Selenified;
import com.coveros.selenified.application.App;
import com.coveros.selenified.element.Element;
import com.google.gson.JsonParser;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.*;

public class SlackBotIT extends Selenified {

    // Need to figure out how to dynamically generate this
    List<String> users = new ArrayList();

    @BeforeClass(alwaysRun = true)
    public void beforeClass(ITestContext test) {
        // set the base URL for the tests here
        setTestSite(this, test,
                "https://" + System.getProperty("slack.workspace") + ".slack" + ".com/?redir=%2Fcustomize%2Fslackbot");

        users.add("Adam");
        users.add("Byron");
        users.add("Jason");
        users.add("Jon");
        users.add("Jonathan Miller Kauffman");
        users.add("Rich");
        users.add("Ryan");
        users.add("Wade");
    }

    @Test(description = "Add Random Insult")
    public void addRandomInsult() {
        App app = this.apps.get();
        login(app);
        String word = getRandomWord(app);
        String phrase = getRandomInsult(app);
        addResponse(app, word, phrase);
        finish();
    }

    @Test(description = "Add Random Nutscapes")
    public void addRandomNutscapes() {
        App app = this.apps.get();
        login(app);
        String word = getRandomWord(app, new Pair("n", "35"));
        String phrase = getRandomNutscape(app);
        addResponse(app, word, phrase);
        finish();
    }

    private void login(App app) {
        app.newElement(Locator.ID, "email").type(System.getProperty("slack.username"));
        app.newElement(Locator.ID, "password").type(System.getProperty("slack.password"));
        app.newElement(Locator.ID, "signin_btn").click();
    }

    private void addResponse(App app, String trigger, String response) {
        app.newElement(Locator.ID, "add_response").click();
        Element newSet = app.newElement(Locator.CLASSNAME, "slackbot_response_fieldset");
        newSet.setMatch(newSet.get().matchCount() - 2);   //for some reason, 2 is the magic number
        newSet.findChild(app.newElement(Locator.CLASSNAME, "triggers_entry")).type(trigger);
        newSet.findChild(app.newElement(Locator.CLASSNAME, "responses_entry")).type(response);
        newSet.findChild(app.newElement(Locator.CLASSNAME, "save_response_button")).click();
        Element saved = newSet.findChild(app.newElement(Locator.XPATH,
                "//button[contains(@class,'save_response_button')]/span[text" + "()='Saved']"));
        saved.waitFor().displayed();
        saved.assertState().displayed();
    }

    private String getRandomWord(App app) {
        app.openNewWindow("https://randomword.com/");
        String word = app.newElement(Locator.ID, "random_word").get().text();
        app.closeCurrentWindow();
        app.switchToParentWindow();
        return word;
    }

    private String getRandomInsult(App app) {
        app.openNewWindow("http://www.robietherobot.com/insult-generator.htm");
        String phrase = app.newElement(Locator.TAGNAME, "h1", 1).get().text().trim();
        app.closeCurrentWindow();
        app.switchToParentWindow();

        Random random = new Random();
        return users.get(random.nextInt(users.size())) + " is a " + phrase;
    }

    private String getRandomWord(App app, Pair word) {
        app.openNewWindow("http://watchout4snakes.com/wo4snakes/Random/RandomWordPlus");
        String initialWord = app.newElement(Locator.ID, "result").get().text();
        app.newElement(Locator.ID, "Pos").selectValue((String) word.getL());
        app.newElement(Locator.ID, "Level").selectValue((String) word.getR());
        app.newElement(Locator.TAGNAME, "input", 1).click();
        String newWord = app.newElement(Locator.ID, "result").get().text();
        while (initialWord.equals(newWord)) {
            newWord = app.newElement(Locator.ID, "result").get().text();
        }
        app.closeCurrentWindow();
        app.switchToParentWindow();
        return newWord;
    }

    private String getRandomPhrase(App app, Pair... words) {
        app.openNewWindow("http://watchout4snakes.com/wo4snakes/random/randomphrase");
        String initialPhrase = app.newElement(Locator.ID, "result").get().text();
        for (int i = 1; i <= words.length; i++) {
            Pair word = words[i - 1];
            app.newElement(Locator.ID, "Pos" + i).selectValue((String) word.getL());
            app.newElement(Locator.ID, "Level" + i).selectValue((String) word.getR());
        }
        app.newElement(Locator.TAGNAME, "input", 1).click();
        String newPhrase = app.newElement(Locator.ID, "result").get().text();
        while (initialPhrase.equals(newPhrase)) {
            newPhrase = app.newElement(Locator.ID, "result").get().text();
        }
        app.closeCurrentWindow();
        app.switchToParentWindow();
        return newPhrase;
    }

    private String getRandomNutscape(App app) {
        app.openNewWindow("https://www.google.com/");
        Element search = app.newElement(Locator.NAME, "q");
        search.type("nutscape");
        search.submit();
        app.newElement(Locator.ID, "ires").waitFor().displayed();
        app.newElement(Locator.LINKTEXT, "Images").click();
        Element element = app.newElement(Locator.CLASSNAME, "rg_bx");
        element.waitFor().displayed();
        element.setMatch(new Random().nextInt(element.get().matchCount()));
        Element metaData = element.findChild(app.newElement(Locator.CLASSNAME, "rg_meta"));
        JsonParser parser = new JsonParser();
        String imageLink =
                parser.parse(metaData.get().attribute("innerHTML")).getAsJsonObject().get("ou").getAsString();
        app.closeCurrentWindow();
        app.switchToParentWindow();
        return imageLink;
    }

    /**
     * A pair of objects used for generating words
     *
     * @param <L>
     * @param <R>
     */
    private class Pair<L, R> {
        private L l;
        private R r;

        public Pair(L l, R r) {
            this.l = l;
            this.r = r;
        }

        public L getL() {
            return l;
        }

        public R getR() {
            return r;
        }

        public void setL(L l) {
            this.l = l;
        }

        public void setR(R r) {
            this.r = r;
        }
    }
}
