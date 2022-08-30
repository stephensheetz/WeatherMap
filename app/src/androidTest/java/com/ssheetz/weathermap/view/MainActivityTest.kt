package com.ssheetz.weathermap.view

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.ssheetz.weathermap.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(MainActivity::class.java)


    @Test
    fun testClickMapPopulatesForecasts() {
        // Dismiss instructions dialog
        Espresso.pressBack()

        onView(withId(R.id.mapbox_view))
            .perform(ViewActions.click())

        Thread.sleep(3000)

        onView(withId(R.id.recyclerView_results))
            .check(matches(CustomMatchers.withMinItemCount(5)))
    }

    class CustomMatchers {
        companion object {
            fun withMinItemCount(count: Int): Matcher<View> {
                return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
                    override fun describeTo(description: Description?) {
                        description?.appendText("RecyclerView with item count: $count")
                    }

                    override fun matchesSafely(item: RecyclerView?): Boolean {
                        return item?.adapter?.itemCount ?: 0 >= count
                    }
                }
            }
        }
    }
}