package io.pavan.movieapp;

import android.os.Handler;
import android.os.Looper;

import org.mockito.Matchers;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by pavan on 02/12/18
 */
@PrepareForTest({Looper.class})
public class AndroidMockUtil {

    private AndroidMockUtil() {}
    /**
     * Mocks main thread handler post() and postDelayed() for use in Android unit tests
     *
     * To use this:
     * <ol>
     *     <li>Call this method in an {@literal @}Before method of your test.</li>
     *     <li>Place Looper.class in the {@literal @}PrepareForTest annotation before your test class.</li>
     *     <li>any class under test that needs to call {@code new Handler(Looper.getMainLooper())} should be placed
     *     in the {@literal @}PrepareForTest annotation as well.</li>
     * </ol>
     *
     * @throws Exception
     */
    public static void mockMainThreadHandler() throws Exception {
        PowerMockito.mockStatic(Looper.class);
        Looper mockMainThreadLooper = mock(Looper.class);
        when(Looper.getMainLooper()).thenReturn(mockMainThreadLooper);
        Handler mockMainThreadHandler = mock(Handler.class);
        Answer<Boolean> handlerPostAnswer = new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) throws Throwable {
                Runnable runnable = invocation.getArgumentAt(0, Runnable.class);
                Long delay = 0L;
                if (invocation.getArguments().length > 1) {
                    delay = invocation.getArgumentAt(1, Long.class);
                }
                if (runnable != null) {
                    mainThread.schedule(runnable, delay, TimeUnit.MILLISECONDS);
                }
                return true;
            }
        };
        doAnswer(handlerPostAnswer).when(mockMainThreadHandler).post(
                Matchers.any(Runnable.class));
        doAnswer(handlerPostAnswer).when(mockMainThreadHandler).postDelayed(
                Matchers.any(Runnable.class), Matchers.anyLong());
        PowerMockito.whenNew(Handler.class).withArguments(mockMainThreadLooper).thenReturn(mockMainThreadHandler);
    }

    private final static ScheduledExecutorService
            mainThread = Executors.newSingleThreadScheduledExecutor();
}
