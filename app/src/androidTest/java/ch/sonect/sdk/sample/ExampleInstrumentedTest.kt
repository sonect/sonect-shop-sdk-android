package ch.sonect.sdk.shop.integrationapp

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    @Rule
    @JvmField
    val rule = IntentsTestRule(MainActivity::class.java)

    @Test
    fun sampleStartsWithProperTextOnButton() {
        Espresso.onView(withId(R.id.btnStartSdkFragment))
            .check(matches(withText("Start SDK, NO TOKEN")))
    }
}
