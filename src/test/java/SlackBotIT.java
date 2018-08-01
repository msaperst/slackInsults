import com.coveros.selenified.Locator;
import com.coveros.selenified.Selenified;
import com.coveros.selenified.application.App;
import com.coveros.selenified.element.Element;
import org.testng.ITestContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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

    @Test(description = "Add Random Phrases")
    public void sampleTest() {
        // use this object to manipulate the app
        App app = this.apps.get();
        // verify the title
        login(app);
        String word = getRandomWord(app);
        String phrase = getRandomInsult(app);
        app.newElement(Locator.ID, "add_response").click();
        Element newSet = app.newElement(Locator.CLASSNAME, "slackbot_response_fieldset");
        newSet.setMatch(newSet.get().matchCount() - 2);   //for some reason, 2 is the magic number
        newSet.findChild(app.newElement(Locator.CLASSNAME, "triggers_entry")).type(word);
        newSet.findChild(app.newElement(Locator.CLASSNAME, "responses_entry")).type(phrase);
        newSet.findChild(app.newElement(Locator.CLASSNAME, "save_response_button")).click();
        Element saved = newSet.findChild(app.newElement(Locator.XPATH,
                "//button[contains(@class,'save_response_button')]/span[text" + "()='Saved']"));
        saved.waitFor().displayed();
        saved.assertState().displayed();

        // close out the test
        finish();
    }

    public void login(App app) {
        app.newElement(Locator.ID, "email").type(System.getProperty("slack.username"));
        app.newElement(Locator.ID, "password").type(System.getProperty("slack.password"));
        app.newElement(Locator.ID, "signin_btn").click();
    }

    public String getRandomWord(App app) {
        app.openNewWindow("https://randomword.com/");
        String word = app.newElement(Locator.ID, "random_word").get().text();
        app.closeCurrentWindow();
        app.switchToParentWindow();
        return word;
    }

    public String getRandomInsult(App app) {
        app.openNewWindow("http://www.robietherobot.com/insult-generator.htm");
        String phrase = app.newElement(Locator.TAGNAME, "h1", 1).get().text().trim();
        app.closeCurrentWindow();
        app.switchToParentWindow();

        Random random = new Random();
        return users.get(random.nextInt(users.size())) + " is a " + phrase;
    }
}
