package net.x4a42.volksempfaenger.ui.addsubscription;

import android.content.Context;
import android.content.Intent;

import net.x4a42.volksempfaenger.IntentBuilder;
import net.x4a42.volksempfaenger.ui.AddSubscriptionActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class AddSubscriptionActivityIntentProviderTest
{
    @Mock Context                         context;
    @Mock IntentBuilder                   intentBuilder;
    @Mock Intent                          intent;
    AddSubscriptionActivityIntentProvider intentProvider;

    @Before
    public void setUp() throws Exception
    {
        Mockito.when(intentBuilder.build(context, AddSubscriptionActivity.class))
               .thenReturn(intent);
        intentProvider = new AddSubscriptionActivityIntentProvider(context, intentBuilder);
    }

    @Test
    public void get() throws Exception
    {
        Intent result = intentProvider.get();
        assertEquals(result, intent);
    }
}