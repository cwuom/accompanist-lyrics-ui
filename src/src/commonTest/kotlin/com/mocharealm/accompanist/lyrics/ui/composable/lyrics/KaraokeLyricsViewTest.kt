package com.mocharealm.accompanist.lyrics.ui.composable.lyrics

import org.junit.Assert.assertEquals
import org.junit.Test

class KaraokeLyricsViewTest {

    @Test
    fun `resolveFocusedLineScrollMode snaps only on initial layout`() {
        assertEquals(
            FocusedLineScrollMode.Snap,
            resolveFocusedLineScrollMode(previousAutoScrollIndex = null, targetIndex = 6)
        )
        assertEquals(
            FocusedLineScrollMode.Animate,
            resolveFocusedLineScrollMode(previousAutoScrollIndex = 5, targetIndex = 6)
        )
        assertEquals(
            FocusedLineScrollMode.Animate,
            resolveFocusedLineScrollMode(previousAutoScrollIndex = 5, targetIndex = 42)
        )
    }

    @Test
    fun `resolveFocusedLinePlacementSuppressionMs keeps jump transitions suppressed longer`() {
        assertEquals(
            80L,
            resolveFocusedLinePlacementSuppressionMs(previousAutoScrollIndex = null, targetIndex = 6)
        )
        assertEquals(
            0L,
            resolveFocusedLinePlacementSuppressionMs(previousAutoScrollIndex = 5, targetIndex = 6)
        )
        assertEquals(
            220L,
            resolveFocusedLinePlacementSuppressionMs(previousAutoScrollIndex = 5, targetIndex = 42)
        )
        assertEquals(
            0L,
            resolveFocusedLinePlacementSuppressionMs(previousAutoScrollIndex = 6, targetIndex = 6)
        )
    }

    @Test
    fun `shouldSuppressLinePlacementAnimation only disables placement for manual scroll or explicit suppression`() {
        assertEquals(
            true,
            shouldSuppressLinePlacementAnimation(
                isManualScrolling = true,
                suppressPlacementAnimation = false
            )
        )
        assertEquals(
            true,
            shouldSuppressLinePlacementAnimation(
                isManualScrolling = false,
                suppressPlacementAnimation = true
            )
        )
        assertEquals(
            false,
            shouldSuppressLinePlacementAnimation(
                isManualScrolling = false,
                suppressPlacementAnimation = false
            )
        )
    }

    @Test
    fun `shouldSuppressFocusedLinePlacementAnimation keeps offscreen animated recovery stable`() {
        assertEquals(
            true,
            shouldSuppressFocusedLinePlacementAnimation(
                useManualViewportRecenter = true,
                animateViewportScroll = false,
                placementSuppressionMs = 0L,
                targetItemVisible = true,
                scrollMode = FocusedLineScrollMode.Animate
            )
        )
        assertEquals(
            true,
            shouldSuppressFocusedLinePlacementAnimation(
                useManualViewportRecenter = false,
                animateViewportScroll = true,
                placementSuppressionMs = 0L,
                targetItemVisible = true,
                scrollMode = FocusedLineScrollMode.Animate
            )
        )
        assertEquals(
            true,
            shouldSuppressFocusedLinePlacementAnimation(
                useManualViewportRecenter = false,
                animateViewportScroll = false,
                placementSuppressionMs = 0L,
                targetItemVisible = false,
                scrollMode = FocusedLineScrollMode.Animate
            )
        )
        assertEquals(
            false,
            shouldSuppressFocusedLinePlacementAnimation(
                useManualViewportRecenter = false,
                animateViewportScroll = false,
                placementSuppressionMs = 0L,
                targetItemVisible = true,
                scrollMode = FocusedLineScrollMode.Animate
            )
        )
    }

    @Test
    fun `manual viewport recenter waits for next focused line before starting`() {
        assertEquals(
            true,
            shouldWaitForManualViewportRecenter(
                manualViewportRecenterPending = true,
                manualViewportRecenterTriggerIndex = 8,
                animateViewportScroll = false,
                currentFocusIndex = 8
            )
        )
        assertEquals(
            true,
            shouldStartManualViewportRecenter(
                manualViewportRecenterPending = true,
                manualViewportRecenterTriggerIndex = 8,
                animateViewportScroll = false,
                currentFocusIndex = 9
            )
        )
        assertEquals(
            false,
            shouldWaitForManualViewportRecenter(
                manualViewportRecenterPending = false,
                manualViewportRecenterTriggerIndex = 8,
                animateViewportScroll = false,
                currentFocusIndex = 8
            )
        )
    }

    @Test
    fun `manual viewport recenter durations stay below one second and non linear friendly`() {
        assertEquals(260, resolveManualViewportRecenterDurationMs(0f))
        assertEquals(299, resolveManualViewportRecenterDurationMs(300f))
        assertEquals(536, resolveManualViewportRecenterDurationMs(1_200f))
        assertEquals(760, resolveManualViewportRecenterDurationMs(5_000f))
        assertEquals(120, resolveManualViewportRecenterCorrectiveDurationMs(0f))
        assertEquals(120, resolveManualViewportRecenterCorrectiveDurationMs(300f))
        assertEquals(180, resolveManualViewportRecenterCorrectiveDurationMs(5_000f))
    }

    @Test
    fun `shouldAnimateVisibleFocusedLineScroll only animates large visible seek jumps`() {
        assertEquals(
            false,
            shouldAnimateVisibleFocusedLineScroll(
                previousAutoScrollIndex = null,
                targetIndex = 6
            )
        )
        assertEquals(
            false,
            shouldAnimateVisibleFocusedLineScroll(
                previousAutoScrollIndex = 5,
                targetIndex = 6
            )
        )
        assertEquals(
            true,
            shouldAnimateVisibleFocusedLineScroll(
                previousAutoScrollIndex = 5,
                targetIndex = 42
            )
        )
    }

    @Test
    fun `resolveFocusedLineViewportDelta keeps target line anchored to configured viewport offset`() {
        assertEquals(
            0f,
            resolveFocusedLineViewportDelta(
                itemOffset = 420,
                viewportStartOffset = 0,
                stableOffsetPx = 300,
                keepAliveZonePx = 120f
            ),
            0.001f
        )
        assertEquals(
            24f,
            resolveFocusedLineViewportDelta(
                itemOffset = 444,
                viewportStartOffset = 0,
                stableOffsetPx = 300,
                keepAliveZonePx = 120f
            ),
            0.001f
        )
    }

    @Test
    fun `shouldRealignFocusedLineAfterLayout skips adjacent line transitions`() {
        assertEquals(
            true,
            shouldRealignFocusedLineAfterLayout(previousAutoScrollIndex = null, targetIndex = 6)
        )
        assertEquals(
            false,
            shouldRealignFocusedLineAfterLayout(previousAutoScrollIndex = 5, targetIndex = 6)
        )
        assertEquals(
            true,
            shouldRealignFocusedLineAfterLayout(previousAutoScrollIndex = 5, targetIndex = 42)
        )
        assertEquals(
            false,
            shouldRealignFocusedLineAfterLayout(previousAutoScrollIndex = 6, targetIndex = 6)
        )
    }
}
