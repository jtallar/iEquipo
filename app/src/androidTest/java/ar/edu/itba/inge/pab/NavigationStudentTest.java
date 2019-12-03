package ar.edu.itba.inge.pab;



import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class NavigationStudentTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<>(LoginActivity.class);
    @Test
    public void navigationStudentTest() {
        //checkear que el navigation explore te lleve al explore
        onView(allOf(withId(R.id.navigation_explore))).perform(click());
        onView(allOf(withId(R.id.rv_explore))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //checkear que el navigation notifications te lleve al notifications
        onView(allOf(withId(R.id.navigation_notifications))).perform(click());
        onView(allOf(withId(R.id.rv_notifications))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));

        //checkear que el navigation myactivities te lleve al myactivities
        onView(allOf(withId(R.id.navigation_projects))).perform(click());
        onView(allOf(withId(R.id.rv_projects))).check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }
}
