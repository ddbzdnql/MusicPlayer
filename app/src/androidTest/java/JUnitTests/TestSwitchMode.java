package JUnitTests;


import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.cse110.team28.flashbackmusicplayer.MediaViewActivity;
import com.cse110.team28.flashbackmusicplayer.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class TestSwitchMode {

    @Rule
    public ActivityTestRule<MediaViewActivity> mActivityTestRule = new ActivityTestRule<>(MediaViewActivity.class);

    @Test
    public void testSwitchMode() {

        ViewInteraction switch_ = onView(
                allOf(ViewMatchers.withId(R.id.changeSwitch),
                        childAtPosition(
                                allOf(withId(R.id.app_bar_switch),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.support.v7.widget.LinearLayoutCompat.class),
                                                0)),
                                0),
                        isDisplayed()));
        switch_.check(matches(isDisplayed()));

        ViewInteraction switch_2 = onView(
                allOf(withId(R.id.changeSwitch),
                        childAtPosition(
                                allOf(withId(R.id.app_bar_switch),
                                        childAtPosition(
                                                withClassName(is("android.support.v7.widget.ActionMenuView")),
                                                0)),
                                0),
                        isDisplayed()));
        switch_2.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(3579);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction imageView = onView(
                allOf(withId(R.id.songPicture),
                        childAtPosition(
                                allOf(withId(R.id.ll),
                                        childAtPosition(
                                                IsInstanceOf.<View>instanceOf(android.view.ViewGroup.class),
                                                0)),
                                0),
                        isDisplayed()));
        imageView.check(matches(isDisplayed()));

    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}